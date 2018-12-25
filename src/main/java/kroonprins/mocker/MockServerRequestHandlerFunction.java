package kroonprins.mocker;

import kroonprins.mocker.model.Rule;
import kroonprins.mocker.model.TemplatedRule;
import kroonprins.mocker.templating.RuleTemplatingService;
import kroonprins.mocker.templating.TemplatingContext;
import kroonprins.mocker.templating.TemplatingService;
import kroonprins.mocker.templating.model.Request;
import kroonprins.mocker.templating.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
public class MockServerRequestHandlerFunction implements HandlerFunction<ServerResponse> {

    private final Rule rule;
    private final RuleTemplatingService ruleTemplatingService;

    public MockServerRequestHandlerFunction(Rule rule, RuleTemplatingService ruleTemplatingService) {
        this.rule = rule;
        this.ruleTemplatingService = ruleTemplatingService;
    }

    @Override
    public Mono<ServerResponse> handle(ServerRequest serverRequest) {
        return Mono.just(serverRequest)
                .map(this::createTemplatingContext)
                .flatMap(templatingContext -> ruleTemplatingService.template(rule, templatingContext))
                .flatMap(this::createResponse);
    }

    private TemplatingContext createTemplatingContext(ServerRequest serverRequest) {
        return TemplatingContext.fromServerRequest(serverRequest);
    }

    private Mono<ServerResponse> createResponse(TemplatedRule rule) {
        log.debug("Templated rule: {}", rule);
        return ServerResponse
                .status(rule.getResponse().getStatusCode())
                .contentType(rule.getResponse().getContentType())
                .body(BodyInserters.fromObject(rule.getResponse().getBody()));
    }
}
