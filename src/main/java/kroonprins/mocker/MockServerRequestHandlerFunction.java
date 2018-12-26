package kroonprins.mocker;

import kroonprins.mocker.model.Rule;
import kroonprins.mocker.model.TemplatedRule;
import kroonprins.mocker.templating.RuleTemplatingService;
import kroonprins.mocker.templating.TemplatingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;

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
                .elapsed()
                .delayUntil(this::applyLatency)
                .map(Tuple2::getT2)
                .flatMap(this::createResponse)
                .doOnError(error -> log.error("An unexpected error occurred", error));
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

    private Mono applyLatency(Tuple2<Long, TemplatedRule> value) {
        Long elapsed = value.getT1();
        TemplatedRule rule = value.getT2();

        log.debug("Applying latency: elapsed[{}]", elapsed);

        long latency = 0;
        if(rule.getResponse().getFixedLatency() != null) {
            latency = rule.getResponse().getFixedLatency().getValue();
        } else if(rule.getResponse().getRandomLatency() != null) {
            latency = rule.getResponse().getRandomLatency().getValue();
        }

        log.debug("Applying latency: requested[{}]", latency);
        long remainingDelay = latency - elapsed;
        log.debug("Applying latency: delay[{}]", remainingDelay);

        if(remainingDelay > 0) {
            return Mono.just(true).delayElement(Duration.ofMillis(remainingDelay));
        } else {
            return Mono.empty();
        }
    }
}
