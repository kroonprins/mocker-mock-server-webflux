package kroonprins.mocker;

import kroonprins.mocker.model.Request;
import kroonprins.mocker.model.Rule;
import kroonprins.mocker.templating.RuleTemplatingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.*;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RoutesBuilder {

    private RuleService ruleService;
    private RuleTemplatingService ruleTemplatingService;

    public RoutesBuilder(RuleService ruleService, RuleTemplatingService ruleTemplatingService) {
        this.ruleService = ruleService;
        this.ruleTemplatingService = ruleTemplatingService;
    }

    @Bean
    RouterFunction<ServerResponse> routes() {
        RouterFunctions.Builder result = route();

        ruleService.produceRules().doOnNext(rule ->
                result.add(createRouterFunctionForRule(rule))
        ).subscribe();

        return result.build();
    }

    private RouterFunction<ServerResponse> createRouterFunctionForRule(Rule rule) {
        return route(
                createRequestPredicate(rule.getRequest()),
                createHandlerFunction(rule)
        );
    }

    private MockServerRequestHandlerFunction createHandlerFunction(Rule rule) {
        return new MockServerRequestHandlerFunction(rule, ruleTemplatingService);
    }

    private RequestPredicate createRequestPredicate(Request request) {
        return RequestPredicates.method(request.getMethod())
                .and(
                        RequestPredicates.path(request.getPath())
                );
    }
}
