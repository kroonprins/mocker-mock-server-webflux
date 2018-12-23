package kroonprins.mocker.templating;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TemplatingEngines {

    NONE,
    MUSTACHE

    ;

    @JsonCreator
    public static TemplatingEngines forValue(String value) {
        for (TemplatingEngines templatingEngine : TemplatingEngines.values()) {
            if (templatingEngine.name().equalsIgnoreCase(value)) {
                return templatingEngine;
            }
        }
        throw new IllegalArgumentException("No constant with text " + value + " found");
    }
}
