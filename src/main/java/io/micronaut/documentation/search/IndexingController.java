package io.micronaut.documentation.search;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;

@Controller("/api")
public class IndexingController {

    private final DocumentIndexingService indexingService;

    public IndexingController(DocumentIndexingService indexingService) {
        this.indexingService = indexingService;
    }

    @Post("/reindex")
    public String reindex() {
        indexingService.indexAllDocuments();
        return "Reindexing started";
    }
}