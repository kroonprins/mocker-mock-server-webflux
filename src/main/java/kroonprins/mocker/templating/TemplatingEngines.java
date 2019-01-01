package kroonprins.mocker.templating;

import com.fasterxml.jackson.annotation.JsonCreator;
import kroonprins.mocker.templating.jinjava.JinjavaTemplatingContext;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public enum TemplatingEngines {

    NONE(serverRequest -> Mono.just(EmptyTemplatingContext.CONTEXT)),

    MUSTACHE(DefaultTemplatingContext::fromServerRequest),

    JINJAVA(JinjavaTemplatingContext::fromServerRequest),

    FREEMARKER(DefaultTemplatingContext::fromServerRequest);

    private final Function<ServerRequest, Mono<TemplatingContext>> templatingContextCreator;

    TemplatingEngines(Function<ServerRequest, Mono<TemplatingContext>> templatingContextCreator) {
        this.templatingContextCreator = templatingContextCreator;
    }

    public Mono<TemplatingContext> createTemplatingContext(ServerRequest serverRequest) {
        return this.templatingContextCreator.apply(serverRequest);
    }

    @JsonCreator
    public static TemplatingEngines forValue(String value) {
        for (TemplatingEngines templatingEngine : TemplatingEngines.values()) {
            if (templatingEngine.name().equalsIgnoreCase(value)) {
                return templatingEngine;
            }
        }
        throw new IllegalArgumentException("No constant with text " + value + " found");
    }
}
