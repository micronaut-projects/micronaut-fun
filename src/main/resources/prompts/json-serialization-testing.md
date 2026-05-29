# How to verify serialization from Java to JSON behaves?

Given the following record: 

```java
package training.questionnaire.repositories;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;

@Serdeable
public record Topic(
    @NonNull @NotBlank String id, 
    @NonNull @NotBlank String name) {}
```

In the test, we inject `JsonMapper`. Those familiar with Jackson Databind's `ObjectMapper` API will find the API quite similar. You can test it serializes to JSON as expected: 

```java
package training.questionnaire.repositories;

import io.micronaut.json.JsonMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)class TopicTest {

    @Test
    void topicJsonSerialization(JsonMapper jsonMapper) 
        throws IOException {
        Topic topic = new Topic("1", "Oracle Cloud Infrastructure Fundamentals");
        String json = jsonMapper.writeValueAsString(topic);
        assertEquals("""
    {"id":"1","name":"Oracle Cloud Infrastructure Fundamentals"}""", json);
    }
}
```
