package kroonprins.mocker.model;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class TemplatedHeader {
    private final String name;
    private final String value;

    public static TemplatedHeader from(Header header) {
        return TemplatedHeader.builder()
                .name(header.getName())
                .value(header.getValue())
                .build();
    }
}
