package kroonprins.mocker.templating;

import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

public interface TemplatingContext {
    static Mono<TemplatingContext> fromServerRequest(ServerRequest serverRequest, TemplatingEngines templatingEngine) {
        return templatingEngine.createTemplatingContext(serverRequest);
    }
}
