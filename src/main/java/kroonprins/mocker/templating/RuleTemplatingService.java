package kroonprins.mocker.templating;

import kroonprins.mocker.model.Rule;
import kroonprins.mocker.model.TemplatedRule;
import reactor.core.publisher.Mono;

public interface RuleTemplatingService {
    Mono<TemplatedRule> template(Rule rule, TemplatingContext context);
}
