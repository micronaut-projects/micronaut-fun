package fun.micronaut.search;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.views.View;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController {

    public static final String MODEL_KEY_HAS_RESULTS = "hasResults";
    public static final String MODEL_KEY_RESULTS = "results";
    public static final String MODEL_KEY_QUERY = "query";
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @Get("/")
    @View("search")
    public Map<String, Object> index(@Nullable @QueryValue String q) {
        Map<String, Object> model = new HashMap<>();
        String query = q !=null ? q : "";
        model.put(MODEL_KEY_QUERY, query);
        if (StringUtils.isNotEmpty(query)) {
            List<SearchResult> results = searchService.search(query);
            model.put(MODEL_KEY_RESULTS, results);
            model.put(MODEL_KEY_HAS_RESULTS, !results.isEmpty());
        } else {
            model.put(MODEL_KEY_HAS_RESULTS, false);
        }
        return model;
    }
}