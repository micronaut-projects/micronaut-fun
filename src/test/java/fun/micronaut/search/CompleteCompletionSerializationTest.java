package fun.micronaut.search;

import io.micronaut.json.JsonMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
class CompleteCompletionSerializationTest {

    @Disabled("https://github.com/micronaut-projects/micronaut-mcp/pull/74")
    @Test
    void codeCompletionSerialization(JsonMapper jsonMapper) throws IOException {
        McpSchema.CompleteResult.CompleteCompletion codeComplete = new McpSchema.CompleteResult.CompleteCompletion(Collections.emptyList(), 0, false);
        String json = jsonMapper.writeValueAsString(codeComplete);
        String expected = "{\"values\":[],\"total\":0,\"hasMore\":false}";
        assertEquals(expected, json, json);
    }
}
