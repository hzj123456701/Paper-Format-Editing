package com.example.backend.formatConfig;

import lombok.Data;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Data
public class Paragraph {
    // 行间距类型
    public String lineSpacingType;
    // 行间距值
    public double lineSpacing;
    // 行间距单位
    public String lineSpacingUnit;
    // 首行缩进值
    public double firstLineIndent;
    // 首行缩进单位
    public String firstLineIndentUnit;
    // 左缩进值
    public double leftIndent;
    // 左缩进单位
    public String leftIndentUnit;
    // 右缩进值
    public double rightIndent;
    // 右缩进单位
    public String rightIndentUnit;
    // 段前间距值
    public double spaceBefore;
    // 段前间距单位
    public String spaceBeforeUnit;
    // 段后间距值
    public double spaceAfter;
    // 段后间距单位
    public String spaceAfterUnit;
    // 段落对齐方式
    public String alignment;

    public double fontSize;//初始时没有，需要后续赋值

    //得到段落格式配置

    public ParagraphConfig getNormalParagraphConfig() {
        // 存储对齐方式字符串和对应枚举值的映射
        Map<String, ParagraphAlignment> alignmentMap = new HashMap<>();
        alignmentMap.put("LEFT", ParagraphAlignment.LEFT);
        alignmentMap.put("CENTER", ParagraphAlignment.CENTER);
        alignmentMap.put("RIGHT", ParagraphAlignment.RIGHT);
        alignmentMap.put("JUSTIFY", ParagraphAlignment.BOTH);
        // 根据传入的对齐方式字符串获取对应的枚举值，若未匹配到则使用默认的左对齐
        ParagraphAlignment paragraphAlignment = alignmentMap.getOrDefault(alignment, ParagraphAlignment.LEFT);

        // 将首行缩进量转换为缇
        double firstLineIndentTwips = convertToTwips(firstLineIndent, firstLineIndentUnit);
//        if(firstLineIndentUnit.equals("ch")){
//            firstLineIndentTwips= firstLineIndent*fontSize*20;
//        }
        // 将左缩进量转换为缇
        double leftIndentTwips = convertToTwips(leftIndent, leftIndentUnit);
        // 将右缩进量转换为缇
        double rightIndentTwips = convertToTwips(rightIndent, rightIndentUnit);

        // 将段落前间距转换为缇
        double spacingBeforeTwips = convertToTwips(spaceBefore, spaceBeforeUnit);
        // 将段落后间距转换为缇
        double spacingAfterTwips = convertToTwips(spaceAfter, spaceAfterUnit);

        // 将行间距值转换为磅
        double convertedLineSpacing = convertToPoints(lineSpacing, lineSpacingUnit);
        // 行间距规则
        STLineSpacingRule.Enum lineSpacingRule;
        // 行间距，单位为缇
        BigInteger lineSpacingTwips;
        // 根据行间距类型设置行间距规则和行间距值
        if ("multiple".equals(lineSpacingType)) {
            lineSpacingRule = STLineSpacingRule.AUTO;
            lineSpacingTwips = BigInteger.valueOf((long) (lineSpacing * 240));//将行间距转换为缇
        } else if ("exact".equals(lineSpacingType)) {
            lineSpacingRule = STLineSpacingRule.EXACT;
            lineSpacingTwips = BigInteger.valueOf((long) (convertedLineSpacing * 20));
//            lineSpacingTwips = BigInteger.valueOf((long) (convertedLineSpacing * 20));
        } else {
            // 默认单倍行距
            lineSpacingRule = STLineSpacingRule.AUTO;
            lineSpacingTwips = BigInteger.valueOf(240);
        }
//        行数 = (段前距twips值 / (字体大小 × 1.2 × 20)) × 100
        // 创建并返回 CustomParagraphConfig 对象

        return new ParagraphConfig(
                paragraphAlignment,firstLineIndentUnit,firstLineIndentTwips,leftIndentUnit,rightIndentUnit, leftIndentTwips, rightIndentTwips,spaceBeforeUnit, spaceAfterUnit,
                spacingBeforeTwips,spacingAfterTwips, lineSpacingTwips, lineSpacingRule
        );
    }

    // 将值转换为缇，根据不同的单位进行转换
    private double convertToTwips(double value, String unit) {
        switch (unit) {
            // 磅转缇，1 磅等于 20 缇
            case "pt":
                return value * 20;
            // 厘米转缇，1 厘米约等于 28.35 磅，再乘以 20 转换为缇
            case "cm":
                return (int) (value * 28.35 * 20);
            // 毫米转缇，1 毫米约等于 2.835 磅，再乘以 20 转换为缇
            case "mm":
                return (int) (value * 2.835 * 20);
            // 行转缇，根据字体大小和行数计算缇值
            case "line":
                return value;//直接返回行值
            case "ch"://字符
                return value;
            // 英寸转缇，1 英寸等于 72 磅，再乘以 20 转换为缇
            case "in":
                return (int) (value * 72 * 20);
            // 默认返回原值
            default:
                return value;
        }
    }

    // 将值转换为磅，根据不同的单位进行转换
    private double convertToPoints(double value, String unit) {
        switch (unit) {
            // 磅单位无需转换
            case "pt":
                return value;
            // 厘米转磅，1 厘米约等于 28.35 磅
            case "cm":
                return value * 28.35;
            // 毫米转磅，1 毫米约等于 2.835 磅
            case "mm":
                return value * 2.835;
            // 行转磅，根据字体大小和行数计算磅值
            case "line":
                return value ;
            case "ch":
                return value ;
            // 英寸转磅，1 英寸等于 72 磅
            case "in":
                return value * 72;
            // 默认返回原值
            default:
                return value;
        }
    }
}
