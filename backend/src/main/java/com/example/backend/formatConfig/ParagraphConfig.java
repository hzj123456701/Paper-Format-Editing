package com.example.backend.formatConfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule;

import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
//段落样式
public class ParagraphConfig {
    // 段落的对齐方式
    ParagraphAlignment alignment;
    //首行缩进单位
    String firstLineIndentUnit;
    // 首行缩进量
    double firstLineIndentTwips;
    //左缩进单位
    String leftIndentUnit;
    //右缩进单位
    String rightIndentUnit;
    // 左缩进量，单位为缇
    double leftIndentTwips;
    // 右缩进量，单位为缇
    double rightIndentTwips;
    //段前单位
    String beforeUnit;
    // 段后单位
    String afterUnit;
    // 段落前间距
    double spacingBefore;
    // 段落后间距
    double spacingAfter;
    // 行间距，单位为缇
    BigInteger lineSpacingTwips;
    // 行间距规则
    STLineSpacingRule.Enum lineSpacingRule;
}
