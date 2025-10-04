package io.micronaut.documentation.search.mcp;

import io.micronaut.core.util.StringUtils;
import io.micronaut.documentation.search.guides.GuidesFetcher;
import io.micronaut.mcp.annotations.ResourceCompletion;
import jakarta.inject.Singleton;

import java.util.Collections;
import java.util.List;

@Singleton
class MicronautGuidesCompletions {
    public static final int MAX = 100;
    private final GuidesFetcher guidesFetcher;
    private final List<String> empty = List.of(" ");

    MicronautGuidesCompletions(GuidesFetcher guidesFetcher) {
        this.guidesFetcher = guidesFetcher;
    }

    @ResourceCompletion(uri = "guidemetadata://{slug}")
    List<String> completeGuideSlug(String slug) {
        if (StringUtils.isEmpty(slug)) {
            return empty;
        }
        List<String> slugs = guidesFetcher.findSlugBySlugStartingWith(slug);
        if (slugs.size() > MAX) {
            return slugs.subList(0, MAX);
        }
        return slugs;
    }

    @ResourceCompletion(uri = "guidehtml://{slugBuildLang}")
    List<String> completeGuideHtmlSlug(String slugBuildLang) {
        if (StringUtils.isEmpty(slugBuildLang)) {
            return empty;
        }
        List<String> slugs = guidesFetcher.findSlugBuildLangBySlugStartingWith(slugBuildLang);
        if (slugs.size() > MAX) {
            return slugs.subList(0, MAX);
        }
        return slugs;
    }
}
