package fun.micronaut.search.modules;

import io.micronaut.context.annotation.EachProperty;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.naming.Named;
import io.micronaut.serde.annotation.Serdeable;

@EachProperty("modules")
@Introspected
@Serdeable
public class MicronautModule implements Named {

    private final String name;
    private String slug;
    private String title;
    private String description;

    public MicronautModule(@Parameter String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
