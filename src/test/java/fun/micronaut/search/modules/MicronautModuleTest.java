package fun.micronaut.search.modules;

import fun.micronaut.conf.MicronautModule;
import fun.micronaut.model.SearchResult;
import io.micronaut.context.BeanContext;
import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.core.type.Argument;
import io.micronaut.serde.SerdeIntrospections;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class MicronautModuleTest {

    @Inject
    BeanContext beanContext;

    @Test
    void modulesAreLoadedViaConfig() {
        Collection<MicronautModule> modules = beanContext.getBeansOfType(MicronautModule.class);
        assertFalse(modules.isEmpty());
    }

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(MicronautModule.class));
    }

    @Test
    void isDeserializable() {
        SerdeIntrospections introspections = assertDoesNotThrow(() -> beanContext.getBean(SerdeIntrospections.class));
        assertDoesNotThrow(() -> introspections.getDeserializableIntrospection(Argument.of(MicronautModule.class)));
    }

    @Test
    void isSerializable() {
        SerdeIntrospections introspections = assertDoesNotThrow(() -> beanContext.getBean(SerdeIntrospections.class));
        assertDoesNotThrow(() -> introspections.getSerializableIntrospection(Argument.of(MicronautModule.class)));
    }
}