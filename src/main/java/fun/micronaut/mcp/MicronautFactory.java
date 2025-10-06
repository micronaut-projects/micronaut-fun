package fun.micronaut.mcp;

import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Factory;
import fun.micronaut.conf.MicronautModule;
import fun.micronaut.httpclients.CoreDocsClient;
import fun.micronaut.httpclients.MicronautProjectsGithubClient;
import io.micronaut.http.MediaType;
import io.modelcontextprotocol.server.McpStatelessServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.function.Function;

@Factory
class MicronautFactory {
    private final MicronautProjectsGithubClient micronautProjectsGithubClient;

    MicronautFactory(MicronautProjectsGithubClient micronautProjectsGithubClient) {
        this.micronautProjectsGithubClient = micronautProjectsGithubClient;
    }

    @Named("#{T(Math).random()}-configuration-reference")
    @EachBean(MicronautModule.class)
    @Singleton
    McpStatelessServerFeatures.SyncResourceSpecification configurationReferenceResourceSpecification(MicronautModule module) {
        return new McpStatelessServerFeatures.SyncResourceSpecification(configurationReferenceResource(module),
                (mcpTransportContext, readResourceRequest) ->
                        configurationReferenceReadResourceResult(module.getSlug(), micronautProjectsGithubClient::snapshotConfigurationReference));
    }

    @Singleton
    @Named("micronaut-core-reference-specification")
    McpStatelessServerFeatures.SyncResourceSpecification coreReferenceResourceSpecification(CoreDocsClient coreDocsClient) {
        final String slug = "micronaut-core";
        return new McpStatelessServerFeatures.SyncResourceSpecification(configurationReferenceResource("Micronaut Core", slug),
                    (mcpTransportContext, readResourceRequest) ->
                            configurationReferenceReadResourceResult(slug, s -> coreDocsClient.latestConfigurationReference()));
    }

    @EachBean(MicronautModule.class)
    @Singleton
    McpStatelessServerFeatures.SyncResourceSpecification moduleResourceSpecification(MicronautModule micronautModule) {
        return new McpStatelessServerFeatures.SyncResourceSpecification(moduleResource(micronautModule),
                (mcpTransportContext, readResourceRequest) ->
                    moduleReadResourceResult(micronautModule.getSlug(), micronautProjectsGithubClient::latest));
    }

    @Singleton
    @Named("micronaut-core")
    McpStatelessServerFeatures.SyncResourceSpecification coreResourceSpecification(CoreDocsClient coreDocsClient) {
        final String slug = "micronaut-core";
        return new McpStatelessServerFeatures.SyncResourceSpecification(moduleResource("Micronaut Core", slug),
                (mcpTransportContext, readResourceRequest) ->
                        moduleReadResourceResult(slug, s -> coreDocsClient.latestConfigurationReference()));
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
