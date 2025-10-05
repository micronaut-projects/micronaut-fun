package fun.micronaut.search;

import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@MicronautTest
class StaticResourceTest {

    @Test
    void guidesJsonSchemaExists(@Client("/") HttpClient client) {
        assertDoesNotThrow(() -> client.toBlocking().retrieve("/schemas/guide.schema.json"));
    }

    @Test
    void modulesJsonSchemaExists(@Client("/") HttpClient client) {
        assertDoesNotThrow(() -> client.toBlocking().retrieve("/schemas/modules.schema.json"));
    }
}