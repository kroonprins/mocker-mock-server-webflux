package kroonprins.mocker.templating;

import kroonprins.mocker.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@Profile("with-threading")
public class RuleTemplatingServiceImpl implements RuleTemplatingService {
    private TemplatingService templatingService;

    public RuleTemplatingServiceImpl(TemplatingService templatingService) {
        this.templatingService = templatingService;
    }

    @Override
    public Mono<TemplatedRule> template(Rule rule, TemplatingContext context) {
        TemplatingEngines templatingEngine = rule.getResponse().getTemplatingEngine();
        log.debug("Templating with engine {}", templatingEngine);

        Mono<FixedLatency> fixedLatency = templateFixedLatency(rule.getResponse().getFixedLatency(), templatingEngine, context);
        Mono<RandomLatency> randomLatency = templateRandomLatency(rule.getResponse().getRandomLatency(), templatingEngine, context);
        Mono<String> statusCode = template(rule.getResponse().getStatusCode(), templatingEngine, context);
        Mono<String> contentType = template(rule.getResponse().getContentType(), templatingEngine, context);
        Mono<String> body = template(rule.getResponse().getBody(), templatingEngine, context);

        return Mono.zip(templatedValues -> {
            log.debug("Templating done: {}", templatedValues);
            return createTemplatedRule(rule, templatedValues);
        }, fixedLatency, randomLatency, statusCode, contentType, body);
    }

    private Mono<String> template(String toTemplate, TemplatingEngines templatingEngine, TemplatingContext context) {
        if(TemplatingEngines.NONE.equals(templatingEngine) || StringUtils.isBlank(toTemplate)) {
            return Mono.just(toTemplate);
        } else {
            return Mono.fromCallable(() -> {
                log.debug("Templating with engine {}: {}", templatingEngine, toTemplate);
                return templatingService.render(templatingEngine, toTemplate, context);
            }).subscribeOn(Schedulers.parallel());
        }
    }

    private Mono<FixedLatency> templateFixedLatency(FixedLatency fixedLatency, TemplatingEngines templatingEngine, TemplatingContext context) {
        if(fixedLatency == null) {
            return Mono.just(FixedLatency.empty());
        }
        return template(fixedLatency.getValue(), templatingEngine, context)
                .map(value -> FixedLatency.builder().value(value).build());
    }

    private Mono<RandomLatency> templateRandomLatency(RandomLatency randomLatency, TemplatingEngines templatingEngine, TemplatingContext context) {
        if(randomLatency == null) {
            return Mono.just(RandomLatency.empty());
        }
        Mono<String> min = template(randomLatency.getMin(), templatingEngine, context);
        Mono<String> max = template(randomLatency.getMax(), templatingEngine, context);
        return Mono.zip(min, max, (templatedMin, templatedMax) -> RandomLatency.builder().min(templatedMin).max(templatedMax).build());
    }

    private TemplatedRule createTemplatedRule(Rule rule, Object[] templatedValues) {
        FixedLatency fixedLatency = (FixedLatency)templatedValues[0];
        RandomLatency randomLatency = (RandomLatency)templatedValues[1];
        HttpStatus statusCode = HttpStatus.valueOf(Integer.parseInt(((String)templatedValues[2])));
        MediaType contentType = MediaType.valueOf((String)templatedValues[3]);
        String body = (String)templatedValues[4];

        return TemplatedRule.builder()
                .name(rule.getName())
                .request(TemplatedRequest.from(rule.getRequest()))
                .response(TemplatedResponse.builder()
                        .fixedLatency(TemplatedFixedLatency.from(fixedLatency))
                        .randomLatency(TemplatedRandomLatency.from(randomLatency))
                        .statusCode(statusCode)
                        .contentType(contentType)
                        .body(body)
                        .build())
                .build();
    }
}
