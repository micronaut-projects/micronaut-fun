package fun.micronaut.services;

import fun.micronaut.mappers.GuideMapper;
import fun.micronaut.model.*;
import io.micronaut.cache.annotation.CacheConfig;
import io.micronaut.cache.annotation.Cacheable;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.json.JsonMapper;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
@CacheConfig
class DefaultGuidesFetcher implements GuidesFetcher {
    private static final List<String> BUILD_TOOL_OPTIONS = List.of("maven", "gradle");
    private static final List<String> LANGUAGES_OPTIONS = List.of("java", "groovy", "kotlin");
    private final GuidesHttpClient guidesHttpClient;
    private final JsonMapper jsonMapper;
    private final GuideMapper guideMapper;

    public DefaultGuidesFetcher(GuidesHttpClient guidesHttpClient,
                                JsonMapper jsonMapper,
                                GuideMapper guideMapper) {
        this.guidesHttpClient = guidesHttpClient;
        this.jsonMapper = jsonMapper;
        this.guideMapper = guideMapper;
    }

    @Cacheable(cacheNames = "findGuides")
    @Override
    @NonNull
    public Guides findGuides() {
        return new Guides(findAll().stream().map(guideMapper::map).toList());
    }

    @Cacheable(parameters = {"slug"}, cacheNames = "guidesFindBySlug")
    @Override
    @NonNull
    public Optional<Guide> findBySlug(@NonNull String slug) {
        return findAll().stream().filter(guide -> guide.slug().equals(slug)).findFirst();
    }

    @Cacheable(cacheNames = "guidesFindAll")
    @Override
    @NonNull
    public List<Guide> findAll() {
        return guidesHttpClient.guides();
    }

    @Cacheable(parameters = {"slug"}, cacheNames = "guidesFindGuideSerialized")
    @Override
    @NonNull
    public Optional<String> findGuideSerialized(@NonNull String slug) {
        Optional<Guide> guideOptional = findBySlug(slug);
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

    @Cacheable(cacheNames = "guidesFindSlug")
    List<String> findSlug() {
        return findAll().stream().map(Guide::slug).toList();
    }

    @Cacheable(parameters = {"slug"}, cacheNames = "guidesfindSlugBySlugStartingWith")
    @Override
    @NonNull
    public List<String> findSlugBySlugStartingWith(@NonNull String slug) {
        return findSlug().stream().filter(s -> s.startsWith(slug)).toList();
    }

    @Cacheable(parameters = {"slug"}, cacheNames = "guidesfindSlugBySlugContains")
    @Override
    @NonNull
    public List<String> findSlugBySlugContains(@NonNull String slug) {
        return findSlug().stream().filter(s -> s.contains(slug)).toList();
    }

    @Cacheable(parameters = {"slug", "buildTool", "language"}, cacheNames = "guidesFindBySlugAndBuildAndLanguage")
    @NonNull
    public Optional<String> findBySlugAndBuildAndLanguage(@NonNull String slug, @NonNull BuildTool buildTool, @NonNull Language language) {
        String slugBuildLang = String.join("-", slug, buildTool.name().toLowerCase(), language.name().toLowerCase());
        return guidesHttpClient.findGuideHtml(slugBuildLang);
    }

    @Cacheable(parameters = {"slugBuildLang"}, cacheNames = "guidesFindSlugBuildLangBySlugStartingWith")
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
