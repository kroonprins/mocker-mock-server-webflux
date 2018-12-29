package kroonprins.mocker.templating.jinjava;

import kroonprins.mocker.templating.TemplatingContext;
import org.springframework.http.HttpCookie;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class JinjavaTemplatingContext extends HashMap<String, Object> implements TemplatingContext {

    public static Mono<TemplatingContext> fromServerRequest(ServerRequest serverRequest) {
        return Mono.zip(createRequestContext(serverRequest), createResponseContext(serverRequest), (request, response) -> {
            JinjavaTemplatingContext context = new JinjavaTemplatingContext();
            context.put("req", request);
            context.put("res", response);
            return context;
        });
    }

    // TODO avoid duplication with defaulttemplatingcontext
    private static Mono<Map<String, Object>> createRequestContext(ServerRequest serverRequest) {
        return getBody(serverRequest)
                .map(body -> {
                    Map<String, Object> request = new HashMap<>();
                    request.put("path", serverRequest.path());
                    request.put("originalUrl", serverRequest.uri().getRawPath() + "?" + serverRequest.uri().getRawQuery());
                    request.put("method", serverRequest.method());
                    request.put("params", serverRequest.pathVariables());
                    request.put("query", serverRequest.queryParams().toSingleValueMap()); // TODO toSingleValueMap means possibly losing data
                    request.put("headers", serverRequest.headers().asHttpHeaders().toSingleValueMap().entrySet().stream().filter(header -> !"cookie".equals(header.getKey()))
                            .collect(Collectors.toMap(
                                    Entry::getKey,
                                    Entry::getValue
                            )));
                    request.put("cookies", serverRequest.cookies().toSingleValueMap().values().stream()
                            .collect(Collectors.toMap(
                                    HttpCookie::getName,
                                    HttpCookie::getValue
                            )));
                    request.put("body", body);
                    return request;
                });
    }

    private static Mono<Object> createResponseContext(ServerRequest serverRequest) {
        // TODO
        return Mono.just("");
    }

    private static Mono<?> getBody(ServerRequest serverRequest) {
        Optional<MediaType> requestContentType = serverRequest.headers().contentType();
        return requestContentType.map(
                contentType -> {
                    if (contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
                        return serverRequest.bodyToMono(Map.class);
                    } else {
                        return serverRequest.bodyToMono(String.class);
                    }
                }
        ).orElseGet(() -> Mono.just(""));
    }
}
