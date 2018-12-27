package kroonprins.mocker.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

@JsonDeserialize(builder = CookieProperties.CookiePropertiesBuilder.class)
@Builder
@Value
public class CookieProperties {
    private final String domain;
    //    private final String expires; // not managed by webflux
    private final String httpOnly;
    private final String maxAge;
    private final String path;
    private final String secure;
    //    private final String signed; // not managed by webflux
    private final String sameSite;

    @JsonPOJOBuilder(withPrefix = "")
    public static class CookiePropertiesBuilder {

    }
}
