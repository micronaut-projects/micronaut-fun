package fun.micronaut.search;

import fun.micronaut.httpclients.MicronautProjectsGithubClient;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class MicronautProjectsGithubClientTest {

    @Inject
    MicronautProjectsGithubClient client;

    @Test
    void latestWorks() {
        assertDoesNotThrow(() -> client.latest("micronaut-security"));
    }

    @Test
    void latestLatestConfigurationReferenceWorks() {
        assertDoesNotThrow(() -> client.latestConfigurationReference("micronaut-security"));
    }

    @Test
    void snapshotWorks() {
        assertDoesNotThrow(() -> client.snapshot("micronaut-security"));
    }

    @Test
    void snapshotConfigurationReferenceWorks() {
        assertDoesNotThrow(() -> client.snapshotConfigurationReference("micronaut-security"));
    }

    @Test
    void getDocumentation() {
        String html = assertDoesNotThrow(() -> client.latest("micronaut-test"));
        assertNotNull(html);
        assertTrue(html.contains("<title>Micronaut Test</title>"));
        html = assertDoesNotThrow(() -> client.snapshotConfigurationReference("micronaut-test"));
        assertNotNull(html);
        assertTrue(html.contains("<title>Configuration Reference | Micronaut</title>"));
    }
}