package de.froschcraft;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.PatternSyntaxException;

import static java.lang.System.exit;

public class Main {
    private static final int PORT = 8100;
    private static final ServerState serverState = new ServerState();
    private static final Scanner scanner = new Scanner(System.in);
    private static Vector<Message> messages;

    private static CommandHandler commandHandler = new CommandHandler(serverState);

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

    private static void startServer() {
        // Hinzufügen eines Shutdown-Hooks
        Runtime.getRuntime().addShutdownHook(new Thread(Main::stopServer));

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            // Lies die Nachrichten in den Speicher, für späteren Zugriff durch Client.
            messages = ObjectSerializer.deserializeMessages("users.ser");
            serverState.setRunning();
            System.out.printf("Server läuft auf http://localhost:%d.%n", PORT);

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

    private static void handleClient(Socket socket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), false)
        ) {

            Map<String, String> requestMap = new HashMap<>();

            // Get the initial Request info.
            String requestLine = in.readLine();
            requestMap.put("Request", requestLine);

            while ((requestLine = in.readLine()) != null && !requestLine.isEmpty()) {
                // Get the remaining info.
                String[] requestParts = requestLine.split(":");

                requestMap.put(requestParts[0].strip(), requestParts[1].strip());
            }

            System.out.printf("Anfrage erhalten: %s%n", requestMap.get("Request"));

            parseRequest(requestMap, in, out);


        } catch (IOException e) {
            System.out.println("IO-Streams konnten nicht gelesen werden.");
        }
    }

    private static void parseRequest(Map<String, String> requestMap, BufferedReader in, PrintWriter out) {
        URIParser uriParser;

        try {
            uriParser = new URIParser(requestMap.get("Request"));
        } catch (NullPointerException e) {
            System.out.println("Anfrage konnte nicht bearbeitet werden.");
            return;
        }


        switch (uriParser.getMethod()) {
            case "POST":
                // Fetch the body.
                int contentLength = Integer.parseInt(requestMap.get("Content-Length"));

                if (contentLength > 0) {
                    char[] buffer = new char[contentLength];

                    try {
                        in.read(buffer, 0, contentLength);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    String bodyString = String.valueOf(buffer);

                    Map <String, String> bodyMap = new HashMap<>();
                    for (String bodyParam: bodyString.split("&")) {
                        String[] keyValue = bodyParam.split("=");
                        bodyMap.put(keyValue[0], keyValue[1]);
                    }

                    if (bodyMap.containsKey("message")) {
                        messages.add(new Message(bodyMap.get("message")));
                    }
                }

                sendResponse(out);
                break;
            case "GET":
                sendResponse(out);
                break;
        }
    }

    private static void sendResponse(PrintWriter out) {
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println();
        out.println("<html><body>");
        out.println("<h1>Hello, World!</h1>");

        for (Message message : messages){
            out.println("<p>" + message.toString() + "</p>");
        }

        out.println("<form method=\"POST\" action=\"/message\"> <input type=\"text\" name=\"message\" value=\"\"> <input type=\"submit\" value=\"Post\"> </form>");
        out.println("</body></html>");
        out.flush();
    }

    private static void stopServer() {
        serverState.setStopping();

        ObjectSerializer.serializeMessages(messages, "users.ser");

        try {
            new Socket("localhost", PORT).close();
        } catch (IOException e) {
            System.out.println("Socket konnte nicht geschlossen werden.");
        }
    }
}
