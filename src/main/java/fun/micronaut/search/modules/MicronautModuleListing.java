package fun.micronaut.search.modules;

import io.micronaut.core.annotation.Introspected;

@Introspected
public record MicronautModuleListing(
        String name,
        String slug,
        String title,
        String description,
        String configurationReferenceUri,
        String documentationUri
) {
}
