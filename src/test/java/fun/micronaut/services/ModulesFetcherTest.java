package fun.micronaut.services;

import fun.micronaut.model.Modules;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class ModulesFetcherTest {
    @Test
    void fetchModules(ModulesFetcher fetcher) {
        Modules modules = assertDoesNotThrow(fetcher::modules);
        assertFalse(modules.modules().isEmpty());
    }

}