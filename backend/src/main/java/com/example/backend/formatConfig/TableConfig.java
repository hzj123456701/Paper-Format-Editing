package com.example.backend.formatConfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TableRowAlign;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableConfig {
    private String alignment;//表格对齐方式
     public TableRowAlign getTableAlignment() {
        switch (this.alignment.toLowerCase()) {
            case "left":
                return TableRowAlign.LEFT;
            case "center":
                return TableRowAlign.CENTER;
            case "right":
                return TableRowAlign.RIGHT;
            default:
                // 如果传入的对齐方式不支持，返回默认的左对齐
                return TableRowAlign.LEFT;
        }
    }
}
