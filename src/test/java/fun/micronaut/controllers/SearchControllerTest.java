package fun.micronaut.controllers;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class SearchControllerTest {

    @Test
    void testSearch(SearchController controller) {
        Map<String, Object> model = controller.searchModel("WWW Authenticate");
        assertEquals(Map.of(
                "query", "WWW Authenticate",
                "hasResults", false,
                "results", Collections.emptyList()
        ), model);
    }
}