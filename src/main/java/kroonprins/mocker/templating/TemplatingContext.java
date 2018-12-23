package kroonprins.mocker.templating;

import kroonprins.mocker.templating.model.Request;
import kroonprins.mocker.templating.model.Response;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class TemplatingContext {
    private final Request req;
    private final Response res;
}
