package de.froschcraft;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class URIParser {
    private String method;
    private URI uri;
    private String httpVersion;
    private Map<String, String> queryParams;

    public URIParser(String requestString) throws NullPointerException {
        String[] requestArray = requestString.split(" ");

        if (requestArray.length != 3) {
            throw new IllegalArgumentException("Anfrage ungültig.");
        }

        try {
            this.method = requestArray[0];
            this.uri = new URI(requestArray[1]);
            this.httpVersion = requestArray[2];

            this.queryParams = parseQueryParams(uri.getQuery());
        } catch (URISyntaxException e) {
            System.out.println("Die Parameter konnten nicht gelesen werden.");
        }
    }

    // Method for parsing query params.
    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> queryParams = new HashMap<>();

        if (query == null || query.isEmpty()) {
            return queryParams;
        }

        String[] params = query.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=");

            if (keyValue.length > 1) {
                queryParams.put(
                        URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8)
                );
            } else {
                queryParams.put(
                        URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8),
                        ""
                );
            }
        }

        return queryParams;
    }

    // Getter-Methoden
    public String getScheme() {
        return this.uri.getScheme();
    }

    public String getHost() {
        return this.uri.getHost();
    }

    public int getPort() {
        return this.uri.getPort();
    }

    public String getPath() {
        return this.uri.getPath();
    }

    public Boolean checkPath(String path) {
        return this.getPath().equals(path);
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public String getMethod(){
        return this.method;
    }

    public String getHttpVersion(){
        return this.httpVersion;
    }


}
