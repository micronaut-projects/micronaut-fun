package io.micronaut.documentation.search.opensearch;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Requires(env = "production")
@Singleton
public class IndexRefreshJob {

    private static final Logger LOG = LoggerFactory.getLogger(IndexRefreshJob.class);

    private final DocumentIndexingService documentIndexingService;

    public IndexRefreshJob(DocumentIndexingService documentIndexingService) {
        this.documentIndexingService = documentIndexingService;
    }

    @Scheduled(cron = "0 0 4 * * *")
    public void refreshIndex() {
        LOG.info("Starting scheduled index refresh at 4:00 AM");
        try {
            documentIndexingService.indexAllDocuments();
            LOG.info("Scheduled index refresh completed successfully");
        } catch (Exception e) {
            LOG.error("Error during scheduled index refresh", e);
        }
    }
}
