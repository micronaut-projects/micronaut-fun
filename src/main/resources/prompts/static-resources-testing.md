Please write a test to verify that a static resource is publicly accessible at ${path}.

The following test demonstrates how to verify that a static resource is accessible through its static resource configuration.
The following test verifies `/assets/css/style.css` is accessible.

```java
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@MicronautTest
class StaticResourceTest {

    @Test
    void stylesheetExists(@Client("/") HttpClient client) {
        assertDoesNotThrow(() -> client.toBlocking().retrieve("/assets/css/style.css"));
    }
}
```