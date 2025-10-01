Please, write a test to verify Micronaut Serialization for ${className}.

The following tests shows how to test if a class is serializable. The following test verifies if the `GamePgnCreate` class is annotated with `@Serdeable`.

```java
package com.chessgamesarchive.forms;

import io.micronaut.context.BeanContext;
import io.micronaut.core.type.Argument;
import io.micronaut.serde.SerdeIntrospections;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class GamePgnCreateTest {

    @Inject
    BeanContext beanContext;

    @Test
    void isDeserializable() {
        SerdeIntrospections introspections = assertDoesNotThrow(() -> beanContext.getBean(SerdeIntrospections.class));
        assertDoesNotThrow(() -> introspections.getDeserializableIntrospection(Argument.of(GamePgnCreate.class)));
    }

    @Test
    void isSerializable() {
        SerdeIntrospections introspections = assertDoesNotThrow(() -> beanContext.getBean(SerdeIntrospections.class));
        assertDoesNotThrow(() -> introspections.getSerializableIntrospection(Argument.of(GamePgnCreate.class)));
    }
}
```