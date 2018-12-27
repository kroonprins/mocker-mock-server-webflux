package kroonprins.mocker.model;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class TemplatedCookie {
    private final String name;
    private final String value;
    private final TemplatedCookieProperties properties;

    public static TemplatedCookie from(Cookie cookie) {
        return TemplatedCookie.builder()
                .name(cookie.getName())
                .value(cookie.getValue())
                .properties(TemplatedCookieProperties.from(cookie.getProperties()))
                .build();
    }
}
