package fun.micronaut.search.guides;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.naming.Named;
import java.util.Locale;

public enum ApplicationType implements Named {
    DEFAULT("Micronaut Application", "A Micronaut Application"),
    CLI("Micronaut CLI Application", "A Command Line Application"),
    FUNCTION("Micronaut Serverless Function", "A Function Application for Serverless"),
    GRPC("Micronaut gRPC Application", "A gRPC Application"),
    MESSAGING("Micronaut Messaging Application", "A Messaging-Driven Application");

    public static final ApplicationType DEFAULT_OPTION = DEFAULT;
    private final String title;
    private final String description;

    private ApplicationType(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public @NonNull String getName() {
        return this.name().toLowerCase(Locale.ENGLISH);
    }
}