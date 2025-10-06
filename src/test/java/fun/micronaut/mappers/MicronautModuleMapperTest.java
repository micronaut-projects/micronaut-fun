package fun.micronaut.mappers;

import fun.micronaut.model.MicronautModuleListing;
import fun.micronaut.search.modules.MicronautModule;
import io.micronaut.context.BeanContext;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class MicronautModuleMapperTest {
    @Inject
    BeanContext beanContext;

    @Test
    void micronautModuleMapper(MicronautModuleMapper mapper) {
        Collection<MicronautModule> modules = beanContext.getBeansOfType(MicronautModule.class);
        MicronautModule micronautSecurity = modules.stream().filter(m -> m.getSlug().equals("micronaut-security")).findFirst().orElseThrow();
        MicronautModuleListing mnSecurity = mapper.map(micronautSecurity);
        assertEquals("micronaut-security", mnSecurity.slug());
        assertEquals("Micronaut Security", mnSecurity.name());
        assertEquals("Built-in security features. Authentication providers and strategies, Token Propagation...", mnSecurity.description());
        assertEquals("configurationreference://micronaut-security", mnSecurity.configurationReferenceUri());
        assertEquals("module://micronaut-security", mnSecurity.documentationUri());

    }
}