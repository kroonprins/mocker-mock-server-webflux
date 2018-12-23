package kroonprins.mocker.templating;

import kroonprins.mocker.model.Rule;

public interface RuleTemplatingService {
    Rule template(Rule rule, TemplatingContext context);
}
