package io.micronaut.documentation.search.opensearch;

import io.micronaut.core.util.StringUtils;
import io.micronaut.documentation.search.docs.CoreDocsClient;
import io.micronaut.documentation.search.IndexedDocument;
import io.micronaut.documentation.search.MarkdownConversion;
import io.micronaut.documentation.search.MicronautModule;
import io.micronaut.documentation.search.docs.MicronautProjectsGithubClient;
import jakarta.inject.Singleton;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.IndexRequest;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Singleton
public class DocumentIndexingService {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentIndexingService.class);
    private static final String INDEX_NAME = "micronaut-docs";

    // Sections to skip when splitting by H1
    private static final Set<String> SKIP_SECTION_TITLES = Set.of(
        "introduction",
        "release history",
        "whats new",
        "breaking changes",
        "repository"
    );

    private final CoreDocsClient coreDocsClient;
    private final MicronautProjectsGithubClient client;
    private final OpenSearchClient openSearchClient;
    private final List<MicronautModule> modules;
    private final MarkdownConversion markdownConversion;

    public DocumentIndexingService(CoreDocsClient coreDocsClient,
                                   MicronautProjectsGithubClient client,
                                   OpenSearchClient openSearchClient,
                                   List<MicronautModule> modules,
                                   MarkdownConversion markdownConversion) {
        this.coreDocsClient = coreDocsClient;
        this.client = client;
        this.openSearchClient = openSearchClient;
        this.modules = modules;
        this.markdownConversion = markdownConversion;
    }

    public void indexAllDocuments() {
        LOG.info("Starting document indexing process...");

        if (modules.isEmpty()) {
            LOG.warn("No documents configured for indexing");
            return;
        }

        indexCoreDocs();
        modules.forEach(this::indexMicronautModule);

        LOG.info("MicronautModule indexing process completed");
    }

    private void indexCoreDocs() {
        try {
            String htmlContent = coreDocsClient.latest();
            String url = "https://docs.micronaut.io/latest/guide/index.html";
            indexMicronautModule(null, url, htmlContent);

//            htmlContent = coreDocsClient.configurationReference();
//            url = "https://docs.micronaut.io/latest/guide/configurationreference.html";
//            indexMicronautModule(null, url, htmlContent);

        } catch (Exception e) {
            LOG.error("Error indexing core docs", e);
        }
    }

    private void indexMicronautModule(MicronautModule document) {
        try {
            String htmlContent = client.latest(document.getSlug());
            String url = "https://micronaut-projects.github.io/" + document.getSlug() + "/latest/guide/index.html";
            indexMicronautModule(document, url, htmlContent);

//            htmlContent = client.configurationReference(document.getSlug());
//            url = "https://micronaut-projects.github.io/" + document.getSlug() + "/latest/guide/configurationreference.html";
//            indexMicronautModule(document, url, htmlContent);

        } catch (Exception e) {
            LOG.error("Error indexing document: {}", document.getName(), e);
        }
    }

    private void indexMicronautModule(MicronautModule micronautModule, String url, String htmlContent) {
        try {
            LOG.info("fetching HTML in {}", url);
            if (StringUtils.isNotEmpty(htmlContent)) {
                org.jsoup.nodes.Document doc = Jsoup.parse(htmlContent);
                final String baseUrl = url.split("#")[0];

                String id = (micronautModule == null ? micronautModule.getSlug() : "micronaut-core");
                // Find sections marked by <h1 id="...">
                List<org.jsoup.nodes.Element> h1Elements = doc.select("h1[id]");
                String moduleTitle = cleanText(micronautModule != null ? micronautModule.getTitle() : "");
                if (!h1Elements.isEmpty()) {
                    for (org.jsoup.nodes.Element h1 : h1Elements) {
                        String anchorId = h1.id();
                        String headingText = h1.text();
                        String sectionTitle = stripLeadingNumber(cleanText(headingText));
                        LOG.debug("# title: {}", sectionTitle);
                        id = (micronautModule != null ? micronautModule.getSlug() : "micronaut-core") + "-" + anchorId;

                        // Skip configured sections
                        String normalized = normalizeTitleKey(sectionTitle);
                        if (SKIP_SECTION_TITLES.contains(normalized)) {
                            LOG.debug("Skipping section '{}' at {}#{}", sectionTitle, baseUrl, anchorId);
                            continue;
                        }

                        String sectionUrl = baseUrl + "#" + anchorId;

                        String sectionHtml = collectSectionHtml(h1);
                        String markdownContent = markdownConversion.toMarkdown(sectionHtml);
//                        LOG.debug("=============");
//                        LOG.debug(markdownContent);


                        // Fallback: use container html if markdown came empty
                        if (markdownContent.isBlank()) {
                            org.jsoup.nodes.Element container = h1.parent();
                            if (container != null) {
                                org.jsoup.nodes.Element clone = container.clone();
                                clone.select("h1#" + anchorId).remove();
                                markdownContent = markdownConversion.toMarkdown(clone.html());
                            }
                        }

                        // Build display title including module title when splitting sections

                        String displayTitle = sectionTitle.isEmpty() ? moduleTitle : sectionTitle + (moduleTitle.isEmpty() ? "" : " | " + moduleTitle);
                        if (!displayTitle.isEmpty() || !markdownContent.isEmpty()) {
                            IndexedDocument indexedDoc = new IndexedDocument(
                                id,
                                displayTitle,
                                markdownContent,
                                sectionUrl
                            );
                            indexDocumentInOpenSearch(indexedDoc);
                            LOG.debug("Indexed section (MD): {} with title: {}", sectionUrl, displayTitle);
                        }
                    }
                } else {
                    // Fallback: index the whole document as a single entry using Markdown
                    String title = cleanText(doc.title());
                    if (title != null && title.contains("Configuration Reference | Micronaut")) {
                        title = title.replace("Micronaut", moduleTitle);
                    }
                    String bodyHtml = doc.body() != null ? doc.body().html() : "";
                    String markdownContent = markdownConversion.toMarkdown(bodyHtml);

                    IndexedDocument indexedDoc = new IndexedDocument(
                        id,
                        title,
                        markdownContent,
                        url
                    );
                    indexDocumentInOpenSearch(indexedDoc);
                    LOG.debug("Indexed document (MD): {}", url);
                }
            } else {
                LOG.info("could not fetch HTML in {}", url);
            }
        } catch (Exception e) {
            LOG.error("Error indexing document: {}", (micronautModule != null ? micronautModule.getName() : ""), e);
        }
    }

    private String collectSectionHtml(org.jsoup.nodes.Element h1) {
        StringBuilder html = new StringBuilder();
        org.jsoup.nodes.Element cursor = h1.nextElementSibling();
        while (cursor != null && !("h1".equalsIgnoreCase(cursor.tagName()) && cursor.hasAttr("id"))) {
            html.append(cursor.outerHtml()).append('\n');
            cursor = cursor.nextElementSibling();
        }
        return html.toString();
    }


    private void indexDocumentInOpenSearch(IndexedDocument doc) throws IOException {
        try {
            IndexRequest<IndexedDocument> request = IndexRequest.of(i -> i
                .index(INDEX_NAME)
                .id(doc.id())
                .document(doc)
            );

            var response = openSearchClient.index(request);
            LOG.debug("Successfully indexed document title: {}, id: {} with result: {}", doc.title(), doc.id(), response.result());
        } catch (Exception e) {
            LOG.error("Failed to index document: {} - Error: {}", doc.id(), e.getMessage());

            // Try with a simplified document structure for debugging
            try {
                var simplifiedDoc = new java.util.HashMap<String, Object>();
                simplifiedDoc.put("id", doc.id());
                simplifiedDoc.put("title", doc.title() != null ? doc.title() : "");
                simplifiedDoc.put("content", doc.content() != null ? doc.content().substring(0, Math.min(1000, doc.content().length())) : "");
                simplifiedDoc.put("url", doc.url() != null ? doc.url() : "");

                IndexRequest<Object> fallbackRequest = IndexRequest.of(i -> i
                    .index(INDEX_NAME)
                    .id(doc.id())
                    .document(simplifiedDoc)
                );

                var fallbackResponse = openSearchClient.index(fallbackRequest);
                LOG.info("Successfully indexed simplified document: {} with result: {}", doc.id(), fallbackResponse.result());
            } catch (Exception fallbackError) {
                LOG.error("Even simplified indexing failed for document: {}", doc.id(), fallbackError);
                throw new IOException("Failed to index document: " + doc.id(), e);
            }
        }
    }

    private String cleanText(String text) {
        if (text == null) {
            return "";
        }
        // Remove control characters and normalize whitespace
        return text.replaceAll("[\\p{Cntrl}&&[^\\r\\n\\t]]", "")
                   .replaceAll("\\s+", " ")
                   .trim();
    }

    private String stripLeadingNumber(String title) {
        if (title == null) return "";
        // Remove patterns like "4 ", "4.", "4.1 ", etc. at the beginning
        return title.replaceFirst("^\\s*\\d+(?:\\.\\d+)*\\s*", "").trim();
    }

    private String normalizeTitleKey(String s) {
        if (s == null) return "";
        String normalized = s.toLowerCase();
        // unify and then remove apostrophes (straight and curly)
        normalized = normalized.replace('’', '\'');
        normalized = normalized.replace("'", "");
        // remove punctuation except spaces, then collapse spaces
        normalized = normalized.replaceAll("[^a-z0-9 ]", " ")
            .replaceAll("\\s+", " ")
            .trim();
        return normalized;
    }
}