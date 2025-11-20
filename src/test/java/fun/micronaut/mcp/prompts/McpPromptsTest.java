package fun.micronaut.mcp.prompts;

import io.micronaut.core.util.CollectionUtils;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class McpPromptsTest {

    @Test
    void jspecifyPrompts(McpSyncClient mcpClient) {
        McpSchema.GetPromptResult prompt =
                assertDoesNotThrow(() -> mcpClient.getPrompt(new McpSchema.GetPromptRequest("jspecify-nullability-instead-micronaut", Collections.emptyMap())));
        assertTrue(CollectionUtils.isNotEmpty(prompt.messages()));
        assertInstanceOf(McpSchema.TextContent.class, prompt.messages().get(0).content());
        assertTrue(((McpSchema.TextContent) prompt.messages().get(0).content()).text().contains("org.jspecify:jspecify"));
    }

    @Test
    void manyToManyPrompt(McpSyncClient mcpClient) {
        McpSchema.GetPromptResult prompt =
                assertDoesNotThrow(() -> mcpClient.getPrompt(new McpSchema.GetPromptRequest("many-to-many-micronaut-data-jdbc", Collections.emptyMap())));
        assertTrue(CollectionUtils.isNotEmpty(prompt.messages()));
        assertInstanceOf(McpSchema.TextContent.class, prompt.messages().get(0).content());
        assertTrue(((McpSchema.TextContent) prompt.messages().get(0).content()).text().contains("Many-to-Many relationship"));
    }

}
