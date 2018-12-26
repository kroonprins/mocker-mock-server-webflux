package kroonprins.mocker.model;

import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpMethod;

@Builder
@Value
public class TemplatedRequest {
    private final String path;
    private final HttpMethod method;

    public static TemplatedRequest from(Request request) {
        return TemplatedRequest.builder()
                .method(request.getMethod())
                .path(request.getPath())
                .build();
    }
}
