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

        Mono<String> statusCode = template(rule.getResponse().getStatusCode(), templatingEngine, context);
        Mono<String> contentType = template(rule.getResponse().getContentType(), templatingEngine, context);
        Mono<String> body = template(rule.getResponse().getBody(), templatingEngine, context);

        return Mono.zip(templatedValues -> {
            log.debug("Templating done: {}", templatedValues);
            return createTemplatedRule(rule, templatedValues);
        }, statusCode, contentType, body);
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

    private TemplatedRule createTemplatedRule(Rule rule, Object[] templatedValues) {
        HttpStatus statusCode = HttpStatus.valueOf(Integer.parseInt(((String)templatedValues[0])));
        MediaType contentType = MediaType.valueOf((String)templatedValues[1]);
        String body = (String)templatedValues[2];

        return TemplatedRule.builder()
                .name(rule.getName())
                .request(TemplatedRequest.builder()
                        .method(rule.getRequest().getMethod())
                        .path(rule.getRequest().getPath())
                        .build())
                .response(TemplatedResponse.builder()
                        .statusCode(statusCode)
                        .contentType(contentType)
                        .body(body)
                        .build())
                .build();
    }
}
