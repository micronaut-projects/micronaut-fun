package io.micronaut.documentation.search.mcp;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class ToolsTest {
    @Test
    void guidesTool(McpSyncClient client) {
        McpSchema.ListToolsResult listToolsResult = client.listTools();
        assertTrue(listToolsResult.tools()
                .stream()
                .map(McpSchema.Tool::name)
                .anyMatch(name -> name.equals("guides")));

        McpSchema.CallToolResult guides = assertDoesNotThrow(() -> client.callTool(McpSchema.CallToolRequest.builder().name("guides").build()));
        assertNotNull(guides.structuredContent());
    }

    @Test
    void micronautModules(McpSyncClient client) {
        McpSchema.ListToolsResult listToolsResult = client.listTools();
        assertTrue(listToolsResult.tools()
                .stream()
                .map(McpSchema.Tool::name)
                .anyMatch(name -> name.equals("modules")));

        McpSchema.CallToolResult guides = assertDoesNotThrow(() ->
                client.callTool(McpSchema.CallToolRequest.builder().name("modules").build()));
        assertNotNull(guides.structuredContent());
    }
}