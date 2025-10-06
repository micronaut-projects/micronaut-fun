package fun.micronaut.search.guides;

import fun.micronaut.model.GuideListing;
import io.micronaut.context.BeanContext;
import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class GuideListingTest {

    @Inject
    BeanContext beanContext;

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(GuideListing.class));
    }

//    @Test
//    void isDeserializable() {
//        SerdeIntrospections introspections = assertDoesNotThrow(() -> beanContext.getBean(SerdeIntrospections.class));
//        assertDoesNotThrow(() -> introspections.getDeserializableIntrospection(Argument.of(GuideListing.class)));
//    }
//
//    @Test
//    void isSerializable() {
//        SerdeIntrospections introspections = assertDoesNotThrow(() -> beanContext.getBean(SerdeIntrospections.class));
//        assertDoesNotThrow(() -> introspections.getSerializableIntrospection(Argument.of(GuideListing.class)));
//    }
}