package kroonprins.mocker.templating;

public interface TemplatingService {
    String render (TemplatingEngines engine, String template, TemplatingContext context);
}
