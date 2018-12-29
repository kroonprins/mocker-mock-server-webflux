package kroonprins.mocker.templating;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public final class EmptyTemplatingContext implements TemplatingContext {
    public static final EmptyTemplatingContext CONTEXT = new EmptyTemplatingContext();

    private EmptyTemplatingContext() {
    }
}
