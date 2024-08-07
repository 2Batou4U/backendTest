package de.froschcraft;

/**
 * Router for GET requests.
 */
public class GETRouter {
    // TODO: Implement GET requests.
    public static void routePath(
            URIParser uriParser
    ) {
        System.out.println(uriParser.getPath());
    }
}
