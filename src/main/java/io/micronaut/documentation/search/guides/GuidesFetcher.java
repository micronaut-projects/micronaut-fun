package io.micronaut.documentation.search.guides;

import io.micronaut.cache.annotation.Cacheable;
import io.micronaut.core.annotation.NonNull;

import java.util.List;
import java.util.Optional;

public interface GuidesFetcher {
    Optional<Guide> findBySlug(String slug);

    @Cacheable
    @NonNull
    List<Guide> findAll();

    @NonNull
    Optional<String> findGuideSerialized(@NonNull String slug);

    @NonNull
    List<String> findSlugBySlugStartingWith(@NonNull String slug);

    @NonNull
    List<String> findSlugBySlugContains(@NonNull String slug);

    @NonNull
    Optional<String> findBySlugAndBuildAndLanguage(@NonNull String slug, @NonNull BuildTool build, @NonNull Language language);

    @NonNull
    List<String> findSlugBuildLangBySlugStartingWith(@NonNull String slugBuildLang);
}
