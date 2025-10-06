package fun.micronaut.services;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public interface MarkdownConversionService {
    @NonNull
    String toMarkdown(@NonNull @NotBlank String html);
}

