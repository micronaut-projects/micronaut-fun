package io.micronaut.documentation.search.guides;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.serde.annotation.Serdeable;

@Introspected
@Serdeable
public record Option(
        @NonNull
        BuildTool buildTool,

        @NonNull
        Language language,

        @NonNull
        String url) {
}
