package com.example.backend.formatConfig;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;


//文档格式配置,映射前端传过来的数据，用于格式化文档
@Data
public class DocumentFormatConfig   {
    @JsonProperty("customStyles")
    CustomStyles customStyles;//正文样式
    @JsonProperty("heading")//标题和特殊段落的样式
    private Heading heading;
    @JsonProperty("image")
    private ImageConfig image;//图片对齐方式
    @JsonProperty("tableAlignment")
    private TableConfig tableAlignment;//表格对齐方式
}


