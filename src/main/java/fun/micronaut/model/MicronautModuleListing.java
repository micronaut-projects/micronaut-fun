package fun.micronaut.model;

import io.micronaut.serde.annotation.Serdeable;

/**
 *
 * @param name The Module Name. For example Micronaut Security.
 * @param slug The Module slug. This matches repository name in the micronaut-projects GitHub Organization. E.g micronaut-security
 * @param description The Module Description
 * @param configurationReferenceUri The Micronaut Module MCP uri for the module's configuration reference documentation
 * @param documentationUri The Micronaut Module MCP uri for the module's documentation
 */
@Serdeable
public record MicronautModuleListing(
        String name,
        String slug,
        String description,
        String configurationReferenceUri,
        String documentationUri
) {
}
