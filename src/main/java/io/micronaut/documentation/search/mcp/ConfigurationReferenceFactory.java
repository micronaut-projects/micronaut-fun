package io.micronaut.documentation.search.mcp;

import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.documentation.search.modules.MicronautModule;
import io.micronaut.documentation.search.docs.CoreDocsClient;
import io.micronaut.documentation.search.docs.MicronautProjectsGithubClient;
import io.micronaut.http.MediaType;
import io.modelcontextprotocol.server.McpStatelessServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.function.Function;

@Factory
class ConfigurationReferenceFactory {
    private final MicronautProjectsGithubClient micronautProjectsGithubClient;

    ConfigurationReferenceFactory(MicronautProjectsGithubClient micronautProjectsGithubClient) {
        this.micronautProjectsGithubClient = micronautProjectsGithubClient;
    }

    @EachBean(MicronautModule.class)
    McpStatelessServerFeatures.SyncResourceSpecification configurationReferenceResourceSpecification(MicronautModule micronautModule) {
        return new McpStatelessServerFeatures.SyncResourceSpecification(resource(micronautModule),
                (mcpTransportContext, readResourceRequest) ->
                        readResourceResult(micronautModule.getSlug(), micronautProjectsGithubClient::configurationReference));
    }

    @Singleton
    @Named("micronaut-core")
    McpStatelessServerFeatures.SyncResourceSpecification coreReferenceResourceSpecification(CoreDocsClient coreDocsClient) {
        final String slug = "micronaut-core";
        return new McpStatelessServerFeatures.SyncResourceSpecification(resource("Micronaut Core", slug),
                    (mcpTransportContext, readResourceRequest) ->
                            readResourceResult(slug, s -> coreDocsClient.configurationReference()));
    }

    private static McpSchema.ReadResourceResult readResourceResult(String slug, Function<String, String> slugToHtml) {
        McpSchema.TextResourceContents textResourceContents =
                new McpSchema.TextResourceContents(uri(slug), MediaType.TEXT_HTML, slugToHtml.apply(slug));
        return new McpSchema.ReadResourceResult(List.of(textResourceContents));
    }

    private static McpSchema.Resource resource(MicronautModule micronautModule) {
        return resource(micronautModule.getTitle(), micronautModule.getSlug());
    }

    private static McpSchema.Resource resource(String name, String slug) {
        return McpSchema.Resource.builder()
                .name(name + " Configuration Reference")
                .description("Configuration reference for the " + name + " module")
                .uri(uri(slug))
                .mimeType(MediaType.TEXT_HTML)
                .build();
    }

    private static String uri(String slug) {
        return "configurationreference://" + slug;
    }
}
