package fun.micronaut.search.mcp;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import fun.micronaut.search.search.SearchService;
import io.micronaut.mcp.server.tools.fetch.FetchRequest;
import io.micronaut.mcp.server.tools.fetch.FetchResponse;
import io.micronaut.mcp.server.tools.fetch.FetchTool;
import io.modelcontextprotocol.common.McpTransportContext;
import jakarta.inject.Singleton;

import java.util.Optional;

@Singleton
class MicronautFetchTool implements FetchTool {
    private final SearchService searchService;

    public MicronautFetchTool(SearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    public @NonNull Optional<FetchResponse> fetch(@NonNull FetchRequest request, @Nullable McpTransportContext mcpTransportContext) {
        return searchService.findById(request.id())
                .map(searchResult -> new FetchResponse(
                        searchResult.id(),
                        searchResult.title(),
                        searchResult.content(),
                        searchResult.link(),
                        null));
    }
}
