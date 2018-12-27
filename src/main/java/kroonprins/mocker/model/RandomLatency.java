package kroonprins.mocker.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

@JsonDeserialize(builder = RandomLatency.RandomLatencyBuilder.class)
@Builder
@Value
public class RandomLatency {
    private final String min;
    private final String max;

    @JsonPOJOBuilder(withPrefix = "")
    public static class RandomLatencyBuilder {

    }
}
