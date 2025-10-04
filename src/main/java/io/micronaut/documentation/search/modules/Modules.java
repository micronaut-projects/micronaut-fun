package io.micronaut.documentation.search.modules;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.jsonschema.JsonSchema;

import java.util.List;

@JsonSchema
@Introspected
public record Modules(
        List<MicronautModuleListing> modules
) {
}
