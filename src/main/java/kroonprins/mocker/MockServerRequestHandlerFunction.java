package kroonprins.mocker;

import kroonprins.mocker.model.Rule;
import kroonprins.mocker.templating.RuleTemplatingService;
import kroonprins.mocker.templating.TemplatingContext;
import kroonprins.mocker.templating.TemplatingService;
import kroonprins.mocker.templating.model.Request;
import kroonprins.mocker.templating.model.Response;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

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
                .map(templatingContext -> ruleTemplatingService.template(rule, templatingContext))
                .flatMap(templatedRule -> ServerResponse
                    .status(Integer.parseInt(rule.getResponse().getStatusCode()))
                    .body(BodyInserters.fromObject(templatedRule.getResponse().getBody()))
                );
    }

    private TemplatingContext createTemplatingContext(ServerRequest serverRequest) {
        return TemplatingContext.builder()
                .req(
                        Request.builder()
                               .path(serverRequest.path())
                               .method(serverRequest.method())
                               .query(serverRequest.queryParams().toSingleValueMap())
                        .build()
                )
                .res(
                        Response.builder()
                        .build()
                )
                .build();
    }
}
