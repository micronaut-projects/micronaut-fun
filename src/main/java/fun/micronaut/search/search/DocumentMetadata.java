package fun.micronaut.search.search;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record DocumentMetadata(
    MetadataAttributes metadataAttributes
) {
    @Serdeable
    public record MetadataAttributes(
        String customized_url_source
    ) {}
}