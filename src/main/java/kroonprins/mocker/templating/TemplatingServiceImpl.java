package kroonprins.mocker.templating;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TemplatingServiceImpl implements TemplatingService {

    private Map<TemplatingEngines, TemplatingEngine> mapping;

    public TemplatingServiceImpl(List<TemplatingEngine> templatingEngines) {
        this.mapping = templatingEngines.stream()
                .collect(Collectors.toMap(
                        TemplatingEngine::forType,
                        templatingEngine -> templatingEngine
                ));
    }

    @Override
    public String render (TemplatingEngines engine, String template, TemplatingContext context) {
        return this.mapping.getOrDefault(engine, this.mapping.get(TemplatingEngines.NONE)).render(template, context);
    }
}
