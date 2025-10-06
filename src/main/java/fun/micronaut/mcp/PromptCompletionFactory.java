package fun.micronaut.mcp;

import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.mcp.primitives.prompts.ClasspathPrompt;
import io.modelcontextprotocol.common.McpTransportContext;
import io.modelcontextprotocol.server.McpStatelessServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.function.BiFunction;

@Factory
class PromptCompletionFactory {
    @EachBean(ClasspathPrompt.class)
    McpStatelessServerFeatures.SyncCompletionSpecification syncPromptSpecification(ClasspathPrompt classpathPrompt) {
        return new McpStatelessServerFeatures.SyncCompletionSpecification(new McpSchema.PromptReference(classpathPrompt.getName()), new BiFunction<McpTransportContext, McpSchema.CompleteRequest, McpSchema.CompleteResult>() {
            @Override
            public McpSchema.CompleteResult apply(McpTransportContext mcpTransportContext, McpSchema.CompleteRequest completeRequest) {
                return MicronautGuidesCompletions.EMPTY;
            }
        });
    }
}
