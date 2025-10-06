package fun.micronaut.search.search;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest(startApplication = false)
class MarkdownConversionServiceTest {
    @Test
    void testHtmlMarkdownConversion(MarkdownConversionService service) {
        String markdown = service.toMarkdown("""
                <article>
                <h1>Hello World!</h1>
                <p> This is a test!</p>
                </article>
                """);
        String expectedMarkdown = """
Hello World!
============

This is a test!
        """;
        assertEquals(expectedMarkdown, markdown);


    }

}