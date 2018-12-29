package kroonprins.mocker.templating.jinjava;

import com.hubspot.jinjava.Jinjava;
import kroonprins.mocker.templating.TemplatingEngine;
import kroonprins.mocker.templating.TemplatingEngines;
import org.springframework.stereotype.Service;

@Service
public class JinjavaTemplatingEngine implements TemplatingEngine<JinjavaTemplatingContext> {
    private Jinjava jinjava;

    public JinjavaTemplatingEngine(Jinjava jinjava) {
        this.jinjava = jinjava;
    }

    @Override
    public String render(String template, JinjavaTemplatingContext context) {
        return jinjava.render(template, context);
    }

    @Override
    public TemplatingEngines forType() {
        return TemplatingEngines.JINJAVA;
    }
}
