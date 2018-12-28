package kroonprins.mocker.model;

import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Collectors;

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

    public static TemplatedResponse from(TemplatableResponse response) {
        if (response == null) {
            return null;
        }
        return TemplatedResponse.builder()
                .fixedLatency(TemplatedFixedLatency.from(response.getFixedLatency()))
                .randomLatency(TemplatedRandomLatency.from(response.getRandomLatency()))
                .contentType(response.getContentType() != null ? MediaType.valueOf(response.getContentType()) : null)
                .statusCode(HttpStatus.valueOf(Integer.parseInt(response.getStatusCode())))
                .headers(response.getHeaders().stream().map(TemplatedHeader::from).collect(Collectors.toList()))
                .cookies(response.getCookies().stream().map(TemplatedCookie::from).collect(Collectors.toList()))
                .body(response.getBody())
                .build();
    }
}
