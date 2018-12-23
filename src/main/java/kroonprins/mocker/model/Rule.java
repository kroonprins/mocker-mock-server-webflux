package kroonprins.mocker.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

@JsonDeserialize(builder = Rule.RuleBuilder.class)
@Builder
@Value
public class Rule {
    private final String name;
    private final Request request;
    private final Response response;

    @JsonPOJOBuilder(withPrefix = "")
    public static class RuleBuilder {

    }
}
