package fun.micronaut.mcp;

import fun.micronaut.model.BuildTool;
import fun.micronaut.services.GuidesFetcher;
import fun.micronaut.model.Language;
import io.micronaut.mcp.annotations.ResourceTemplate;
import io.modelcontextprotocol.spec.McpError;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.inject.Singleton;
import io.micronaut.http.MediaType;

import java.util.List;
import java.util.Optional;

@Singleton
class MicronautResourceTemplates {
    private final GuidesFetcher guidesFetcher;

    MicronautResourceTemplates(GuidesFetcher guidesFetcher) {
        this.guidesFetcher = guidesFetcher;
    }

    @ResourceTemplate(uriTemplate = "guidemetadata://{slug}",
            mimeType = MediaType.APPLICATION_JSON,
            title = "Micronaut Guide Metadata",
            description = "returns a guide metadata as a JSON Object. The JSON object comforms to the JSON Schema https://micronaut.fun/schemas/guide.schema.json")
    McpSchema.ReadResourceResult guideMetadata(String slug) {
        Optional<String> json = guidesFetcher.findGuideSerialized(slug);
        if (json.isPresent()) {
            return new McpSchema.ReadResourceResult(List.of(new McpSchema.TextResourceContents("guidemetadata://"+ slug,
                    MediaType.APPLICATION_JSON,
                    json.get())));
        }
        throw new McpError(new McpSchema.JSONRPCResponse.JSONRPCError(McpSchema.ErrorCodes.RESOURCE_NOT_FOUND, "guide not found with slug " + slug, null));
    }

    @ResourceTemplate(uriTemplate = "guidehtml://{slug}/{buildTool}/{language}",
            mimeType = MediaType.TEXT_HTML,
            title = "Micronaut Guide",
            description = "returns a Guide HTML Page")
    String guideHtml(String slug, String buildTool, String language) {
        Optional<String> html = guidesFetcher.findBySlugAndBuildAndLanguage(slug,
                BuildTool.valueOf(buildTool),
                Language.valueOf(language));
        if (html.isPresent()) {
            return html.get();
        }
        throw new McpError(new McpSchema.JSONRPCResponse.JSONRPCError(McpSchema.ErrorCodes.RESOURCE_NOT_FOUND, "guide not found with slug " + slug, null));
    }
}
