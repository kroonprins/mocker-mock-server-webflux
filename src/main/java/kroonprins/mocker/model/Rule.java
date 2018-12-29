package kroonprins.mocker.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import kroonprins.mocker.templating.TemplatingEngines;
import lombok.Builder;
import lombok.Value;

@JsonDeserialize(builder = Rule.RuleBuilder.class)
@Builder
@Value
public class Rule {
    private final String name;
    private final Request request;
    private final Response response;
    private final ConditionalResponse conditionalResponse;

    @JsonPOJOBuilder(withPrefix = "")
    public static class RuleBuilder {

    }

    public TemplatingEngines getTemplatingEngine() {
        if (response != null) {
            return response.getTemplatingEngine();
        } else {
            return conditionalResponse.getTemplatingEngine();
        }
    }
}
