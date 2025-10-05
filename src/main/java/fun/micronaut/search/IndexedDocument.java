package fun.micronaut.search;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record IndexedDocument(
    String id,
    String title,
    String content,
    String url
) {}