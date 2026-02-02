package com.example.backend.formatConfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CustomStyles {
    @JsonProperty("normal")
    NormalConfig normalConfig;//普通文本样式
    @JsonProperty("paragraph")
    Paragraph paragraph;//正文段落样式
}
