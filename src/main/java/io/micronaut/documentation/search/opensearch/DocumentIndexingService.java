package io.micronaut.documentation.search.opensearch;

import io.micronaut.core.util.StringUtils;
import io.micronaut.documentation.search.docs.CoreDocsClient;
import io.micronaut.documentation.search.IndexedDocument;
import io.micronaut.documentation.search.MarkdownConversion;
import io.micronaut.documentation.search.guides.*;
import io.micronaut.documentation.search.modules.MicronautModule;
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

    private final GuidesFetcher guidesFetcher;
    private final CoreDocsClient coreDocsClient;
    private final MicronautProjectsGithubClient client;
    private final OpenSearchClient openSearchClient;
    private final List<MicronautModule> modules;
    private final MarkdownConversion markdownConversion;

    public DocumentIndexingService(GuidesFetcher guidesFetcher,
                                   CoreDocsClient coreDocsClient,
                                   MicronautProjectsGithubClient client,
                                   OpenSearchClient openSearchClient,
                                   List<MicronautModule> modules,
                                   MarkdownConversion markdownConversion) {
        this.guidesFetcher = guidesFetcher;
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

        indexGuides();
        indexCoreDocs();
        modules.forEach(this::indexMicronautModule);

        LOG.info("MicronautModule indexing process completed");
    }

    private void indexGuides() {
        for (Guide guide : guidesFetcher.findAll()) {
            LOG.debug("indexing guide: {}", guide.slug());

            // Prioritize Java language option, fallback to first option
            Option selectedOption = guide.options().stream()
                    .filter(option -> option.language() == Language.JAVA)
                    .findFirst()
                    .orElseGet(() -> guide.options().isEmpty() ? null : guide.options().getFirst());

            if (selectedOption != null) {
                guidesFetcher.findBySlugAndBuildAndLanguage(guide.slug(), selectedOption.buildTool(), selectedOption.language())
                        .ifPresent(html -> {
                            String url = guide.url();
                            indexMicronautModule(guide.slug(), guide.title(), url, html, "guide", false);
                        });
            }
        }
    }

    private void indexCoreDocs() {
        try {
            String htmlContent = coreDocsClient.latest();
            String url = "https://docs.micronaut.io/latest/guide/index.html";
            indexMicronautModule("Micronaut Core", "micronaut-core", url, htmlContent, "module", true);

//            htmlContent = coreDocsClient.configurationReference();
//            url = "https://docs.micronaut.io/latest/guide/configurationreference.html";
//            indexMicronautModule(null, url, htmlContent);

        } catch (Exception e) {
            LOG.error("Error indexing core docs", e);
        }
    }

    private void indexMicronautModule(MicronautModule module) {
        try {
            String htmlContent = client.snapshot(module.getSlug());
            String url = "https://micronaut-projects.github.io/" + module.getSlug() + "/snapshot/guide/index.html";
            indexMicronautModule(module.getTitle(), module.getSlug(), url, htmlContent, "module", true);

//            htmlContent = client.configurationReference(document.getSlug());
//            url = "https://micronaut-projects.github.io/" + document.getSlug() + "/latest/guide/configurationreference.html";
//            indexMicronautModule(document, url, htmlContent);

        } catch (Exception e) {
            LOG.error("Error indexing document: {}", module.getName(), e);
        }
    }

    private void indexMicronautModule(String slug,
                                      String title,
                                      String url,
                                      String htmlContent,
                                      String idPrefix,
                                      boolean splitSections) {
        try {
            LOG.debug("fetching HTML in {}", url);
            if (StringUtils.isNotEmpty(htmlContent)) {
                org.jsoup.nodes.Document doc = Jsoup.parse(htmlContent);
                final String baseUrl = url.split("#")[0];

                String id = idPrefix + "-" + slug;
                // Find sections marked by <h1 id="...">
                List<org.jsoup.nodes.Element> h2Elements = doc.select("h2[id]");
                if (splitSections && !h2Elements.isEmpty()) {
                    for (org.jsoup.nodes.Element h2 : h2Elements) {
                        String anchorId = h2.id();
                        String headingText = h2.text();
                        String sectionTitle = stripLeadingNumber(cleanText(headingText));
                        LOG.debug("# title: {}", sectionTitle);
                        id = idPrefix + "-" + slug + "-" + anchorId;

                        // Skip configured sections
                        String normalized = normalizeTitleKey(sectionTitle);
                        if (SKIP_SECTION_TITLES.contains(normalized)) {
                            LOG.debug("Skipping section '{}' at {}#{}", sectionTitle, baseUrl, anchorId);
                            continue;
                        }

                        String sectionUrl = baseUrl + "#" + anchorId;

                        String sectionHtml = collectSectionHtml(h2);
                        String markdownContent = markdownConversion.toMarkdown(sectionHtml);
//                        LOG.debug("=============");
//                        LOG.debug(markdownContent);


                        // Fallback: use container html if markdown came empty
                        if (markdownContent.isBlank()) {
                            org.jsoup.nodes.Element container = h2.parent();
                            if (container != null) {
                                org.jsoup.nodes.Element clone = container.clone();
                                clone.select("h1#" + anchorId).remove();
                                markdownContent = markdownConversion.toMarkdown(clone.html());
                            }
                        }

                        // Build display title including module title when splitting sections

                        String displayTitle = sectionTitle.isEmpty() ? title : sectionTitle;
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
                LOG.warn("could not fetch HTML in {}", url);
            }
        } catch (Exception e) {
            LOG.error("Error indexing document: {}", idPrefix + "-" + slug, e);
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
                LOG.debug("Successfully indexed simplified document: {} with result: {}", doc.id(), fallbackResponse.result());
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