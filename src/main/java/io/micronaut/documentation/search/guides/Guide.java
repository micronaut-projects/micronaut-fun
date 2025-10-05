package io.micronaut.documentation.search.guides;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.jsonschema.JsonSchema;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @param title              The guide's title
 * @param intro              The guide introduction
 * @param authors            The guide's authors
 * @param tags               The guide tags
 * @param category           The guide category
 * @param publicationDate    The guide publication date. It should follow the format YYYY-MM-DD*
 * @param slug               The guide's slug. If not specified, the guides folder is used
 * @param url                The guide url.
 */
@JsonSchema
@Serdeable
public record Guide(
        @NonNull
        String title,

        @NonNull
        String intro,

        @NonNull
        Set<String> authors,

        @NonNull
        List<String> tags,

        @NonNull
        String category,

        @NonNull
        String publicationDate,

        @NonNull
        String slug,

        @NonNull
        String url,

        @NonNull
        List<Option> options
) {
}
