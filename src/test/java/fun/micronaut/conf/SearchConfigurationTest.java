package fun.micronaut.conf;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class SearchConfigurationTest {
    @Test
    void testDefaultNumberOfResults(SearchConfiguration searchConfiguration) {
        assertEquals(10, searchConfiguration.getResultsPerPage());
    }
}