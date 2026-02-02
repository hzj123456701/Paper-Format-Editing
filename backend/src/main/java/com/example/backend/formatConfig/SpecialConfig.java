package com.example.backend.formatConfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//特定格式配置
//中文，英文，数字，字符
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecialConfig {
    // 中文字体
    public String fontCn;
    public String fontAscii;//ASCII字体
    public String fontHAnsi;//用于西欧语言等使用的字体,在这里与ASCII的字体相同,这两个都算在西文字体中
    //复杂脚本字体，如阿拉伯语、希伯来语
    // 复杂文种字体
    public String fontComplex;
    double fontSize;//字体大小
    boolean bold;//是否加粗
    boolean italic;//是否斜体
    boolean underline;//下划线
    String fontColor;//字体颜色
}
