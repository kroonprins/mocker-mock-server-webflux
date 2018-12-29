package kroonprins.mocker.templating.none;

import kroonprins.mocker.templating.EmptyTemplatingContext;
import kroonprins.mocker.templating.TemplatingEngine;
import kroonprins.mocker.templating.TemplatingEngines;
import org.springframework.stereotype.Service;

@Service
public class NoneTemplatingEngine implements TemplatingEngine<EmptyTemplatingContext> {
    @Override
    public String render(String template, EmptyTemplatingContext context) {
        return template;
    }

    @Override
    public TemplatingEngines forType() {
        return TemplatingEngines.NONE;
    }
}
