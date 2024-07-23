package de.froschcraft;

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

public class Main {
    private static final int PORT = 8100;
    private static final ServerState serverState = new ServerState();
    private static final Scanner scanner = new Scanner(System.in);
    private static Vector<Message> messages;

    public static void main(String[] args) {
        // Erstelle Thread für den Server und warte auf Nutzereingabe.
        Thread serverThread = new Thread(Main::startServer);
        serverThread.start();

        System.out.println("Server läuft und hört auf Eingaben.");

        while (serverState.isRunning()){
            String uIn = scanner.nextLine();
            serverState.stopIfCheck(uIn.equals("exit"));
        }

        stopServer();
    }

    private static void startServer() {
        // Hinzufügen eines Shutdown-Hooks
        Runtime.getRuntime().addShutdownHook(new Thread(Main::stopServer));

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            // Lies die Nachrichten in den Speicher, für späteren Zugriff durch Client.
            messages = ObjectSerializer.deserializeMessages("users.ser");
            serverState.setRunning();
            System.out.printf("Server läuft auf Port %d.%n", PORT);

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
            String requestLine = in.readLine();
            if (requestLine != null && !requestLine.isEmpty()) {
                System.out.printf("Anfrage erhalten: %s%n", requestLine);
                parseRequest(requestLine);
                sendResponse(out);
            }
        } catch (IOException e) {
            System.out.println("IO-Streams konnten nicht gelesen werden.");
        }
    }

    private static void parseRequest(String requestLine) {
        URIParser uriParser = new URIParser(requestLine);

        switch (uriParser.getMethod()) {
            case "POST":
                Map<String, String> params = uriParser.getQueryParams();

                String message = params.get("message");
                if (message != null && !message.isEmpty()) {
                    messages.add(new Message(params.get("message")));
                }

                break;
            case "GET":
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
        out.println("<form method=\"POST\" action=\"/submit\"> <input type=\"text\" name=\"message\" value=\"\"> <input type=\"submit\" value=\"Post\"> </form>");
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
