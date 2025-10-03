package io.micronaut.documentation.search.guides;

import io.micronaut.cache.annotation.CacheConfig;
import io.micronaut.cache.annotation.CachePut;
import io.micronaut.cache.annotation.Cacheable;
import io.micronaut.cache.annotation.CachePut;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.json.JsonMapper;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
@CacheConfig("guides")
class DefaultGuidesFetcher implements GuidesFetcher {
    public static final List<String> BUILD_TOOL_OPTIONS = List.of("maven", "gradle");
    public static final List<String> LANGUAGES_OPTIONS = List.of("java", "groovy", "kotlin");
    private final GuidesHttpClient guidesHttpClient;
    private final JsonMapper jsonMapper;

    public DefaultGuidesFetcher(GuidesHttpClient guidesHttpClient, JsonMapper jsonMapper) {
        this.guidesHttpClient = guidesHttpClient;
        this.jsonMapper = jsonMapper;
    }

    @CachePut(parameters = {"slug"})
    @Override
    @NonNull
    public Optional<Guide> findById(@NonNull String slug) {
        return findAll().stream().filter(guide -> guide.slug().equals(slug)).findFirst();
    }

    @Cacheable
    @Override
    @NonNull
    public List<Guide> findAll() {
        return guidesHttpClient.guides();
    }

    @CachePut(parameters = {"slug"})
    @Override
    @NonNull
    public Optional<String> findGuideSerialized(@NonNull String slug) {
        Optional<Guide> guideOptional = findById(slug);
        if (guideOptional.isPresent()) {
            try {
                Guide guide = guideOptional.get();
                return Optional.of(jsonMapper.writeValueAsString(guide));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.empty();
    }

    @Cacheable
    List<String> findSlug() {
        return findAll().stream().map(Guide::slug).toList();
    }

    @CachePut(parameters = {"slug"})
    @Override
    public List<String> findSlugBySlugStartingWith(String slug) {
        return findSlug().stream().filter(s -> s.startsWith(slug) || s.contains(slug)).toList();
    }

    @CachePut(parameters = {"slugBuildLang"})
    @NonNull
    public Optional<String> findBySlugBuildLang(String slugBuildLang) {
        return guidesHttpClient.findGuideHtml(slugBuildLang);
    }

    @CachePut(parameters = {"slugBuildLang"})
    @Override
    @NonNull
    public List<String> findSlugBuildLangBySlugStartingWith(@NonNull String slugBuildLang) {
        List<String> buildTools = BUILD_TOOL_OPTIONS;
        List<String> languages = LANGUAGES_OPTIONS;

        return findSlug().stream()
                .flatMap(slug -> buildTools.stream()
                        .flatMap(buildTool -> languages.stream()
                                .map(language -> slugBuildLang(slug, buildTool, language))))
                .filter(s -> s.startsWith(slugBuildLang) || s.contains(slugBuildLang))
                .collect(Collectors.toList());
    }

    public String slugBuildLang(String slug, String buildTool, String language) {
        return String.join("-", slug, buildTool, language);
    }
}
