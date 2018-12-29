package kroonprins.mocker.templating;

public interface TemplatingEngine<T> {
    String render(String template, T context);

    TemplatingEngines forType();
}
