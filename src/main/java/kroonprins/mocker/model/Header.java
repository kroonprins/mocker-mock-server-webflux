package kroonprins.mocker.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

@JsonDeserialize(builder = Header.HeaderBuilder.class)
@Builder
@Value
public class Header {
    private final String name;
    private final String value;

    @JsonPOJOBuilder(withPrefix = "")
    public static class HeaderBuilder {

    }
}
