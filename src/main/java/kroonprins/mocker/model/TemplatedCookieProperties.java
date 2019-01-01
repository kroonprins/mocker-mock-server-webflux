package kroonprins.mocker.model;

import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

@Builder
@Value
public class TemplatedCookieProperties {
    private final String domain;
    private final boolean httpOnly;
    private final long maxAge;
    private final String path;
    private final boolean secure;
    private final String sameSite; // according to spec can be either a string or a boolean

    public static TemplatedCookieProperties from(CookieProperties cookieProperties) {
        return TemplatedCookieProperties.builder()
                .domain(cookieProperties.getDomain())
                .httpOnly(Boolean.valueOf(cookieProperties.getHttpOnly()))
                .maxAge(StringUtils.isNotBlank(cookieProperties.getMaxAge()) ? Long.parseLong(cookieProperties.getMaxAge()) : -1L)
                .path(cookieProperties.getPath())
                .secure(Boolean.valueOf(cookieProperties.getSecure()))
                .sameSite(cookieProperties.getSameSite())
                .build();
    }
}
