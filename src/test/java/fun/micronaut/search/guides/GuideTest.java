package fun.micronaut.search.guides;

import fun.micronaut.model.Guide;
import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GuideTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Guide.class));
    }
}