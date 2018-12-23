package kroonprins.mocker.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import kroonprins.mocker.templating.TemplatingEngines;
import lombok.Builder;
import lombok.Value;

@JsonDeserialize(builder = Response.ResponseBuilder.class)
@Builder
@Value
public class Response {
    private final TemplatingEngines templatingEngine;
    private final String contentType;
    private final String statusCode;
    private final String body;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ResponseBuilder {

    }
}
