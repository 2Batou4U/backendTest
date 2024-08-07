package de.froschcraft;

import java.io.*;
import java.net.*;
import java.util.*;

import static java.lang.System.exit;

public class Main {
    private static final int PORT = 8100;
    private static final ServerState serverState = new ServerState();
    private static final Scanner scanner = new Scanner(System.in);

    private static ServerData serverData;

    private static final CommandHandler commandHandler = new CommandHandler(serverState);

    /**
     * Main function. Creates threads, etc.
     * @param args Optional startup arguments.
     */
    public static void main(String[] args) {
        // Erstelle Thread für den Server und warte auf Nutzereingabe.
        Thread serverThread = new Thread(Main::startServer);
        serverThread.start();

        System.out.println("Server läuft und hört auf Eingaben.");

        do {
            commandHandler.handleInput(scanner.nextLine());
        } while (serverState.isRunning());

        exit(1);
    }

    /**
     * Manages startup tasks for the server.
     */
    private static void startServer() {
        // Add shutdown hook.
        Runtime.getRuntime().addShutdownHook(new Thread(Main::stopServer));

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            // Load messages and users.
            serverData = new ServerData(
                    ObjectSerializer.deserializeMessages("messages.ser"),
                    ObjectSerializer.deserializeUsers("users.ser")
            );

            // Create dummy data *if* objects are empty.
            serverData.dummyData();

            serverState.setRunning();

            // Console hint on successful startup.
            System.out.printf(
                    "Server läuft auf http://localhost:%d.%n",
                    PORT
            );

            // Main logic for console input.
            while (serverState.isRunning()) {
                try {

                    Socket socket = serverSocket.accept();
                    handleClient(socket);

                } catch (SocketException e) {
                    if (!serverState.isRunning()) {
                        System.out.println("Server wurde gestoppt.");
                    } else {
                        System.out.println("Am Socket trat ein unbekannter Fehler auf.");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Am Socket trat ein unbekannter Fehler auf.");
        }
    }

    /**
     * Handles client input and puts the data sent into a dictionary.
     * @param socket Socket connection for communication.
     */
    private static void handleClient(Socket socket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), false)
        ) {
            // Later contains the request data.
            Map<String, String> requestMap = new HashMap<>();

            // Get the initial Request info.
            String requestLine = in.readLine();
            requestMap.put("Request", requestLine);

            while ((requestLine = in.readLine()) != null && !requestLine.isEmpty()) {
                // Get the remaining info.
                String[] requestParts = requestLine.split(":");

                requestMap.put(
                        requestParts[0].strip(),
                        requestParts[1].strip()
                );
            }

            // Pass the data for further transformation.
            try {
                parseRequest(
                        requestMap,
                        in,
                        out
                );
            } catch (NullPointerException e) {
                System.out.println(e.getMessage());
            }



        } catch (IOException e) {
            System.out.println("IO-Streams konnten nicht gelesen werden.");
        }
    }

    /**
     * Parses a request and makes sense of client input.
     * @param requestMap A request dictionary, containing all data.
     * @param in Input stream where we get the data from.
     * @param out Output Stream where we send the data.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void parseRequest(
            Map<String, String> requestMap,
            BufferedReader in,
            PrintWriter out
    ) throws NullPointerException {
        // Try and parse the Request Header.
        URIParser uriParser;

        String request = Objects.requireNonNull(requestMap.get("Request"), "Anfrage konnte nicht bearbeitet werden");

        uriParser = new URIParser(request);
        System.out.printf(
                "Anfrage erhalten: %s%n",
                requestMap.get("Request")
        );

        // Check method.
        switch (uriParser.getMethod()) {
            case "POST":
                // Fetch the body if it exists.
                int contentLength = Integer.parseInt(requestMap.get("Content-Length"));

                if (contentLength <= 0) {
                    throw new IllegalArgumentException("POST request has to contain a body.");
                }

                // Create buffer and pass it to read() function so it can be filled with the actual body.
                char[] buffer = new char[contentLength];

                try {
                    in.read(
                            buffer,
                            0,
                            contentLength
                    );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // Convert the char array into string.
                Map<String, String> bodyMap = getStringStringMap(buffer);

                // Pass the request to buffer
                POSTRouter.routePath(bodyMap, uriParser, serverData);
                break;

            case "GET":
                GETRouter.routePath(uriParser);
                break;
        }
        sendResponse(out);
    }

    private static Map<String, String> getStringStringMap(char[] buffer) {
        String bodyString = String.valueOf(buffer);

        // Map the Request.
        Map <String, String> bodyMap = new HashMap<>();
        for (String bodyParam: bodyString.split("&")) {
            String[] keyValue = bodyParam.split("=");

            bodyMap.put(
                    keyValue[0],
                    keyValue[1]
            );
        }
        return bodyMap;
    }

    /**
     * Builds and sends response through the PrintWriter.
     * @param out PrintWriter object used for sending data to client.
     */
    private static void sendResponse(
            PrintWriter out
    ) {
        /*
         * This will get a rework in the future, right now it's just here for testing.
         */
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println();
        out.println("<html><body>");

        out.println("<h1>Login</h1>");

        out.println("<form method=\"GET\" action=\"/login\">");
        out.println("<input type=\"text\" name=\"username\" value=\"\">");
        out.println("<input type=\"password\" name=\"password\" value=\"\">");
        out.println("<input type=\"submit\" value=\"Anmelden\"> ");
        out.println("</form>");

        out.println("<h1>Message-Board</h1>");

        for (Message message : serverData.getMessages()){
            out.println("<p>" + message.toString() + "</p>");
        }

        out.println("<form method=\"POST\" action=\"/message\">");
        out.println("<input type=\"text\" name=\"message\" value=\"\"> <input type=\"submit\" value=\"Post\"> ");
        out.printf("<input type=\"hidden\" name=\"username\" value=\"%s\">%n", serverData.getUser("adrian"));
        out.println("</form>");
        out.println("</body></html>");
        out.flush();
    }

    /**
     * Referenced by ShutdownHook. Finishes all tasks and saves data into serialized files.
     */
    private static void stopServer() {
        serverState.setStopping();

        ObjectSerializer.serializeMessages(serverData.getMessages(), "messages.ser");
        ObjectSerializer.serializeUsers(serverData.getUsers(), "users.ser");
        try {
            new Socket(
                    "localhost",
                    PORT
            ).close();

        } catch (IOException e) {
            System.out.println("Socket konnte nicht geschlossen werden.");
        }
    }
}
