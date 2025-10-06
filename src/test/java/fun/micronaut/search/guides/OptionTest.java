package fun.micronaut.search.guides;

import fun.micronaut.model.Option;
import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OptionTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Option.class));
    }
}