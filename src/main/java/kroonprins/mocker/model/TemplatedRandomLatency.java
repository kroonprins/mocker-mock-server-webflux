package kroonprins.mocker.model;

import kroonprins.mocker.util.Random;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class TemplatedRandomLatency {
    private final long min;
    private final long max;

    public static TemplatedRandomLatency from(RandomLatency randomLatency) {
        if(randomLatency == null || randomLatency.isEmpty()) {
            return null;
        }
        return TemplatedRandomLatency.builder()
                .min(Long.parseLong(randomLatency.getMin()))
                .max(Long.parseLong(randomLatency.getMax()))
                .build();
    }

    public final long getValue() {
        return new Random().randomLong(min, max);
    }
}
