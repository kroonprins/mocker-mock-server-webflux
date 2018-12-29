package kroonprins.mocker.templating;

public interface TemplatingEngine<T extends TemplatingContext> {
    String render(String template, T context);

    TemplatingEngines forType();
}
