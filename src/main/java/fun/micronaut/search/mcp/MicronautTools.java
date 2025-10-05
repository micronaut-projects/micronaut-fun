package fun.micronaut.search.mcp;

import fun.micronaut.search.guides.Guides;
import fun.micronaut.search.guides.GuidesFetcher;
import fun.micronaut.search.modules.Modules;
import fun.micronaut.search.modules.ModulesFetcher;
import io.micronaut.mcp.annotations.Tool;
import jakarta.inject.Singleton;

@Singleton
class MicronautTools {
    private final GuidesFetcher guidesFetcher;
    private final ModulesFetcher modulesFetcher;

    MicronautTools(GuidesFetcher guidesFetcher, ModulesFetcher modulesFetcher) {
        this.guidesFetcher = guidesFetcher;
        this.modulesFetcher = modulesFetcher;
    }

    @Tool(title = "Micronaut Guides Listing",
            description = "Obtains a listing of Micronaut Guides. For each guide, a title, description, slug, and URI are returned. The URI is the URI of an MCP Resource of this server which can be used to obtain more metadata about the guide")
    Guides guides() {
        return guidesFetcher.findGuides();
    }

    @Tool(title = "Micronaut Modules Listing",
            description = "Obtains a listing of Micronaut Modules. For each module you get title, description, slug, and configuration reference URI. The URI is the URI of an MCP Resource of this server which can be used to obtain the configuration reference of the module")
    Modules modules() {
        return modulesFetcher.modules();
    }
}
