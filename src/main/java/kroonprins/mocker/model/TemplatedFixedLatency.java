package kroonprins.mocker.model;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class TemplatedFixedLatency {
    private final long value;

    public static TemplatedFixedLatency from(FixedLatency fixedLatency) {
        if(fixedLatency == null || fixedLatency.isEmpty()) {
            return null;
        }
        return TemplatedFixedLatency.builder()
                .value(Long.parseLong(fixedLatency.getValue()))
                .build();
    }
}
