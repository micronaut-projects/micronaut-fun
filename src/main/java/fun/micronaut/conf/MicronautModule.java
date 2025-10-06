package fun.micronaut.conf;

import io.micronaut.context.annotation.EachProperty;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.naming.Named;
import io.micronaut.serde.annotation.Serdeable;

/**
 * Micronaut Module.
 * Each module is defined in configuration.
 */
@EachProperty("modules")
@Serdeable
public class MicronautModule implements Named {

    private final String name;
    private String slug;
    private String title;
    private String description;

    /**
     *
     * @param name name qualifier
     */
    public MicronautModule(@Parameter String name) {
        this.name = name;
    }

    /**
     *
     * @return The name qualifier
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     *
     * @return The slug
     */
    public String getSlug() {
        return slug;
    }

    /**
     *
     * @param slug The slug
     */
    public void setSlug(String slug) {
        this.slug = slug;
    }

    /**
     *
     * @return the Micronaut Module title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
