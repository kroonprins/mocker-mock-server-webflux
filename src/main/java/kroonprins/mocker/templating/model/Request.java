package kroonprins.mocker.templating.model;

import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpMethod;

import java.util.Map;

@Builder
@Value
public class Request {
    private final String path;
    private final HttpMethod method;
    private final Map<String, String> params;
    private final Map<String, String> query;
    private final Map<String, String> headers;
    private final Map<String, String> cookies;
    private final String body;
}
