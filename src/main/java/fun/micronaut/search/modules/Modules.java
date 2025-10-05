package fun.micronaut.search.modules;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.jsonschema.JsonSchema;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@JsonSchema
@Serdeable
@Introspected
public record Modules(
        List<MicronautModuleListing> modules
) {
}
