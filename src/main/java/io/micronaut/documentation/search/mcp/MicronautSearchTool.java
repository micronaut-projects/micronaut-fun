package io.micronaut.documentation.search.mcp;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.documentation.search.SearchService;
import io.micronaut.mcp.server.tools.search.SearchRequest;
import io.micronaut.mcp.server.tools.search.SearchResponse;
import io.micronaut.mcp.server.tools.search.SearchTool;
import io.modelcontextprotocol.common.McpTransportContext;
import jakarta.inject.Singleton;

@Singleton
public class MicronautSearchTool implements SearchTool {
    private final SearchService searchService;

    public MicronautSearchTool(SearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    public @NonNull SearchResponse search(@NonNull SearchRequest request, @Nullable McpTransportContext mcpTransportContext) {
        return new SearchResponse(searchService.search(request.query())
                .stream()
                .map(searchResult -> new io.micronaut.mcp.server.tools.search.SearchResult(
                            searchResult.id(),
                            searchResult.title(),
                            searchResult.link()
                    )
                ).toList());
    }
}
