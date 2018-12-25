package kroonprins.mocker.templating;

import kroonprins.mocker.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@Profile("no-threading")
public class NoThreadingRuleTemplatingServiceImpl implements RuleTemplatingService {
    private TemplatingService templatingService;

    public NoThreadingRuleTemplatingServiceImpl(TemplatingService templatingService) {
        this.templatingService = templatingService;
    }

    @Override
    public Mono<TemplatedRule> template(Rule rule, TemplatingContext context) {
        TemplatingEngines templatingEngine = rule.getResponse().getTemplatingEngine();
        log.debug("Templating with engine {}", templatingEngine);
        return Mono.just(TemplatedRule.builder()
                .name(rule.getName())
                .request(TemplatedRequest.builder()
                            .method(rule.getRequest().getMethod())
                            .path(rule.getRequest().getPath())
                            .build())
                .response(TemplatedResponse.builder()
                            .statusCode(HttpStatus.valueOf(Integer.parseInt(template(rule.getResponse().getStatusCode(), templatingEngine, context))))
                            .contentType(MediaType.valueOf(template(rule.getResponse().getContentType(), templatingEngine, context)))
                            .body(template(rule.getResponse().getBody(), templatingEngine, context))
                            .build())
                .build());
    }

    private String template(String toTemplate, TemplatingEngines templatingEngine, TemplatingContext context) {
       log.debug("Templating with engine {}: {}", templatingEngine, toTemplate);
       return templatingService.render(templatingEngine, toTemplate, context);
    }
}
