package kroonprins.mocker.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

@JsonDeserialize(builder = Cookie.CookieBuilder.class)
@Builder
@Value
public class Cookie {
    private final String name;
    private final String value;
    private final CookieProperties properties;

    @JsonPOJOBuilder(withPrefix = "")
    public static class CookieBuilder {

    }
}
