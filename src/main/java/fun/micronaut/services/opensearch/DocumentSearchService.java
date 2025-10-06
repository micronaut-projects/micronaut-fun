package fun.micronaut.services.opensearch;

import fun.micronaut.conf.SearchConfiguration;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.StringUtils;
import fun.micronaut.model.SearchResult;
import fun.micronaut.services.SearchService;
import jakarta.inject.Singleton;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.query_dsl.MultiMatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.opensearch.client.opensearch.core.search.HitsMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Singleton
public class DocumentSearchService implements SearchService {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentSearchService.class);
    private static final String INDEX_NAME = "micronaut-docs";

    private final SearchConfiguration searchConfiguration;
    private final OpenSearchClient openSearchClient;

    public DocumentSearchService(SearchConfiguration searchConfiguration,
                                 OpenSearchClient openSearchClient) {
        this.searchConfiguration = searchConfiguration;
        this.openSearchClient = openSearchClient;
    }

    @Override
    public List<SearchResult> search(String query) {
        return search(query, searchConfiguration.getResultsPerPage());
    }

    public List<SearchResult> search(String query, int size) {
        try {
            SearchRequest searchRequest = buildSearchRequest(query, size);
            SearchResponse<Map> searchResponse = openSearchClient.search(searchRequest, Map.class);

            return processSearchResponse(searchResponse);
        } catch (IOException e) {
            LOG.error("Error performing search for query: {}", query, e);
            return List.of();
        }
    }

    private SearchRequest buildSearchRequest(String query, int size) {
        Query multiMatchQuery = MultiMatchQuery.of(m -> m
            .query(query)
            // Much higher boost for title to prioritize title matches over content matches
            .fields("title^10.0", "content^1.0")
            .type(org.opensearch.client.opensearch._types.query_dsl.TextQueryType.BestFields)
            .fuzziness("AUTO")
        )._toQuery();

        // Boost micronaut-core results
        Query prefixBoostQuery = Query.of(q -> q
            .bool(b -> b
                .must(multiMatchQuery)
                .should(s -> s
                    .prefix(p -> p
                        .field("id")
                        .value("module-micronaut-core")
                        .boost(2.0f)
                    )
                )
            )
        );

        return SearchRequest.of(s -> s
            .index(INDEX_NAME)
            .query(prefixBoostQuery)
            .size(size)
            .highlight(h -> h
                .fields("title", tf -> tf)
                .fields("content", cf -> cf)
                .preTags("<mark>")
                .postTags("</mark>")
                .fragmentSize(150)
                .numberOfFragments(1)
            )
        );
    }

    private List<SearchResult> processSearchResponse(SearchResponse<Map> searchResponse) {
        List<SearchResult> results = new ArrayList<>();

        HitsMetadata<Map> hits = searchResponse.hits();
        for (Hit<Map> hit : hits.hits()) {
            try {
                searchResult(hit).ifPresent(results::add);
            } catch (Exception e) {
                LOG.warn("Error processing search hit", e);
            }
        }
        return results;
    }

    private Optional<SearchResult> searchResult(Hit<Map> hit) {
        return searchResult(hit, hit.source());
    }

    private Optional<SearchResult> searchResult(@Nullable Hit<Map> hit, Map<String, Object> m) {
        String id = (String) m.get("id");
        String title = (String) m.get("title");
        String url = (String) m.get("url");
        String content = (String) m.get("content");
        String description = extractDescription(hit, m);
        if (title != null && url != null) {
            return Optional.of(new SearchResult(id, title, description, content, url));
        }
        return Optional.empty();
    }

    private String extractDescription(@Nullable Hit<Map> hit, Map<String, Object> sourceMap) {
        if (hit != null) {
            Map<String, List<String>> highlightFields = hit.highlight();
            if (highlightFields != null && highlightFields.containsKey("content")) {
                List<String> highlights = highlightFields.get("content");
                if (!highlights.isEmpty()) {
                    return highlights.get(0);
                }
            }
        }

        // Fallback to truncated content
        String content = (String) sourceMap.get("content");
        if (content != null && content.length() > 150) {
            return content.substring(0, 150) + "...";
        }

        return content != null ? content : "No description available.";
    }

    // New: fetch a single SearchResult by IndexedDocument id
    public Optional<SearchResult> findById(String id) {
        if (StringUtils.isEmpty(id)) {
            return Optional.empty();
        }
        try {
            var getResponse = openSearchClient.get(g -> g
                .index(INDEX_NAME)
                .id(id), Map.class);

            if (getResponse == null || !getResponse.found()) {
                return Optional.empty();
            }
            Map<String, Object> sourceMap = getResponse.source();
            if (sourceMap == null) {
                return Optional.empty();
            }
            return searchResult(null, sourceMap);
        } catch (Exception e) {
            LOG.error("Error fetching document by id {}", id, e);
            return Optional.empty();
        }
    }
}