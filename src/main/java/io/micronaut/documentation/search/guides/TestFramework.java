package io.micronaut.documentation.search.guides;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public enum TestFramework {
    JUNIT,
    SPOCK,
    KOTLINTEST,
    KOTEST;

    public String toString() {
        return this.name().toLowerCase();
    }
}
