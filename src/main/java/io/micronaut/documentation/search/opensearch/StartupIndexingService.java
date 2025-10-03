package io.micronaut.documentation.search.opensearch;

import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.runtime.event.ApplicationStartupEvent;
import io.micronaut.scheduling.annotation.Async;
import jakarta.inject.Singleton;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.CreateIndexResponse;
import org.opensearch.client.opensearch.indices.DeleteIndexRequest;
import org.opensearch.client.opensearch.indices.ExistsRequest;
import org.opensearch.client.transport.endpoints.BooleanResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Singleton
public class StartupIndexingService implements ApplicationEventListener<ApplicationStartupEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(StartupIndexingService.class);
    private static final String INDEX_NAME = "micronaut-docs";

    private final OpenSearchClient openSearchClient;
    private final DocumentIndexingService indexingService;

    public StartupIndexingService(OpenSearchClient openSearchClient, DocumentIndexingService indexingService) {
        this.openSearchClient = openSearchClient;
        this.indexingService = indexingService;
    }

    @Override
    public void onApplicationEvent(ApplicationStartupEvent event) {
        try {
            LOG.info("Starting OpenSearch connection test...");
            testConnection();
            LOG.info("OpenSearch connection successful, proceeding with index setup...");
            indexAllDocuments();
        } catch (Exception e) {
            LOG.error("Error during startup indexing", e);
            LOG.error("Full stack trace:", e);
        }
    }

    @Async
    void indexAllDocuments() {
        try {
            setupIndex();
            indexingService.indexAllDocuments();
        } catch (Exception e) {
            LOG.error("Error during startup indexing", e);
            LOG.error("Full stack trace:", e);
        }
    }

    private void testConnection() throws IOException {
        try {
            var health = openSearchClient.cluster().health();
            LOG.info("OpenSearch cluster health: {}", health.status());
        } catch (Exception e) {
            LOG.error("Failed to connect to OpenSearch cluster", e);
            throw e;
        }
    }

    private void setupIndex() throws IOException {
        // Check if index exists
        ExistsRequest existsRequest = ExistsRequest.of(e -> e.index(INDEX_NAME));
        BooleanResponse indexExists = openSearchClient.indices().exists(existsRequest);

        if (indexExists.value()) {
            LOG.info("Index {} already exists, deleting and recreating...", INDEX_NAME);
            DeleteIndexRequest deleteRequest = DeleteIndexRequest.of(d -> d.index(INDEX_NAME));
            openSearchClient.indices().delete(deleteRequest);
        }

        // Create index with mapping and settings
        CreateIndexRequest createIndexRequest = CreateIndexRequest.of(c -> c
            .index(INDEX_NAME)
            .settings(s -> s
                .numberOfShards("1")
                .numberOfReplicas("0")
                .analysis(a -> a
                    .analyzer("html_analyzer", an -> an
                        .custom(ca -> ca
                            .tokenizer("standard")
                            .filter("lowercase", "stop")
                        )
                    )
                )
            )
            .mappings(m -> m
                .properties("id", p -> p.keyword(k -> k))
                .properties("title", p -> p
                    .text(t -> t
                        .analyzer("html_analyzer")
                        .fields("keyword", f -> f.keyword(k -> k))
                    )
                )
                .properties("content", p -> p
                    .text(t -> t.analyzer("html_analyzer"))
                )
                .properties("htmlContent", p -> p
                    .text(t -> t.index(false))
                )
                .properties("url", p -> p.keyword(k -> k))
                .properties("filePath", p -> p.keyword(k -> k))
            )
        );

        CreateIndexResponse createIndexResponse = openSearchClient.indices().create(createIndexRequest);

        if (createIndexResponse.acknowledged()) {
            LOG.info("Successfully created index: {}", INDEX_NAME);
        } else {
            LOG.error("Failed to create index: {}", INDEX_NAME);
        }
    }
}