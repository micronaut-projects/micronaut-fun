package fun.micronaut.services;

import fun.micronaut.model.SearchResult;
import io.micronaut.core.annotation.NonNull;

import java.util.List;
import java.util.Optional;

public interface SearchService {
    @NonNull
    List<SearchResult> search(@NonNull String query);

    @NonNull
    Optional<SearchResult> findById(@NonNull String id);
}
