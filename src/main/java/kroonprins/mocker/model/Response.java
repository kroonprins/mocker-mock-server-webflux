package kroonprins.mocker.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import kroonprins.mocker.templating.TemplatingEngines;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@JsonDeserialize(builder = Response.ResponseBuilder.class)
@Builder
@Value
public class Response implements TemplatableResponse {
    private final TemplatingEngines templatingEngine;
    private final FixedLatency fixedLatency;
    private final RandomLatency randomLatency;
    private final String contentType;
    private final String statusCode;
    private final List<Header> headers;
    private final List<Cookie> cookies;
    private final String body;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ResponseBuilder {

    }
}
