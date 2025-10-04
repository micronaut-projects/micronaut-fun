package io.micronaut.documentation.search;

import io.micronaut.documentation.search.docs.MicronautProjectsGithubClient;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class SummaryServiceTest {

    @Test
    void getDocumentation(MicronautProjectsGithubClient client,
                          SummaryService summaryService,
                          MarkdownConversion markdownConversion) {
        String html = assertDoesNotThrow(() -> client.latest("micronaut-test"));
        assertNotNull(html);
        String markdown = markdownConversion.toMarkdown(html);
        System.out.println("=============");
        System.out.println(markdown);
        String description = assertDoesNotThrow(() -> summaryService.summary(markdown));
        System.out.println("=============");
        System.out.println(description);
        System.out.println("=============");
    }
}