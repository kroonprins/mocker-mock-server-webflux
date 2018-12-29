package kroonprins.mocker.templating;

import org.springframework.web.reactive.function.server.ServerRequest;

public interface TemplatingContext {
    static TemplatingContext fromServerRequest(ServerRequest serverRequest, TemplatingEngines templatingEngine) {
        return templatingEngine.createTemplatingContext(serverRequest);
    }
}
