package fun.micronaut.search.modules;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

@Introspected
@Serdeable
public record MicronautModuleListing(
        String name,
        String slug,
        String title,
        String description,
        String configurationReferenceUri,
        String documentationUri
) {
}
