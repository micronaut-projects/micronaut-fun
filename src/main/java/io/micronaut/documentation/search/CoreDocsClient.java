package io.micronaut.documentation.search;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.client.annotation.Client;

@Client("https://docs.micronaut.io")
public interface CoreDocsClient {

    @Consumes(MediaType.TEXT_HTML)
    @Get("/latest/guide/index.html")
    String latest();

    @Consumes(MediaType.TEXT_HTML)
    @Get("/latest/guide/configurationreference.html")
    String configurationReference();
}
