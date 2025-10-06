package fun.micronaut.httpclients;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class CoreDocsClientTest {

    @Inject
    CoreDocsClient client;

    @Test
    void latestWorks() {
        assertDoesNotThrow(() -> client.latest());
    }

    @Test
    void latestLatestConfigurationReferenceWorks() {
        assertDoesNotThrow(() -> client.latestConfigurationReference());
    }

    @Test
    void snapshotWorks() {
        assertDoesNotThrow(() -> client.snapshot());
    }

    @Test
    void snapshotConfigurationReferenceWorks() {
        assertDoesNotThrow(() -> client.snapshotConfigurationReference());
    }

}