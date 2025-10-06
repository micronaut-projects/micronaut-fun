package fun.micronaut.model;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;

/**
 *
 * @param title The guide's title
 * @param intro The guide introduction
 * @param slug  The guide's slug.
 * @param uri   The MCP Resource URI
 */
@Serdeable
public record GuideListing(
        @NonNull
        @NotBlank
        String title,

        @NonNull
        @NotBlank
        String intro,

        @NonNull
        String slug,

        @NonNull
        String uri
        ) {
}
