package io.micronaut.documentation.search;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;

public interface MarkdownConversion {
    @NonNull
    String toMarkdown(@Nullable String html);
}

