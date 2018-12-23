package kroonprins.mocker.templating.mustache;

import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import kroonprins.mocker.templating.TemplatingContext;
import kroonprins.mocker.templating.TemplatingEngine;
import kroonprins.mocker.templating.TemplatingEngines;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.io.StringWriter;

@Service
public class MustacheTemplatingEngine implements TemplatingEngine {
    private MustacheFactory mustacheFactory;

    public MustacheTemplatingEngine(MustacheFactory mustacheFactory) {
        this.mustacheFactory = mustacheFactory;
    }

    @SneakyThrows
    @Override
    public String render(String template, TemplatingContext context) {
        Mustache mustache = mustacheFactory.compile(new StringReader(template), "TODO - name used for error reporting apparently");
        StringWriter writer = new StringWriter();
        mustache.execute(writer, context).flush();
        return writer.toString();
    }

    @Override
    public TemplatingEngines forType() {
        return TemplatingEngines.MUSTACHE;
    }
}
