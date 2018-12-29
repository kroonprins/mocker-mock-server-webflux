package kroonprins.mocker;

import kroonprins.mocker.model.Rule;
import reactor.core.publisher.Flux;

@FunctionalInterface
public interface RuleService {
    Flux<Rule> produceRules();
}
