package io.micronaut.documentation.search;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.cluster.HealthRequest;
import org.opensearch.client.opensearch.cluster.HealthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Controller("/health")
public class OpenSearchHealthController {

    private static final Logger LOG = LoggerFactory.getLogger(OpenSearchHealthController.class);
    private final OpenSearchClient openSearchClient;

    public OpenSearchHealthController(OpenSearchClient openSearchClient) {
        this.openSearchClient = openSearchClient;
    }

    @Get("/opensearch")
    public Map<String, Object> checkOpenSearchHealth() {
        try {
            LOG.info("Testing OpenSearch connection...");
            HealthResponse health = openSearchClient.cluster().health(HealthRequest.of(h -> h));
            LOG.info("OpenSearch cluster status: {}", health.status());

            return Map.of(
                "status", "UP",
                "clusterName", health.clusterName(),
                "clusterStatus", health.status().toString(),
                "numberOfNodes", health.numberOfNodes(),
                "numberOfDataNodes", health.numberOfDataNodes()
            );
        } catch (Exception e) {
            LOG.error("OpenSearch connection failed", e);
            return Map.of(
                "status", "DOWN",
                "error", e.getMessage(),
                "errorType", e.getClass().getSimpleName()
            );
        }
    }
}