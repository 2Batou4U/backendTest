package de.froschcraft;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class HTMLBuilder {
    private static HTMLBuilder htmlBuilder;

    private final Map<String, Path> templateMap;

    private HTMLBuilder () {
        this.templateMap = new HashMap<>();

        Path workingDirectory = FileSystems.getDefault().getPath(System.getProperty("user.dir"));
        Path templateDirectory = workingDirectory.resolve("templates/");

        DirectoryStream.Filter<Path> filterFile = file -> (file.endsWith(".html"));

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(templateDirectory, filterFile)) {

            // Add all templates to the Map.
            for (Path path : stream) this.templateMap.put(path.getFileName().toString(), path);

        } catch (IOException e) {
            System.out.printf("Auf die Datei %s konnte nicht zugegriffen werden.%n", workingDirectory);
        } catch (NullPointerException e) {
            System.out.println("Es konnte kein templates-Ordner gefunden werden.");
        }
    }

    public static HTMLBuilder getInstance(){
        if (htmlBuilder == null) {
            htmlBuilder = new HTMLBuilder();
        }
        return htmlBuilder;
    }

    public Path getTemplate(String templateName) {
        return this.templateMap.get(templateName);
    }

    private String getTemplateContents(Path templatePath) throws IOException {
        try {
            return Files.readString(templatePath);
        } catch (IOException e) {
            System.out.printf("Template %s konnte nicht gelesen werden.%n", templatePath);
            throw e;
        }
    }

    public String renderHTML(String templateName) {
        return formatHTML(this.templateMap.get(templateName), null);
    }

    public String renderHTML(Path templatePath) {
        return formatHTML(templatePath, null);
    }

    private static String formatHTML(Path templatePath, String[] formatOptions) {

        // TODO: Format options
        return "";
    }
}
