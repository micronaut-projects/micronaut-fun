package fun.micronaut.services;

import fun.micronaut.model.Guide;
import fun.micronaut.services.DefaultGuidesFetcher;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class GuidesFetcherTest {

    @Test
    void testGuidesFetcher(DefaultGuidesFetcher guidesFetcher) {
        List<Guide> guides = assertDoesNotThrow(guidesFetcher::findAll);
        assertNotNull(guides);
        assertFalse(guides.isEmpty());
        List<String> slugs = assertDoesNotThrow(guidesFetcher::findSlug);
        assertNotNull(slugs);
        assertFalse(slugs.isEmpty());
        Optional<Guide> guide = assertDoesNotThrow(() -> guidesFetcher.findBySlug("micronaut-oauth2-auth0"));
        assertNotNull(guide);
        assertTrue(guide.isPresent());
    }

}