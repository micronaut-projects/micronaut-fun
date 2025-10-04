package io.micronaut.documentation.search.mcp;

import io.micronaut.documentation.search.guides.Guides;
import io.micronaut.documentation.search.guides.GuidesFetcher;
import io.micronaut.mcp.annotations.Tool;
import jakarta.inject.Singleton;

@Singleton
class MicronautGuidesTools {
    private final GuidesFetcher guidesFetcher;

    MicronautGuidesTools(GuidesFetcher guidesFetcher) {
        this.guidesFetcher = guidesFetcher;
    }

    @Tool(title = "Micronaut Guides Listing",
            description = "Obtains a listing of Micronaut Guides. For each guide, a title, description, slug, and URI are returned. The URI is the URI of an MCP Resource of this server which can be used to obtain more metadata about the guide")
    Guides guides() {
        return guidesFetcher.findGuides();
    }
}
