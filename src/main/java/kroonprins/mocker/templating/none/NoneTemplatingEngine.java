package kroonprins.mocker.templating.none;

import kroonprins.mocker.templating.TemplatingContext;
import kroonprins.mocker.templating.TemplatingEngine;
import kroonprins.mocker.templating.TemplatingEngines;
import org.springframework.stereotype.Service;

@Service
public class NoneTemplatingEngine implements TemplatingEngine {
    @Override
    public String render(String template, TemplatingContext context) {
        return template;
    }

    @Override
    public TemplatingEngines forType() {
        return TemplatingEngines.NONE;
    }
}
