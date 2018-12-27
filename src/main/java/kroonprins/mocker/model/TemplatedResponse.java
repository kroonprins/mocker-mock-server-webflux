package kroonprins.mocker.model;

import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

@Builder
@Value
public class TemplatedResponse {
    private final TemplatedFixedLatency fixedLatency;
    private final TemplatedRandomLatency randomLatency;
    private final MediaType contentType;
    private final HttpStatus statusCode;
    private final List<TemplatedHeader> headers;
    private final List<TemplatedCookie> cookies;
    private final String body;
}
