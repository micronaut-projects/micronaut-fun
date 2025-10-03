@Requires(property = "micronaut.opensearch.enabled", notEquals = StringUtils.FALSE, defaultValue = StringUtils.TRUE)
@Configuration
package  io.micronaut.documentation.search.opensearch;

import io.micronaut.context.annotation.Configuration;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;