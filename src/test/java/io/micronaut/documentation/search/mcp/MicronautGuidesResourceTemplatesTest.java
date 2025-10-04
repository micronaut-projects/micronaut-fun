package io.micronaut.documentation.search.mcp;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class MicronautGuidesResourceTemplatesTest {

    @Test
    void resourceCompletion(McpSyncClient client) {
        McpSchema.ReadResourceResult readResourceResult = assertDoesNotThrow(() -> client.readResource(new McpSchema.ReadResourceRequest("guidemetadata://micronaut-oauth2-auth0")));
        assertNotNull(readResourceResult);
        assertFalse(readResourceResult.contents().isEmpty());
    }

}