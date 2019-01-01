package kroonprins.mocker.templating;

import kroonprins.mocker.templating.model.Request;
import kroonprins.mocker.templating.model.Response;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpCookie;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Builder
@Value
public class DefaultTemplatingContext implements TemplatingContext {
    private final Request req;
    private final Response res;

    public static Mono<TemplatingContext> fromServerRequest(ServerRequest serverRequest) {
        return getBody(serverRequest)
                .map(body ->
                        DefaultTemplatingContext.builder()
                                .req(
                                        Request.builder()
                                                .path(serverRequest.path())
                                                .originalUrl(serverRequest.uri().getRawPath() + (StringUtils.isNotBlank(serverRequest.uri().getRawQuery()) ? "?" + serverRequest.uri().getRawQuery() : ""))
                                                .method(serverRequest.method())
                                                .params(serverRequest.pathVariables())
                                                .query(serverRequest.queryParams().toSingleValueMap()) // TODO toSingleValueMap means possibly losing data
                                                .headers(serverRequest.headers().asHttpHeaders().toSingleValueMap().entrySet().stream().filter(header -> !"cookie" .equalsIgnoreCase(header.getKey()))
                                                        .collect(Collectors.toMap(
                                                                Map.Entry::getKey,
                                                                Map.Entry::getValue
                                                        )))
                                                .cookies(serverRequest.cookies().toSingleValueMap().values().stream()
                                                        .collect(Collectors.toMap(
                                                                HttpCookie::getName,
                                                                HttpCookie::getValue
                                                        )))
                                                .body(body)
                                                .build()
                                )
                                .res(
                                        Response.builder()
                                                // TODO
                                                .build()
                                )
                                .build());
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
