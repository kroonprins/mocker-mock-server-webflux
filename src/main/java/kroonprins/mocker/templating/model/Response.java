package kroonprins.mocker.templating.model;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Response {
    private final int statusCode;
}
