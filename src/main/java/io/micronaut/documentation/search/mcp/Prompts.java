package io.micronaut.documentation.search.mcp;

import io.micronaut.mcp.annotations.Prompt;
import io.micronaut.mcp.annotations.PromptArg;
import jakarta.inject.Singleton;

@Singleton
class Prompts {

    @Prompt(name = "introspection-testing", title = "Introspection-Testing", description = "Test whether a class is introspected in a Micronaut application")
    String introspectionTesting(@PromptArg(description = "The class for which you want to test introspection") String className) {
        return String.format("""
    Please, write a test to verify introspection for %s
    
    The following tests shows how to test if a class is introspected. The following test verifies if the `CreateGame` class is annotated with `@Introspected`.
    
    ```java
    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(CreateGame.class));
    }
    ``` 
        """, className);
    }

}
