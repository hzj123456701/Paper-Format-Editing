package com.example.backend.formatConfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageConfig {
    private String imageAlignment;//图片对齐方式
    String fixedDimension;//固定尺寸
    double dimensionValue;//尺寸值
    boolean showfixed;//是否显示选项,是否开启固定尺寸
    public ParagraphAlignment getImageAlignment() {
        switch (this.imageAlignment.toUpperCase()) {
            case "LEFT":
                return ParagraphAlignment.LEFT;
            case "CENTER":
                return ParagraphAlignment.CENTER;
            case "RIGHT":
                return ParagraphAlignment.RIGHT;
            case "JUSTIFY":
                return ParagraphAlignment.BOTH;
            case "BOTH":
                return ParagraphAlignment.BOTH;
            case "DISTRIBUTE":
                return ParagraphAlignment.DISTRIBUTE;
            case "NUM_TAB":
                return ParagraphAlignment.NUM_TAB;
            default:
                return ParagraphAlignment.LEFT;
        }
    }
}