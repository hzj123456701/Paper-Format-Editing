package com.example.backend.formatConfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

//正文相关的配置
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NormalConfig {
    // 定义 NormalConfig 类，用于存储普通字体配置信息

    // 中文字体
    public String normalFontCn;
    // 西文字体
    public String normalFontWestern;
    // 复杂文种字体
    public String normalFontComplex;
    // 字体大小
    public int normalFontSize;
    // 字体颜色
    public String fontColor;
    // 字体样式列表，可能包含 "bold", "underline", "italic" 等字符串
    public List<String> fontStyle;
    // 英文字符的字体样式列表
    public List<String> englishFontStyle;//先保留，后续再考虑是否需要
    // 是否单独设置英文字符的样式
    public boolean setEnglishCharStyleIndividually;//先保留，后续再考虑是否需要

    public SpecialConfig getNormalConfig() {
        SpecialConfig config = new SpecialConfig();
        if(this.normalFontWestern.equals("@same-as-chinese")){
            this.normalFontWestern= this.normalFontCn;
        }
        if(this.normalFontComplex.equals("@same-as-chinese")){
            this.normalFontComplex= this.normalFontCn;
        }
        if(this.normalFontComplex.equals("@same-as-western")){
            this.normalFontComplex= this.normalFontWestern;
        }
        config.fontSize = this.normalFontSize;
        config.fontCn = this.normalFontCn;
        config.fontAscii= this.normalFontWestern;
        config.fontHAnsi = this.normalFontWestern;
        config.fontComplex= this.normalFontComplex;
        config.fontColor = this.fontColor.equals("")?"000000":this.fontColor.substring(1);
        config.bold = this.fontStyle.contains("bold");
        config.italic = this.fontStyle.contains("italic");
        config.underline = this.fontStyle.contains("underline");
        return config;
    }

}
