package fun.micronaut.model;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.client.annotation.Client;
import java.util.Optional;
import java.util.List;

@Client(id = "mnguides")
public interface GuidesHttpClient {
    @Get("/latest/guides.json")
    List<Guide> guides();

    @Produces(MediaType.TEXT_HTML)
    @Get("/latest/{slug}.html")
    Optional<String> findGuideHtml(String slug);
}
