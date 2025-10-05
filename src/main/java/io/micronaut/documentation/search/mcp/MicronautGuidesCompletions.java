package io.micronaut.documentation.search.mcp;

import io.micronaut.core.util.StringUtils;
import io.micronaut.documentation.search.guides.*;
import io.micronaut.mcp.annotations.ResourceCompletion;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.inject.Singleton;

import java.util.*;
import java.util.stream.Stream;

@Singleton
class MicronautGuidesCompletions {
    public static final int MAX = 100;
    private final GuidesFetcher guidesFetcher;
    private static final McpSchema.CompleteResult EMPTY = new McpSchema.CompleteResult(
            new McpSchema.CompleteResult.CompleteCompletion(List.of(" "), 0, false));

    MicronautGuidesCompletions(GuidesFetcher guidesFetcher) {
        this.guidesFetcher = guidesFetcher;
    }

    @ResourceCompletion(uri = "guidemetadata://{slug}")
    McpSchema.CompleteResult completeGuideSlug(String slug) {
        if (StringUtils.isEmpty(slug)) {
            return EMPTY;
        }
        return new McpSchema.CompleteResult(completeSlug(slug));
    }

    private McpSchema.CompleteResult.CompleteCompletion completeSlug(String slug) {
        List<String> slugs = guidesFetcher.findSlugBySlugStartingWith(slug);
        if (slugs.size() < MAX) {
            slugs = new ArrayList<>(slugs);
            slugs.addAll(guidesFetcher.findSlugBySlugContains(slug));
        }
        if (slugs.size() > MAX) {
            return new McpSchema.CompleteResult.CompleteCompletion(slugs.subList(0, MAX), slugs.size(), true);
        }
        return new McpSchema.CompleteResult.CompleteCompletion(slugs, 0, false);
    }

    @ResourceCompletion(uri = "guidehtml://{slug}/{buildTool}/{language}")
    McpSchema.CompleteResult completeGuideHtmlSlug(McpSchema.CompleteRequest completeRequest) {
        if (completeRequest.argument() == null) {
            return EMPTY;
        }
        return switch (completeRequest.argument().name()) {
            case "slug" -> new McpSchema.CompleteResult(completeSlug(completeRequest.argument().value()));
            case "buildTool" -> completeCompletion(findBuildToolsForSlug(completeRequest));
            case "language" -> completeCompletion(findLanguagesForSlug(completeRequest));
            default -> EMPTY;
        };
    }

    private List<String> findLanguagesForSlug(McpSchema.CompleteRequest completeRequest) {
        Optional<Guide> guideOptional = guide(completeRequest);
        if (guideOptional.isPresent()) {
            Guide guide = guideOptional.get();
            return completionsForLanguages(guide.options().stream().map(Option::language).toList());
        }
        return completions(Language.values());
    }

    private List<String> findBuildToolsForSlug(McpSchema.CompleteRequest completeRequest) {
        Optional<Guide> guideOptional = guide(completeRequest);
        if (guideOptional.isPresent()) {
            Guide guide = guideOptional.get();
            return completions(guide.options().stream().map(Option::buildTool).toList());
        }
        return completions(List.of(BuildTool.GRADLE, BuildTool.MAVEN));
    }

    private Optional<Guide> guide(McpSchema.CompleteRequest completeRequest) {
        Optional<String> slugOptional = slug(completeRequest);
        if (slugOptional.isPresent()) {
            return guidesFetcher.findBySlug(slugOptional.get());
        }
        return Optional.empty();
    }

    private Optional<String> slug(McpSchema.CompleteRequest completeRequest) {
        if (completeRequest.context() != null && completeRequest.context().arguments() != null) {
            String slug = completeRequest.context().arguments().get("slug");
            if (StringUtils.isNotEmpty(slug)) {
                return Optional.of(slug);
            }
        }
        return Optional.empty();
    }

    private List<String> completions(List<BuildTool> buildTools) {
        return buildTools.stream().map(BuildTool::name).toList();
    }

    private List<String> completionsForLanguages(List<Language> buildTools) {
        return buildTools.stream().map(Language::name).toList();
    }

    private List<String> completions(Language[] values) {
        return Stream.of(values).map(Language::name).toList();
    }

    private McpSchema.CompleteResult completeCompletion(List<String> values) {
        return new McpSchema.CompleteResult(new McpSchema.CompleteResult.CompleteCompletion(values, values.size(), false));
    }
}
