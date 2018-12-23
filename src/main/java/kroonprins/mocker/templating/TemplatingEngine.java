package kroonprins.mocker.templating;

public interface TemplatingEngine {
    String render (String template, TemplatingContext context);
    TemplatingEngines forType();
}
