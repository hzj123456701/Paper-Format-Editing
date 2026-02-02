package com.example.backend.formatConfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;
@Data
public class Heading {
    @JsonProperty("headingStyles")
    private Map<Integer,HeadingStyle> headingStyles;
    @JsonProperty("headingRules")
    private Map<Integer, HeadingRule> headingRules;
    @JsonProperty("titleExamples")
    private Map<Integer,TitleExample> titleExamples;//不会用到
    @JsonProperty("useCustomHeadingRules")
    private boolean useCustomHeadingRules;

    public Boolean isUseCustomHeadingRules() {
        return useCustomHeadingRules;
    }
}
