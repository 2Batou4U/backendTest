package de.froschcraft;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Router for POST requests.
 */
public class POSTRouter {
    /**
     * Route request based on URL.
     * @param bodyMap Contains body arguments parsed into a Map.
     * @param uriParser For Metadata.
     * @param serverData Contains data like messages, etc.
     */
    public static void routePath(
            Map<String, String> bodyMap,
            URIParser uriParser,
            ServerData serverData
    ) {
        System.out.println(uriParser.getPath());
        switch (uriParser.getPath()) {
            case "/user":
                System.out.println("User.");
                break;
            case "/message":
                if (
                        bodyMap.containsKey("message") && bodyMap.containsKey("username") && uriParser.checkPath("/message")
                ) {
                    // Get user:
                    String username = bodyMap.get("username");
                    User user = serverData.getUser(username);

                    // Decode URL code.
                    String messageText = URLDecoder.decode(bodyMap.get("message"), StandardCharsets.UTF_8);
                    serverData.addMessage(new Message(messageText, user));
                }
                break;
            default:
                System.out.println("Default.");
                break;
        }
    }
}
