package fun.micronaut.mcp;

import fun.micronaut.conf.MicronautModule;
import fun.micronaut.httpclients.CoreDocsClient;
import fun.micronaut.httpclients.MicronautProjectsGithubClient;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.MediaType;
import io.modelcontextprotocol.server.McpStatelessServerFeatures;
import io.modelcontextprotocol.server.McpStatelessSyncServer;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.function.Function;

@Singleton
class McpStatelessSyncServerBeanCreatedEventListener
        implements BeanCreatedEventListener<McpStatelessSyncServer> {
    private final List<MicronautModule> modules;
    private final CoreDocsClient coreDocsClient;
    private final MicronautProjectsGithubClient micronautProjectsGithubClient;
    McpStatelessSyncServerBeanCreatedEventListener(List<MicronautModule> modules,
                                                   CoreDocsClient coreDocsClient,
                                                   MicronautProjectsGithubClient micronautProjectsGithubClient) {
        this.modules = modules;
        this.coreDocsClient = coreDocsClient;
        this.micronautProjectsGithubClient = micronautProjectsGithubClient;
    }

    @Override
    public McpStatelessSyncServer onCreated(@NonNull BeanCreatedEvent<McpStatelessSyncServer> event) {
        McpStatelessSyncServer server = event.getBean();
        modules.sort((o1, o2) -> o1.getSlug().compareTo(o2.getSlug()));
        for (MicronautModule module : modules) {
            server.addResource(configurationReferenceResourceSpecification(module));
            server.addResource(moduleResourceSpecification(module));
        }
        server.addResource(coreResourceSpecification());
        server.addResource(coreReferenceResourceSpecification());
        return server;
    }

    McpStatelessServerFeatures.SyncResourceSpecification configurationReferenceResourceSpecification(MicronautModule module) {
        return new McpStatelessServerFeatures.SyncResourceSpecification(configurationReferenceResource(module),
                (mcpTransportContext, readResourceRequest) ->
                        configurationReferenceReadResourceResult(module.getSlug(), micronautProjectsGithubClient::snapshotConfigurationReference));
    }

    McpStatelessServerFeatures.SyncResourceSpecification moduleResourceSpecification(MicronautModule micronautModule) {
        return new McpStatelessServerFeatures.SyncResourceSpecification(moduleResource(micronautModule),
                (mcpTransportContext, readResourceRequest) ->
                        moduleReadResourceResult(micronautModule.getSlug(), micronautProjectsGithubClient::latest));
    }

    McpStatelessServerFeatures.SyncResourceSpecification coreReferenceResourceSpecification() {
        final String slug = "micronaut-core";
        return new McpStatelessServerFeatures.SyncResourceSpecification(configurationReferenceResource("Micronaut Core", slug),
                (mcpTransportContext, readResourceRequest) ->
                        configurationReferenceReadResourceResult(slug, s -> coreDocsClient.latestConfigurationReference()));
    }

    McpStatelessServerFeatures.SyncResourceSpecification coreResourceSpecification() {
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
