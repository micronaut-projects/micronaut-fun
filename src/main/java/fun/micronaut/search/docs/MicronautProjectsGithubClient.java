package fun.micronaut.search.docs;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.client.annotation.Client;

@Client(id = "micronautprojects")
public interface MicronautProjectsGithubClient {

    @Consumes(MediaType.TEXT_HTML)
    @Get("/{slug}/latest/guide/index.html")
    String coreLatest(@PathVariable String slug);

    @Consumes(MediaType.TEXT_HTML)
    @Get("/{slug}/latest/guide/index.html")
    String latest(@PathVariable String slug);

    @Consumes(MediaType.TEXT_HTML)
    @Get("/{slug}/latest/guide/configurationreference.html")
    String latestConfigurationReference(@PathVariable String slug);

    @Consumes(MediaType.TEXT_HTML)
    @Get("/{slug}/snapshot/guide/index.html")
    String snapshot(@PathVariable String slug);

    @Consumes(MediaType.TEXT_HTML)
    @Get("/{slug}/snapshot/guide/configurationreference.html")
    String snapshotConfigurationReference(@PathVariable String slug);
}
