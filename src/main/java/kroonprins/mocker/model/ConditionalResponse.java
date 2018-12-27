package kroonprins.mocker.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import kroonprins.mocker.templating.TemplatingEngines;
import lombok.Builder;
import lombok.Value;

@JsonDeserialize(builder = ConditionalResponse.ConditionalResponseBuilder.class)
@Builder
@Value
public class ConditionalResponse {
    private final TemplatingEngines templatingEngine;
    private final ConditionalResponseValue response;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ConditionalResponseBuilder {

    }
}
