package kroonprins.mocker.model;

import kroonprins.mocker.templating.TemplatingEngines;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class TemplatedConditionalResponse {
    private final TemplatingEngines templatingEngine;
    private final TemplatedConditionalResponseValue response;
}
