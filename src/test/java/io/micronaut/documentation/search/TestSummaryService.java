package io.micronaut.documentation.search;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;

@Singleton
@Replaces(DefaultSummaryService.class)
@Requires(env = "test")
class TestSummaryService implements SummaryService {
    @Override
    public String summary(String text) {
        if (text == null) return "";
        String normalized = text.replaceAll("\\s+", " ").trim();
        return normalized.length() <= 160 ? normalized : normalized.substring(0, 157) + "...";
    }
}

