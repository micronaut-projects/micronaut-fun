package fun.micronaut.model;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.jsonschema.JsonSchema;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

/**
 * JSON Array of every Micronaut Module represented by an instance of {@link MicronautModuleListing}.
 * @param modules Micronaut Module
 */
@JsonSchema
@Serdeable
@Introspected
public record Modules(List<MicronautModuleListing> modules) {
}
