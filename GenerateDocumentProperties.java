///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 17+

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class GenerateDocumentProperties {

    public static void main(String[] args) {
        try {
            GenerateDocumentProperties generator = new GenerateDocumentProperties();
            generator.generateProperties();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void generateProperties() throws IOException {
        Path docsDir = Paths.get("src/main/resources/docs");

        if (!Files.exists(docsDir)) {
            System.err.println("Docs directory not found: " + docsDir);
            return;
        }

        List<DocumentEntry> entries = new ArrayList<>();

        // Find all HTML files with corresponding metadata
        try (Stream<Path> htmlFiles = Files.walk(docsDir)
                .filter(path -> path.toString().endsWith(".html"))
                .filter(path -> {
                    // Only include main documentation files (index.html and configurationreference.html)
                    String fileName = path.getFileName().toString();
                    return fileName.equals("index.html") || fileName.equals("configurationreference.html");
                })
                .filter(path -> {
                    // Exclude deep API documentation
                    return !path.toString().contains("/api/");
                })
                .sorted()) {

            htmlFiles.forEach(htmlPath -> {
                try {
                    Path metadataPath = Paths.get(htmlPath.toString() + ".metadata.json");
                    if (Files.exists(metadataPath)) {
                        String url = extractUrlFromMetadata(metadataPath);
                        if (url != null) {
                            entries.add(createDocumentEntry(htmlPath, url, docsDir));
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error processing " + htmlPath + ": " + e.getMessage());
                }
            });
        }

        // Sort entries by project name and then by type (configurationreference before index)
        entries.sort(Comparator.comparing(DocumentEntry::getProjectName)
                .thenComparing(entry -> entry.getFileName().equals("configurationreference.html") ? 0 : 1));

        // Generate properties
        System.out.println("# Generated application.properties entries for main documentation files:");
        System.out.println("# Found " + entries.size() + " main documentation files");
        System.out.println("# Format: documents.{project-name}-{type}.path and documents.{project-name}-{type}.url");
        System.out.println();

        for (DocumentEntry entry : entries) {
            String key = entry.getProjectName() + "-" + entry.getType();
            System.out.println("documents." + key + ".path=" + entry.getPath());
            System.out.println("documents." + key + ".url=" + entry.getUrl());
        }

        System.out.println();
        System.out.println("# Processing summary:");
        entries.forEach(entry -> {
            System.out.println("# " + entry.getProjectName() + " - " + entry.getType());
        });
    }

    private String extractUrlFromMetadata(Path metadataPath) throws IOException {
        // Simple JSON parsing without external dependencies
        String content = Files.readString(metadataPath);

        // Look for "customized_url_source":"url"
        String searchFor = "\"customized_url_source\":\"";
        int startIndex = content.indexOf(searchFor);
        if (startIndex == -1) {
            return null;
        }

        startIndex += searchFor.length();
        int endIndex = content.indexOf("\"", startIndex);
        if (endIndex == -1) {
            return null;
        }

        return content.substring(startIndex, endIndex);
    }

    private DocumentEntry createDocumentEntry(Path htmlPath, String url, Path docsDir) {
        Path relativePath = docsDir.relativize(htmlPath);
        String classpathPath = "classpath:docs/" + relativePath.toString().replace('\\', '/');

        // Extract project name from path (e.g., "micronaut-aot" from "micronaut-aot/configurationreference.html")
        String[] pathParts = relativePath.toString().replace('\\', '/').split("/");
        String projectName = pathParts[0];
        String fileName = htmlPath.getFileName().toString();

        String type = fileName.equals("configurationreference.html") ? "configurationreference" : "index";

        // Construct the URL with proper classpath format
        String fullUrl = "classpath:docs/" + projectName + "/" + url;

        return new DocumentEntry(projectName, type, fileName, classpathPath, fullUrl);
    }

    static class DocumentEntry {
        private final String projectName;
        private final String type;
        private final String fileName;
        private final String path;
        private final String url;

        public DocumentEntry(String projectName, String type, String fileName, String path, String url) {
            this.projectName = projectName;
            this.type = type;
            this.fileName = fileName;
            this.path = path;
            this.url = url;
        }

        public String getProjectName() { return projectName; }
        public String getType() { return type; }
        public String getFileName() { return fileName; }
        public String getPath() { return path; }
        public String getUrl() { return url; }
    }
}