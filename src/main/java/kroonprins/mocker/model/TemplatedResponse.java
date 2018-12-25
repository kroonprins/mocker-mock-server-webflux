package kroonprins.mocker.model;

import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@Builder
@Value
public class TemplatedResponse {
    private final HttpStatus statusCode;
    private final MediaType contentType;
    private final String body;
}
