package kroonprins.mocker.templating.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import kroonprins.mocker.templating.DefaultTemplatingContext;
import kroonprins.mocker.templating.TemplatingEngine;
import kroonprins.mocker.templating.TemplatingEngines;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.StringWriter;

@Service
public class FreeMarkerTemplatingEngine implements TemplatingEngine<DefaultTemplatingContext> {
    private Configuration configuration;

    public FreeMarkerTemplatingEngine(Configuration configuration) {
        this.configuration = configuration;
    }

    @SneakyThrows
    @Override
    public String render(String template, DefaultTemplatingContext context) {
        Template t = new Template("templateName", template, configuration);

        StringWriter writer = new StringWriter();
        t.process(context, writer);
        return writer.toString();
    }

    @Override
    public TemplatingEngines forType() {
        return TemplatingEngines.FREEMARKER;
    }
}
