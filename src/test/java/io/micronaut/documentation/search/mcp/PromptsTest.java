package io.micronaut.documentation.search.mcp;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class PromptsTest {

    @Test
    void introspectionTesting(McpSyncClient client) {
        McpSchema.ListPromptsResult listPromptsResult = assertDoesNotThrow(() -> client.listPrompts());
        assertTrue(listPromptsResult.prompts().stream().anyMatch(prompt -> prompt.name().equals("introspection-testing")));
    }
}