package io.micronaut.documentation.search;

import jakarta.inject.Singleton;

@Singleton
class DefaultSummaryService implements SummaryService {

    @Override
    public String summary(String text) {
        return text;
    }
}
