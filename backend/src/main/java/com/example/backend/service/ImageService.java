

package com.example.backend.service;

        import org.apache.poi.xwpf.usermodel.*;
        import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
        import org.springframework.stereotype.Service;

        import java.util.List;

/**
 * 图片尺寸调整器
 * 用于调整Word文档中图片的尺寸，保持宽高比例
 */
@Service
public class ImageService {

    // 默认目标高度（EMU单位，1厘米约等于360000 EMU）
    private static final long DEFAULT_TARGET_HEIGHT = 360000; // 约1厘米，设置得更小以确保效果明显

    /**
     * 调整段落中所有图片的高度，宽度等比例缩放
     * @param paragraph 包含图片的段落
//     * @param targetHeight 目标高度（EMU单位）
     * @return 调整的图片数量
     */
    public static int resizeImages(XWPFParagraph paragraph, double target,String heightOrWidth) {
        if (paragraph == null) {
            return 0;
        }
        long targetValue=cmToEMU(target);
        System.out.println("目标值"+targetValue);

        int resizedCount = 0;
        List<XWPFRun> runs = paragraph.getRuns();

        for (XWPFRun run : runs) {
            List<XWPFPicture> pictures = run.getEmbeddedPictures();

            for (XWPFPicture picture : pictures) {
                try {
                    // 综合方法调整图片大小
                    resizeImageComprehensive(picture, run, targetValue,heightOrWidth);

                    resizedCount++;
                } catch (Exception e) {
                    System.err.println("调整图片尺寸时出错: " + e.getMessage());
                }
            }
        }

        return resizedCount;
    }

