package fun.micronaut.services;

import fun.micronaut.model.SearchResult;
import io.micronaut.context.annotation.Secondary;
import jakarta.inject.Singleton;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Secondary
@Singleton
class SearchServiceVoid implements SearchService {
    @Override
    public List<SearchResult> search(String query) {
        return Collections.emptyList();
    }

    @Override
    public Optional<SearchResult> findById(String id) {
        return Optional.empty();
    }
}
