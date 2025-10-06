package fun.micronaut.services;

import fun.micronaut.model.BuildTool;
import fun.micronaut.model.Guide;
import fun.micronaut.model.Guides;
import fun.micronaut.model.Language;
import io.micronaut.core.annotation.NonNull;

import java.util.List;
import java.util.Optional;

public interface GuidesFetcher {
    Optional<Guide> findBySlug(String slug);

    @NonNull
    List<Guide> findAll();

    @NonNull
    Guides findGuides();

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
