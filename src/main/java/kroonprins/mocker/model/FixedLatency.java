package kroonprins.mocker.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

@JsonDeserialize(builder = FixedLatency.FixedLatencyBuilder.class)
@Builder
@Value
public class FixedLatency {
    private final String value;

    @JsonPOJOBuilder(withPrefix = "")
    public static class FixedLatencyBuilder {

    }
}
