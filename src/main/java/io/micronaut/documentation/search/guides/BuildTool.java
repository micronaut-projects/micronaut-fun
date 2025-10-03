package io.micronaut.documentation.search.guides;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public enum BuildTool {
    /**
     * Gradle with the Gradle DSL
     */
    GRADLE,
    /**
     * Gradle with the Kotlin DSL
     */
    GRADLE_KOTLIN,
    MAVEN
}