    /**
     * 综合调整图片大小的方法
     * @param picture 图片对象
     * @param run 包含图片的XWPFRun
//     * @param targetHeight 目标高度
     */
    private static void resizeImageComprehensive(XWPFPicture picture, XWPFRun run, long target,String heightOrWidth) {
        try {
            //保留第一个方法防止更改后的文档的结构混乱
            // 1. 直接通过CTPositiveSize2D设置尺寸
            CTPositiveSize2D size = picture.getCTPicture().getSpPr().getXfrm().getExt();
            long currentWidth = size.getCx();
            long currentHeight = size.getCy();

            long otherTarget;
            // 计算比例并保持宽高比
            double ratio = (double) currentWidth / currentHeight;
            if(heightOrWidth.equals("height")){
                otherTarget = Math.round(target * ratio);//新的宽
                size.setCy(target);
                size.setCx(otherTarget);
            }
            else{
                otherTarget = Math.round(target / ratio);//新的高
                size.setCy(otherTarget);
                size.setCx(target);
            }
//            long newWidth = Math.round(targetHeight * ratio);

            // 打印原始大小和新大小
//            System.out.println("调整图片尺寸：" +
//                    "原尺寸 [" + currentWidth + " x " + currentHeight + "] -> " +
//                    "新尺寸 [" + newWidth + " x " + targetHeight + "]");

            // 设置CTPositiveSize2D尺寸 - 这是主要的修改方法
//            size.setCy(targetHeight);
//            size.setCx(newWidth);



//            实际上是第二三个方法在发挥作用
            //第二个方法和第三个方法的作用是一样的，都是通过修改XML结构来调整图片尺寸

//             2. 尝试修改XML结构 - 使用XPath
            try {
                if(heightOrWidth.equals("height")){
                    modifyAllDrawingDimensions(run,otherTarget,target);//宽在前面
                }
                else{
                    modifyAllDrawingDimensions(run, target, otherTarget);
                }

            } catch (Exception e) {
                System.err.println("修改drawing尺寸时出错: " + e.getMessage());
            }

            // 3. 尝试使用XPath直接修改XML结构
            try {
                if (run.getCTR() != null) {
                    // 修改所有图形尺寸相关属性
                    if(heightOrWidth.equals("height")){
                        modifyAllSizeAttributesWithXPath(run,otherTarget,target);//宽在前面
                    }
                    else {
                        modifyAllSizeAttributesWithXPath(run, target, otherTarget);
                    }
//                    modifyAllSizeAttributesWithXPath(run, newWidth, targetHeight);
                }
            } catch (Exception e) {
                System.err.println("使用XPath修改图片尺寸时出错: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("综合调整图片大小时出错: " + e.getMessage());
        }
    }

    /**
     * 修改run中的所有图形尺寸
     */
    private static void modifyAllDrawingDimensions(XWPFRun run, long width, long height) {
        try {
            // 使用XPath直接操作XML结构
            org.apache.xmlbeans.XmlObject[] drawings = run.getCTR().selectPath(
                    "declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' " +
                            ".//w:drawing");

            for (org.apache.xmlbeans.XmlObject drawing : drawings) {
                // 内联图片的extent
                org.apache.xmlbeans.XmlObject[] inlineExtents = drawing.selectPath(
                        "declare namespace wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing' " +
                                ".//wp:inline/wp:extent");

                for (org.apache.xmlbeans.XmlObject extent : inlineExtents) {
                    org.apache.xmlbeans.XmlCursor cursor = extent.newCursor();
                    cursor.setAttributeText(new javax.xml.namespace.QName("cx"), String.valueOf(width));
                    cursor.setAttributeText(new javax.xml.namespace.QName("cy"), String.valueOf(height));
                    cursor.close(); // 使用close()代替已弃用的dispose()
                }

                // 锚点图片的extent
                org.apache.xmlbeans.XmlObject[] anchorExtents = drawing.selectPath(
                        "declare namespace wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing' " +
                                ".//wp:anchor/wp:extent");

                for (org.apache.xmlbeans.XmlObject extent : anchorExtents) {
                    org.apache.xmlbeans.XmlCursor cursor = extent.newCursor();
                    cursor.setAttributeText(new javax.xml.namespace.QName("cx"), String.valueOf(width));
                    cursor.setAttributeText(new javax.xml.namespace.QName("cy"), String.valueOf(height));
                    cursor.close(); // 使用close()代替已弃用的dispose()
                }
            }
        } catch (Exception e) {
            System.err.println("修改drawing尺寸时出错: " + e.getMessage());
        }
    }

    /**
     * 使用XPath修改所有与尺寸相关的XML属性
     * @param run 包含图片的XWPFRun
     * @param width 新宽度
     * @param height 新高度
     */
    private static void modifyAllSizeAttributesWithXPath(XWPFRun run, long width, long height) {
        try {
            // 1. 修改extent属性
            modifyXPathAttribute(run,
                    "declare namespace wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing' " +
                            ".//wp:extent",
                    "cx", String.valueOf(width));

            modifyXPathAttribute(run,
                    "declare namespace wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing' " +
                            ".//wp:extent",
                    "cy", String.valueOf(height));

            // 2. 修改ext属性
            modifyXPathAttribute(run,
                    "declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main' " +
                            ".//a:ext",
                    "cx", String.valueOf(width));

            modifyXPathAttribute(run,
                    "declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main' " +
                            ".//a:ext",
                    "cy", String.valueOf(height));

            // 3. 修改effectExtent属性
            modifyXPathAttribute(run,
                    "declare namespace wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing' " +
                            ".//wp:effectExtent",
                    "l", "0");

            modifyXPathAttribute(run,
                    "declare namespace wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing' " +
                            ".//wp:effectExtent",
                    "t", "0");

            modifyXPathAttribute(run,
                    "declare namespace wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing' " +
                            ".//wp:effectExtent",
                    "r", "0");

            modifyXPathAttribute(run,
                    "declare namespace wp='http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing' " +
                            ".//wp:effectExtent",
                    "b", "0");

        } catch (Exception e) {
            System.err.println("使用XPath修改属性时出错: " + e.getMessage());
        }
    }

    /**
     * 使用XPath修改特定属性
     * @param run 包含图片的XWPFRun
     * @param xpath XPath查询字符串
     * @param attributeName 属性名
     * @param value 属性值
     */
    private static void modifyXPathAttribute(XWPFRun run, String xpath, String attributeName, String value) {
        try {
            org.apache.xmlbeans.XmlObject[] objects = run.getCTR().selectPath(xpath);

            for (org.apache.xmlbeans.XmlObject obj : objects) {
                org.apache.xmlbeans.XmlCursor cursor = obj.newCursor();
                cursor.setAttributeText(new javax.xml.namespace.QName(attributeName), value);
                cursor.close(); // 使用close()代替已弃用的dispose()
            }
        } catch (Exception e) {
            // 忽略特定错误
        }
    }

    /**
     * 使用默认高度调整段落中所有图片的尺寸
     * @param paragraph 包含图片的段落
     * @return 调整的图片数量
     */
//    public static int resizeImages(XWPFParagraph paragraph) {
//        return resizeImages(paragraph, DEFAULT_TARGET_HEIGHT);
//    }

//    /**
//     * 调整文档中所有段落的图片尺寸
//     * @param document 文档对象
//     * @param targetHeight 目标高度
//     * @return 调整的图片总数
//     */
//    public static int resizeAllImages(org.apache.poi.xwpf.usermodel.XWPFDocument document, long targetHeight) {
//        if (document == null) {
//            return 0;
//        }
//
//        // 使用AtomicInteger替代普通int，以便在Lambda表达式中安全修改
//        final AtomicInteger totalResized = new AtomicInteger(0);
//
//        try {
//            // 处理主文档中的段落
//            List<XWPFParagraph> paragraphs = document.getParagraphs();
//            for (XWPFParagraph paragraph : paragraphs) {
//                totalResized.addAndGet(resizeImages(paragraph, targetHeight));
//            }
//
//            // 处理表格中的段落
//            document.getTables().forEach(table -> {
//                table.getRows().forEach(row -> {
//                    row.getTableCells().forEach(cell -> {
//                        cell.getParagraphs().forEach(paragraph -> {
//                            totalResized.addAndGet(resizeImages(paragraph, targetHeight));
//                        });
//                    });
//                });
//            });
//
//            // 输出找到的所有图片名称，仅用于调试
//            System.out.println("文档中的所有图片:");
//            int imgIndex = 0;
//            for (XWPFPictureData picData : document.getAllPictures()) {
//                imgIndex++;
//                System.out.println("图片 #" + imgIndex + ": " + picData.getFileName() +
//                        ", 大小: " + picData.getData().length + " 字节");
//            }
//
//            // 尝试强制更新文档
//            try {
//                // 标记文档为"脏"状态，确保保存时刷新
//                document.getPackage().flush();
//            } catch (Exception e) {
//                // 忽略错误
//            }
//
//        } catch (Exception e) {
//            System.err.println("调整图片时出错: " + e.getMessage());
//        }
//
//        System.out.println("共调整 " + totalResized.get() + " 张图片的尺寸");
//        return totalResized.get();
//    }


    /**
     * 将厘米转换为EMU单位
     * @param cm 厘米
     * @return EMU单位
     */
    public static long cmToEMU(double cm) {
        if(cm==0){
            cm=10;//默认值
        }
        return Math.round(cm * 360000);
    }

    /**
     * 将英寸转换为EMU单位
     * @param inches 英寸
     * @return EMU单位
     */
    public static long inchesToEMU(double inches) {
        return Math.round(inches * 914400);
    }

//    public byte[] modifyMathStyles(MultipartFile file) throws IOException {
//
//        XWPFDocument document = new XWPFDocument(file.getInputStream());
//        for (XWPFParagraph paragraph : document.getParagraphs()) {
//            resizeImages(paragraph,10*360000);
//        }
//        // 保存修改后的文档
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        document.write(outputStream);
//        document.close();
//        return outputStream.toByteArray();
//    }
}