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
        return new McpStatelessServerFeatures.SyncResourceSpecification(configurationReferenceResource(micronautModule),
                (mcpTransportContext, readResourceRequest) ->
                        configurationReferenceReadResourceResult(micronautModule.getSlug(), micronautProjectsGithubClient::configurationReference));
    }

    @Singleton
    @Named("micronaut-core")
    McpStatelessServerFeatures.SyncResourceSpecification coreReferenceResourceSpecification(CoreDocsClient coreDocsClient) {
        final String slug = "micronaut-core";
        return new McpStatelessServerFeatures.SyncResourceSpecification(configurationReferenceResource("Micronaut Core", slug),
                    (mcpTransportContext, readResourceRequest) ->
                            configurationReferenceReadResourceResult(slug, s -> coreDocsClient.configurationReference()));
    }

    @EachBean(MicronautModule.class)
    McpStatelessServerFeatures.SyncResourceSpecification moduleResourceSpecification(MicronautModule micronautModule) {
        return new McpStatelessServerFeatures.SyncResourceSpecification(moduleResource(micronautModule),
                (mcpTransportContext, readResourceRequest) ->
                    configurationReferenceReadResourceResult(micronautModule.getSlug(), micronautProjectsGithubClient::latest));
    }

    @Singleton
    @Named("micronaut-core")
    McpStatelessServerFeatures.SyncResourceSpecification coreResourceSpecification(CoreDocsClient coreDocsClient) {
        final String slug = "micronaut-core";
        return new McpStatelessServerFeatures.SyncResourceSpecification(configurationReferenceResource("Micronaut Core", slug),
                (mcpTransportContext, readResourceRequest) ->
                        configurationReferenceReadResourceResult(slug, s -> coreDocsClient.configurationReference()));
    }

    private static McpSchema.ReadResourceResult configurationReferenceReadResourceResult(String slug, Function<String, String> slugToHtml) {
        McpSchema.TextResourceContents textResourceContents =
                new McpSchema.TextResourceContents(configurationReferenceUri(slug), MediaType.TEXT_HTML, slugToHtml.apply(slug));
        return new McpSchema.ReadResourceResult(List.of(textResourceContents));
    }

    private static McpSchema.Resource configurationReferenceResource(MicronautModule micronautModule) {
        return configurationReferenceResource(micronautModule.getTitle(), micronautModule.getSlug());
    }

    private static McpSchema.Resource configurationReferenceResource(String name, String slug) {
        return McpSchema.Resource.builder()
                .name(name + " Configuration Reference")
                .description("Configuration reference for the " + name + " module")
                .uri(configurationReferenceUri(slug))
                .mimeType(MediaType.TEXT_HTML)
                .build();
    }

    private static String configurationReferenceUri(String slug) {
        return "configurationreference://" + slug;
    }

    private static McpSchema.ReadResourceResult moduleReadResourceResult(String slug, Function<String, String> slugToHtml) {
        McpSchema.TextResourceContents textResourceContents =
                new McpSchema.TextResourceContents(moduleUri(slug), MediaType.TEXT_HTML, slugToHtml.apply(slug));
        return new McpSchema.ReadResourceResult(List.of(textResourceContents));
    }

    private static McpSchema.Resource moduleResource(MicronautModule micronautModule) {
        return moduleResource(micronautModule.getTitle(), micronautModule.getSlug());
    }

    private static McpSchema.Resource moduleResource(String name, String slug) {
        return McpSchema.Resource.builder()
                .name(name)
                .description("Documentation for the " + name + " module")
                .uri(moduleUri(slug))
                .mimeType(MediaType.TEXT_HTML)
                .build();
    }

    private static String moduleUri(String slug) {
        return "module://" + slug;
    }
}
