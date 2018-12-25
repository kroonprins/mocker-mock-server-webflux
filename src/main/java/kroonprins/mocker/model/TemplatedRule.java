package kroonprins.mocker.model;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class TemplatedRule {
    private final String name;
    private final TemplatedRequest request;
    private final TemplatedResponse response;
}
