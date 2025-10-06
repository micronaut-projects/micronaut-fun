package fun.micronaut.search.search;

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotBlank;

@Singleton
class DefaultMarkdownConversionService implements MarkdownConversionService {

    private final FlexmarkHtmlConverter converter = FlexmarkHtmlConverter.builder()
            .build();

    @Override
    @NonNull
    public String toMarkdown(@NonNull @NotBlank String html) {
        return converter.convert(html);
    }
}

