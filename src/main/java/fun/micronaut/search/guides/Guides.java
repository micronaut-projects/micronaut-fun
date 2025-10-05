package fun.micronaut.search.guides;

import io.micronaut.jsonschema.JsonSchema;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@JsonSchema
@Serdeable
public record Guides(List<GuideListing> guides) {
}
