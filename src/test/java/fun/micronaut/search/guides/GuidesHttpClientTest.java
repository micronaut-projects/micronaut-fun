package fun.micronaut.search.guides;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class GuidesHttpClientTest {

    @Test
    void guidesJson(GuidesHttpClient guidesHttpClient) {
        List<Guide> guideList = assertDoesNotThrow(guidesHttpClient::guides);
        assertFalse(guideList.isEmpty());
    }
}