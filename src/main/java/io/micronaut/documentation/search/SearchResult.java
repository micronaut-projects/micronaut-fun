package io.micronaut.documentation.search;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record SearchResult(
        String id,
    String title,
    String description,
    String content,
    String link
) {}