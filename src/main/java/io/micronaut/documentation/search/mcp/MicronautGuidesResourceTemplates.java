package io.micronaut.documentation.search.mcp;

import io.micronaut.documentation.search.guides.BuildTool;
import io.micronaut.documentation.search.guides.GuidesFetcher;
import io.micronaut.documentation.search.guides.Language;
import io.micronaut.json.JsonMapper;
import io.micronaut.mcp.annotations.ResourceTemplate;
import io.modelcontextprotocol.spec.McpError;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.inject.Singleton;
import io.micronaut.http.MediaType;
import java.util.Optional;

@Singleton
class MicronautGuidesResourceTemplates {
    private final GuidesFetcher guidesFetcher;

    MicronautGuidesResourceTemplates(GuidesFetcher guidesFetcher) {
        this.guidesFetcher = guidesFetcher;
    }

    @ResourceTemplate(uriTemplate = "guidemetadata://{slug}",
            mimeType = MediaType.APPLICATION_JSON,
            title = "Micronaut Guide Metadata",
            description = "returns a guide metadata as a JSON Object. The JSON object comforms to the JSON Schema https://micronaut.fun/schemas/guide.schema.json")
    String guideMetadata(String slug) {
        Optional<String> json = guidesFetcher.findGuideSerialized(slug);
        if (json.isPresent()) {
            return json.get();
        }
        throw new McpError(new McpSchema.JSONRPCResponse.JSONRPCError(McpSchema.ErrorCodes.RESOURCE_NOT_FOUND, "guide not found wiht slug " + slug, null));
    }

    @ResourceTemplate(uriTemplate = "guidehtml://{slugBuildLang}",
            mimeType = MediaType.TEXT_HTML,
            title = "Micronaut Guide",
            description = "returns a Guide HTML Page")
    String guideHtml(String slugBuildLang) {
        Optional<String> html = guidesFetcher.findBySlugBuildLang(slugBuildLang);
        if (html.isPresent()) {
            return html.get();
        }
        throw new McpError(new McpSchema.JSONRPCResponse.JSONRPCError(McpSchema.ErrorCodes.RESOURCE_NOT_FOUND, "guide not found with slug " + slugBuildLang, null));
    }
}
