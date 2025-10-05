package fun.micronaut.search;

import io.micronaut.core.order.Ordered;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.ServerHttpRequest;
import io.micronaut.http.annotation.ResponseFilter;
import io.micronaut.http.annotation.ServerFilter;
import io.micronaut.http.filter.ServerFilterPhase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@ServerFilter("/**")
class RequestLoggingFilter implements Ordered {
    private static final Logger LOG = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public int getOrder() {
        return ServerFilterPhase.TRACING.before();
    }

    @ResponseFilter
    void filterResponse(HttpRequest<?> request, HttpResponse<?> response) {
        log(request);
    }

    private static void log(HttpRequest<?> request) {
        if (request.getPath().startsWith("/assets")) {
            return;
        }
        if (request.getPath().startsWith("/health")) {
            return;
        }
        LOG.trace("{} {} Body: {} Authorization: {}",
                request.getMethod(),
                request.getPath(),
                body(request).orElse(""),
                request.getHeaders().getAuthorization().orElse("")
                );
    }

    private static Optional<String> body(HttpRequest<?> request) {
        try {
        if (request instanceof ServerHttpRequest serverHttpRequest && request.getMethod() == HttpMethod.POST) {
            return Optional.of(new String(serverHttpRequest.byteBody().split().toInputStream().readAllBytes(), StandardCharsets.UTF_8));
        }
        } catch (IOException e) {
            return Optional.empty();
        }
        return Optional.empty();
    }
}
