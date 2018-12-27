package kroonprins.mocker.model;

import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.util.MimeType;

import java.util.List;

@Builder
@Value
public class TemplatedConditionalResponseValue {
    private final boolean condition;
    private final FixedLatency fixedLatency;
    private final RandomLatency randomLatency;
    private final MimeType contentType;
    private final HttpStatus statusCode;
    private final List<TemplatedHeader> headers;
    private final List<TemplatedCookie> cookies;
    private final String body;
}
