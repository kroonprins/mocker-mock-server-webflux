package kroonprins.mocker.templating;

import kroonprins.mocker.model.Request;
import kroonprins.mocker.model.Response;
import kroonprins.mocker.model.Rule;
import org.springframework.stereotype.Service;

@Service
public class RuleTemplatingServiceImpl implements RuleTemplatingService {
    private TemplatingService templatingService;

    public RuleTemplatingServiceImpl(TemplatingService templatingService) {
        this.templatingService = templatingService;
    }

    // Can we make something that creates flux of templated values and piece by piece the rule object is created? The templating can then run on a specific scheduler
    @Override
    public Rule template(Rule rule, TemplatingContext context) {
        TemplatingEngines templatingEngine = rule.getResponse().getTemplatingEngine();
        return Rule.builder()
                .name(rule.getName())
                .request(Request.builder()
                            .method(rule.getRequest().getMethod())
                            .path(rule.getRequest().getPath())
                            .build())
                .response(Response.builder()
                            .body(template(rule.getResponse().getBody(), templatingEngine, context))
                            .build())
                .build();
    }

    private String template(String toTemplate, TemplatingEngines templatingEngine, TemplatingContext context) {
       return templatingService.render(templatingEngine, toTemplate, context);
    }
}
