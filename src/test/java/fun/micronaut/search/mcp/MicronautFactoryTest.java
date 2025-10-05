package fun.micronaut.search.mcp;

import io.micronaut.context.BeanContext;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.modelcontextprotocol.server.McpStatelessServerFeatures;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class MicronautFactoryTest {

    @Inject
    BeanContext beanContext;

    @Test
    void resources() {
        Collection<BeanDefinition<McpStatelessServerFeatures.SyncResourceSpecification>> resourcesSpecs = beanContext.getBeanDefinitions(McpStatelessServerFeatures.SyncResourceSpecification.class);
        assertNotNull(resourcesSpecs);
        assertFalse(resourcesSpecs.isEmpty());
    }
}