package io.micronaut.documentation.search;

import io.micronaut.documentation.search.docs.MicronautProjectsGithubClient;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class MicronautProjectsGithubClientTest {

    @Test
    void getDocumentation(MicronautProjectsGithubClient client) {
        String html = assertDoesNotThrow(() -> client.latest("micronaut-test"));
        assertNotNull(html);
        assertTrue(html.contains("<title>Micronaut Test</title>"));
        html = assertDoesNotThrow(() -> client.configurationReference("micronaut-test"));
        assertNotNull(html);
        assertTrue(html.contains("<title>Configuration Reference | Micronaut</title>"));
    }
}