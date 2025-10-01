package io.micronaut.documentation.search;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.client.annotation.Client;

@Client("https://micronaut-projects.github.io")
public interface MicronautProjectsGithubClient {

    @Consumes(MediaType.TEXT_HTML)
    @Get("/{slug}/latest/guide/index.html")
    String coreLatest(@PathVariable String slug);

    @Consumes(MediaType.TEXT_HTML)
    @Get("/{slug}/latest/guide/index.html")
    String latest(@PathVariable String slug);

    @Consumes(MediaType.TEXT_HTML)
    @Get("/{slug}/latest/guide/configurationreference.html")
    String configurationReference(@PathVariable String slug);
}
