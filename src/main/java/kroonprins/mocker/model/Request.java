package kroonprins.mocker.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpMethod;

@JsonDeserialize(builder = Request.RequestBuilder.class)
@Builder
@Value
public class Request {
    private final String path;
    private final HttpMethod method;

    @JsonPOJOBuilder(withPrefix = "")
    public static class RequestBuilder {

    }
}
