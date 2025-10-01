package io.micronaut.documentation.search;

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;

@Singleton
class DefaultMarkdownConversion implements MarkdownConversion {

    private final FlexmarkHtmlConverter converter = FlexmarkHtmlConverter.builder().build();

    @Override
    @NonNull
    public String toMarkdown(@Nullable String html) {
        if (html == null) {
            return "";
        }
        String md = converter.convert(html);
        // Normalize whitespace for better indexing and display
        md = md.replaceAll("\r\n", "\n").replaceAll("\n{3,}", "\n\n").trim();
        return md;
    }
}

