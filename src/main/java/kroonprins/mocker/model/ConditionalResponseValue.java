package kroonprins.mocker.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@JsonDeserialize(builder = ConditionalResponseValue.ConditionalResponseValueBuilder.class)
@Builder
@Value
public class ConditionalResponseValue implements TemplatableResponse {
    private final String condition;
    private final FixedLatency fixedLatency;
    private final RandomLatency randomLatency;
    private final String contentType;
    private final String statusCode;
    private final List<Header> headers;
    private final List<Cookie> cookies;
    private final String body;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ConditionalResponseValueBuilder {

    }
}
