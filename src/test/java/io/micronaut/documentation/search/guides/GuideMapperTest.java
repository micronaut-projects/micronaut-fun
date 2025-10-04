package io.micronaut.documentation.search.guides;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class GuideMapperTest {

    @Test
    void testGuideMapper(GuideMapper mapper, GuidesFetcher fetcher) {
        Optional<Guide> guideOptional = fetcher.findBySlug("micronaut-oauth2-auth0");
        assertTrue(guideOptional.isPresent());
        Guide guide = guideOptional.get();
        GuideListing guideListing = assertDoesNotThrow(() -> mapper.map(guide));
        assertEquals("guidemetadata://micronaut-oauth2-auth0", guideListing.uri());

    }

}