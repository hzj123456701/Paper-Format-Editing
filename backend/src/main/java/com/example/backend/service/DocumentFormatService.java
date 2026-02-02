package com.example.backend.service;
import com.example.backend.formatConfig.*;
import com.example.backend.numbering.ParagraphNumberingInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@Slf4j
public class DocumentFormatService {


    @Autowired
    ImageService imageService;

    // 在开始处理前先收集所有段落的编号信息
    Map<XWPFParagraph, ParagraphNumberingInfo> numberingInfoMap;
    private int maxTitleLevel;//标题的最大级别
    CustomStyles customStyles;//正文样式
    private Map<Integer, HeadingRule> headingPatterns;//多级标题的正则表达式
    private Map<Integer, Pattern> patternMatch=new HashMap<Integer,Pattern>();//多级标题的正则表达式的匹配器
    private Map<Integer, HeadingStyle> headingStyles;//多级标题的样式
    private Map<Integer, HeadingRule> headingRules;//多级标题的规则
    //正文样式
    private SpecialConfig normalConfig;//正文样式
    //正文下的段落样式
    private ParagraphConfig normalParagraphConfig;//段落样式
    private TableConfig tableAlignment;//表格样式
    private ImageConfig image;//图片样式
    //标题下的样式
    private Map<Integer, SpecialConfig> headingConfigs=new HashMap<Integer,SpecialConfig>();//标题下的正文样式
    //标题下的段落样式
    private Map<Integer, ParagraphConfig> headingParagraphConfigs=new HashMap<Integer,ParagraphConfig>();//标题下的段落样式
    public byte[] applyFormat(MultipartFile file) throws Exception {

        XWPFDocument document = new XWPFDocument(file.getInputStream());

        //在开始处理前先收集所有段落的编号信息
        numberingInfoMap = collectParagraphNumberingInfo(document);

        for (XWPFParagraph paragraph : document.getParagraphs()) {
            //判断段落中是否有图片
            boolean hasPicture=hasImage(paragraph);


            //设置图片大小
            if(hasPicture&&image.isShowfixed()){
                log.info("图片固定大小");
                if(image.getFixedDimension().equals("width"))
                    setImageWidth(paragraph,image.getDimensionValue());
                else
                    setImageHeight(paragraph,image.getDimensionValue());
            }


            boolean hasFormula=containsMathFormula(paragraph);

            // 检查段落是否有编号
            String numText="";
//            boolean hasNumbering = paragraph.getNumID() != null;
            boolean hasNumbering=hasNumbering(paragraph);
//            log.info("段落是否有编号："+hasNumbering);
            if(hasNumbering){
                numText=extractAndRemoveNumbering(paragraph,document);
            }
            if(!hasNumbering){
//                log.info("段落没有编号");
//                log.info(String.valueOf(paragraph.getText().trim().isEmpty()||paragraph.getText().trim().equals(" ")));
//                log.info(String.valueOf(!hasFormula));
//                log.info(String.valueOf(hasPicture));
                if((paragraph.getText().trim().isEmpty()||paragraph.getText().trim().equals(" "))&&!hasFormula) {
                    if(hasPicture) {
                        log.info("段落只有一张图片");
                        applyPictureStyles(paragraph);
                        continue;//认为这个段落只有一张图片
                    }
                    continue;//认为这个段落为空行
                }
            }
            String text = (numText+paragraph.getText()).trim();//获取段落文本,需要去除前后空格



                //对于空行直接跳过
                if(text==null||text.isEmpty()){
                    continue;
                }
                //isParagraphStartMatching(text)!=0代表为标题，返回的值代表标题的级别
                int level=isParagraphStartMatching(text);
            if(level!=0){
//                log.info("标题级别为："+level);
//                log.info("text:"+text);
                if(hasNumbering){
//                    对编号设置对应的样式
                    setNumberingStyle(
                            paragraph,
                            headingConfigs.get(level).fontCn,                // 中文字体
                            headingConfigs.get(level).fontAscii,    // 英文字体
                            headingConfigs.get(level).fontHAnsi,              // 带重音的欧洲字符字体
                            headingConfigs.get(level).fontComplex,             // 复杂脚本字体
                            (int)headingConfigs.get(level).getFontSize(),                   // 字号
                            headingConfigs.get(level).isBold(),                 // 加粗
                            headingConfigs.get(level).isItalic(),                // 倾斜
                            headingConfigs.get(level).isUnderline(),                 // 下划线
                            headingConfigs.get(level).getFontColor()             // 颜色
                    );
                }
                if(containsMathFormula(paragraph)){
                    setMathFormulaStyle(
                            paragraph,
                            headingConfigs.get(level).fontCn,                // 中文字体
                            headingConfigs.get(level).fontAscii,    // 英文字体
                            headingConfigs.get(level).fontHAnsi,              // 带重音的欧洲字符字体
                            headingConfigs.get(level).fontComplex,             // 复杂脚本字体
                            (int)headingConfigs.get(level).getFontSize(),                   // 字号
                            headingConfigs.get(level).isBold(),                 // 加粗
                            headingConfigs.get(level).isItalic(),                // 倾斜
                            headingConfigs.get(level).isUnderline(),                 // 下划线
                            headingConfigs.get(level).getFontColor()             // 颜色
                    );
                }
                applyHeadingConfigs(level,paragraph);
                //应用标题段落样式
                applyHeadingParagraphConfig(paragraph,level,hasPicture||hasFormula);
            }
            else{
//                log.info("正文");
//                log.info("text:"+text);
//                log.info("段落样式"+normalParagraphConfig);
                if(hasNumbering){
                    setNumberingStyle(
                            paragraph,
                            normalConfig.fontCn,                // 中文字体
                            normalConfig.fontAscii,    // 英文字体
                            normalConfig.fontHAnsi,              // 带重音的欧洲字符字体
                            normalConfig.fontComplex,             // 复杂脚本字体
                            (int)normalConfig.getFontSize(),                   // 字号
                            normalConfig.isBold(),                 // 加粗
                            normalConfig.isItalic(),                // 倾斜
                            normalConfig.isUnderline(),                 // 下划线
                            normalConfig.getFontColor()             // 颜色
                    );

                }
                if(containsMathFormula(paragraph)){
                    setMathFormulaStyle(
                            paragraph,
                            normalConfig.fontCn,                // 中文字体
                            normalConfig.fontAscii,    // 英文字体
                            normalConfig.fontHAnsi,              // 带重音的欧洲字符字体
                            normalConfig.fontComplex,             // 复杂脚本字体
                            (int)normalConfig.getFontSize(),                   // 字号
                            normalConfig.isBold(),                 // 加粗
                            normalConfig.isItalic(),                // 倾斜
                            normalConfig.isUnderline(),                 // 下划线
                            normalConfig.getFontColor()           // 颜色;
                    );
                }

                applyNormalConfig(paragraph);//应用正文样式
                applyNormalParagraphConfig(paragraph,hasPicture||hasFormula);//应用正文段落样式
            }
        }
        for (XWPFTable table : document.getTables()) {
            applyTableStyles(table);
        }


        // 保存修改后的文档
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.write(outputStream);
        document.close();
        return outputStream.toByteArray();
    }


    private void applyNormalConfig(XWPFParagraph paragraph){
        for(XWPFRun run:paragraph.getRuns()){
            // 1. 为中文字符设置字体
                run.setFontFamily(normalConfig.getFontCn(), XWPFRun.FontCharRange.eastAsia);
            // 2. 为西文字符设置字体(同时设置ASCII和高ANSI)
                // ASCII字符 (基本拉丁字母和数字)
                run.setFontFamily(normalConfig.getFontAscii(), XWPFRun.FontCharRange.ascii);
                // 高ANSI字符 (带重音符号的拉丁字母等)
                run.setFontFamily(normalConfig.getFontHAnsi(), XWPFRun.FontCharRange.hAnsi);
            // 3. 为复杂文种字符设置字体
                run.setFontFamily(normalConfig.getFontComplex(), XWPFRun.FontCharRange.cs);
            run.setFontSize(normalConfig.getFontSize());
            run.setBold(normalConfig.isBold());
            run.setItalic(normalConfig.isItalic());
            run.setUnderline(normalConfig.isUnderline() ? UnderlinePatterns.SINGLE : UnderlinePatterns.NONE);
            run.setColor(normalConfig.getFontColor());//字体颜色
        }
    }




    private void applyHeadingConfigs(int level, XWPFParagraph paragraph) {
        for(XWPFRun run:paragraph.getRuns()){
            // 1. 为中文字符设置字体
            run.setFontFamily(headingConfigs.get(level).getFontCn(), XWPFRun.FontCharRange.eastAsia);
            // 2. 为西文字符设置字体(同时设置ASCII和高ANSI)
            // ASCII字符 (基本拉丁字母和数字)
            run.setFontFamily(headingConfigs.get(level).getFontAscii(), XWPFRun.FontCharRange.ascii);
            // 高ANSI字符 (带重音符号的拉丁字母等)
            run.setFontFamily(headingConfigs.get(level).getFontHAnsi(), XWPFRun.FontCharRange.hAnsi);
            // 3. 为复杂文种字符设置字体
            run.setFontFamily(headingConfigs.get(level).getFontComplex(), XWPFRun.FontCharRange.cs);
            run.setFontSize(headingConfigs.get(level).getFontSize());
            run.setBold(headingConfigs.get(level).isBold());
            run.setItalic(headingConfigs.get(level).isItalic());
            run.setUnderline(headingConfigs.get(level).isUnderline() ? UnderlinePatterns.SINGLE : UnderlinePatterns.NONE);
            run.setColor(headingConfigs.get(level).getFontColor());//字体颜色
        }
    }


    public void setParams(DocumentFormatConfig config) {
        this.customStyles = config.getCustomStyles();//正文样式
//        log.info("customStyles:{}", customStyles);
        this.headingPatterns = config.getHeading().getHeadingRules();//多级标题的正则表达式
        this.headingStyles =config.getHeading().getHeadingStyles();//多级标题的样式
        maxTitleLevel = this.headingStyles.size();//最大标题级别
        if(!config.getHeading().isUseCustomHeadingRules())
            maxTitleLevel=0;//如果不用自定义标题规则，则最大标题级别为0
//        log.info("maxTitleLevel:{}", maxTitleLevel);
        this.image = config.getImage();//图片样式

        this.tableAlignment = config.getTableAlignment();//表格样式
        this.normalConfig = customStyles.getNormalConfig().getNormalConfig();//正文样式

        //设置正文段落对象中的字体大小
        Paragraph paragraph = customStyles.getParagraph();
        paragraph.setFontSize(customStyles.getNormalConfig().getNormalFontSize());
        this.normalParagraphConfig = paragraph.getNormalParagraphConfig();//正文段落样式
//        log.info("正文段落样式（检验字体大小是否成功设置）paragraph:{}", paragraph);
        //标题（特殊段落）设置
        for (int i = 1; i <= this.maxTitleLevel; i++) {
            this.headingConfigs.put(i, headingStyles.get(i).getHeadingConfig());

            this.headingParagraphConfigs.put(i, headingStyles.get(i).getHeadingParagraphConfig());//标题段落的样式
            //匹配器
            this.patternMatch.put(i, Pattern.compile(headingPatterns.get(i).getPattern()));

        }
    }

    //判断是否符合标题的正则表达式
    public int isParagraphStartMatching(String text) {
        for (int i = this.maxTitleLevel; i >= 1; i--) {
            // 使用 Matcher 的 lookingAt 方法检查文本的开头是否匹配正则表达式
            if (patternMatch.get(i).matcher(text).lookingAt())  {
                return i;
            }
        }
        return 0;//不是标题,返回0，后续应用正文样式
    }


    // 应用段落配置到段落对象的方法
    public void applyHeadingParagraphConfig(XWPFParagraph paragraph,int level,boolean hasPiOrFu) {

        // 获取段落的CTP对象
        org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP ctp = paragraph.getCTP();
        // 检查是否存在段落属性
        if (ctp.isSetPPr()) {
            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr pPr = ctp.getPPr();

            // 清除缩进设置（包括首行缩进、左缩进、右缩进）
            if (pPr.isSetInd()) {
                pPr.unsetInd();
            }

            // 清除段落间距设置（包括段前距、段后距、行间距）
            if (pPr.isSetSpacing()) {
                pPr.unsetSpacing();
            }

            // 清除对齐方式
            if (pPr.isSetJc()) {
                pPr.unsetJc();
            }
        }


        // 设置段落对齐方式
        if (headingParagraphConfigs.get(level).getAlignment() != null) {
            paragraph.setAlignment(headingParagraphConfigs.get(level).getAlignment());
        }

        // 设置首行缩进量
//        paragraph.setFirstLineIndent((int)headingParagraphConfigs.get(level).getFirstLineIndentTwips());
        //TODO
//        if(headingParagraphConfigs.get(level).getFirstLineIndentUnit().equals("ch")){
//            paragraph.setIndentationFirstLine((int)headingParagraphConfigs.get(level).getFirstLineIndentTwips());
//        }
//        else{
//            paragraph.setFirstLineIndent((int)headingParagraphConfigs.get(level).getFirstLineIndentTwips());
//        }
        //设置首行缩进
        if(headingParagraphConfigs.get(level).getFirstLineIndentUnit().equals("ch")){
            //设置首行缩进
            // 获取段落的 XML 属性容器
            CTP ctp1 = paragraph.getCTP();
            CTPPr ppr1 = ctp1.isSetPPr() ? ctp1.getPPr() : ctp1.addNewPPr();

            // 获取或创建缩进元素
            CTInd ind = ppr1.isSetInd() ? ppr1.getInd() : ppr1.addNewInd();

            // 设置首行缩进 2 个字符
            ind.setFirstLineChars(BigInteger.valueOf((long)headingParagraphConfigs.get(level).getFirstLineIndentTwips()*100));
        }
        else{
            paragraph.setFirstLineIndent((int)headingParagraphConfigs.get(level).getFirstLineIndentTwips());
        }

        //TODO 待验证
        if(headingParagraphConfigs.get(level).getLeftIndentUnit().equals("ch")){
            paragraph.setIndentationLeftChars((int) headingParagraphConfigs.get(level).getLeftIndentTwips()*100);
        }
        else{
            paragraph.setIndentationLeft((int)headingParagraphConfigs.get(level).getLeftIndentTwips());
        }
        if(headingParagraphConfigs.get(level).getRightIndentUnit().equals("ch")){
            paragraph.setIndentationRightChars((int)headingParagraphConfigs.get(level).getRightIndentTwips()*100);
        }
        else{
            paragraph.setIndentationRight((int)headingParagraphConfigs.get(level).getRightIndentTwips());
        }
//        // 设置左缩进量
//        paragraph.setIndentationLeft((int)headingParagraphConfigs.get(level).getLeftIndentTwips());
//
//        // 设置右缩进量
//        paragraph.setIndentationRight((int)headingParagraphConfigs.get(level).getRightIndentTwips());
        org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing spacing =  ctp.getPPr().addNewSpacing();
//TODO 待验证
// 明确设置段前距
        if( headingParagraphConfigs.get(level).getBeforeUnit().equals("line")){
            spacing.setBeforeLines(BigInteger.valueOf((long)headingParagraphConfigs.get(level).getSpacingBefore()*100));
        }
        else{
            spacing.setBefore(headingParagraphConfigs.get(level).getSpacingBefore());
        }

// 明确设置段后距
        if(headingParagraphConfigs.get(level).getAfterUnit().equals("line")){
            spacing.setAfterLines(BigInteger.valueOf((long)headingParagraphConfigs.get(level).getSpacingAfter()*100));
        }
        else{
            spacing.setAfter(headingParagraphConfigs.get(level).getSpacingAfter());
        }

        // 设置行间距和行间距规则
        if (headingParagraphConfigs.get(level).getLineSpacingTwips() != null && headingParagraphConfigs.get(level).getLineSpacingRule() != null) {
            // 由于已经清除了原有spacing，这里可以安全地添加新的spacing
            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr ppr = paragraph.getCTP().getPPr();
            if (ppr == null) {
                ppr = paragraph.getCTP().addNewPPr();
            }
//            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing spacing = ppr.addNewSpacing();
            if( headingParagraphConfigs.get(level).getLineSpacingRule().equals(STLineSpacingRule.EXACT)&&hasPiOrFu){
                spacing.setLine(headingParagraphConfigs.get(level).getLineSpacingTwips());
                spacing.setLineRule(STLineSpacingRule.AT_LEAST);

            }
            else {
                spacing.setLine(headingParagraphConfigs.get(level).getLineSpacingTwips());
                spacing.setLineRule(headingParagraphConfigs.get(level).getLineSpacingRule());
            }
        }
    }

    // 应用段落配置到段落对象的方法
    public void applyNormalParagraphConfig(XWPFParagraph paragraph,boolean hasPiOrFu)  {


        // 获取段落的CTP对象
        org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP ctp = paragraph.getCTP();

// 检查是否存在段落属性
        if (ctp.isSetPPr()) {
            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr pPr = ctp.getPPr();

            // 清除缩进设置（包括首行缩进、左缩进、右缩进）
            if (pPr.isSetInd()) {
                pPr.unsetInd();
            }


            if (pPr.isSetSpacing()) {
                pPr.unsetSpacing();
            }

            // 清除对齐方式
            if (pPr.isSetJc()) {
                pPr.unsetJc();
            }
        }


        org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr ppr = paragraph.getCTP().getPPr();
        if (ppr == null) {
            ppr = paragraph.getCTP().addNewPPr();//段落属性
        }
        // 设置正文对齐方式
        if (normalParagraphConfig.getAlignment() != null) {
            paragraph.setAlignment(normalParagraphConfig.getAlignment());
        }

        // 设置首行缩进量
        //TODO 很可能是错误的
//        if(normalParagraphConfig.getFirstLineIndentUnit().equals("ch")){
//            paragraph.setIndentationFirstLine((int)normalParagraphConfig.getFirstLineIndentTwips()*100);
//        }
//        else{
//            paragraph.setFirstLineIndent((int)normalParagraphConfig.getFirstLineIndentTwips());
//        }
//        paragraph.setFirstLineIndent((int)normalParagraphConfig.getFirstLineIndentTwips());
//        paragraph.set


        if(normalParagraphConfig.getFirstLineIndentUnit().equals("ch")){
            //设置首行缩进
            // 获取段落的 XML 属性容器
            CTP ctp1 = paragraph.getCTP();
            CTPPr ppr1 = ctp1.isSetPPr() ? ctp1.getPPr() : ctp1.addNewPPr();

            // 获取或创建缩进元素
            CTInd ind = ppr1.isSetInd() ? ppr1.getInd() : ppr1.addNewInd();

            // 设置首行缩进 2 个字符
            ind.setFirstLineChars(BigInteger.valueOf((long)normalParagraphConfig.getFirstLineIndentTwips()*100));
        }
        else{
            paragraph.setFirstLineIndent((int)normalParagraphConfig.getFirstLineIndentTwips());
        }



        org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing spacing = ppr.addNewSpacing();

//// 明确设置段前距
//        spacing.setBeforeLines(BigInteger.valueOf((long)normalParagraphConfig.getSpacingBefore()));
//
//// 明确设置段后距
//        spacing.setAfterLines(BigInteger.valueOf((long)normalParagraphConfig.getSpacingAfter()));
//TODO 待验证
// 明确设置段前距
        if(normalParagraphConfig.getBeforeUnit().equals("line")){
            spacing.setBeforeLines(BigInteger.valueOf((long)normalParagraphConfig.getSpacingBefore()*100));
        }
        else{
            spacing.setBefore(normalParagraphConfig.getSpacingBefore());
        }

// 明确设置段后距
        if( normalParagraphConfig.getAfterUnit().equals("line")){
            spacing.setAfterLines(BigInteger.valueOf((long)normalParagraphConfig.getSpacingAfter()*100));
        }
        else{
            spacing.setAfter(normalParagraphConfig.getSpacingAfter());
        }

        //TODO 待验证
        if(normalParagraphConfig.getLeftIndentUnit().equals("ch")){
            paragraph.setIndentationLeftChars((int) normalParagraphConfig.getLeftIndentTwips()*100);
        }
        else{
            paragraph.setIndentationLeft((int)normalParagraphConfig.getLeftIndentTwips());
        }
        if(normalParagraphConfig.getRightIndentUnit().equals("ch")){
            paragraph.setIndentationRightChars((int)normalParagraphConfig.getRightIndentTwips()*100);
        }
        else{
            paragraph.setIndentationRight((int)normalParagraphConfig.getRightIndentTwips());
        }

//        //
//        // 设置左缩进量
//        paragraph.setIndentationLeft((int)normalParagraphConfig.getLeftIndentTwips());
//        paragraph.setIndentationLeftChars
//        // 设置右缩进量
//        paragraph.setIndentationRight((int)normalParagraphConfig.getRightIndentTwips());


        // 设置行间距和行间距规则
        if (normalParagraphConfig.getLineSpacingTwips() != null &&normalParagraphConfig.getLineSpacingRule() != null) {
            if(normalParagraphConfig.getLineSpacingRule().equals(STLineSpacingRule.EXACT)&&hasPiOrFu){
                spacing.setLine(normalParagraphConfig.getLineSpacingTwips());
                spacing.setLineRule(STLineSpacingRule.AT_LEAST);

            }
            else {
                spacing.setLine(normalParagraphConfig.getLineSpacingTwips());
                spacing.setLineRule(normalParagraphConfig.getLineSpacingRule());
            }
        }
    }



    private void applyPictureStyles(XWPFParagraph paragraph) {
        // 设置图片所在段落的对齐方式为居中对齐
//        paragraph.setAlignment(imageAlignment.getImageAlignment());
        // 获取段落的CTP对象
        org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP ctp = paragraph.getCTP();

        // 检查是否存在段落属性
        if (ctp.isSetPPr()) {
            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr pPr = ctp.getPPr();

            // 清除缩进设置（包括首行缩进、左缩进、右缩进）
            if (pPr.isSetInd()) {
                pPr.unsetInd();
            }
            // 清除对齐方式
            if (pPr.isSetJc()) {
                pPr.unsetJc();
            }
        }

        // 设置图片所在段落的新对齐方式
        paragraph.setAlignment(image.getImageAlignment());
    }

    private void applyTableStyles(XWPFTable table) {

        table.setTableAlignment(TableRowAlign.LEFT);

        // 然后设置为目标值
        table.setTableAlignment(tableAlignment.getTableAlignment());
    }
    public String extractAndRemoveNumbering(XWPFParagraph paragraph, XWPFDocument doc) {
        // 检查是否有编号
        BigInteger numId = paragraph.getNumID();
        if (numId == null) {
//            log.info("段落没有编号");
            return "";
        }

        String numberingText = "";

        try {

            ParagraphNumberingInfo currentInfo = numberingInfoMap.get(paragraph);
            if (currentInfo == null || currentInfo.getNumId() == null) {
                return "";
            }

            // 获取文档和编号信息
            XWPFNumbering numbering = doc.getNumbering();

            // 获取编号级别
            int level = currentInfo.getLevel();


            XWPFNum num = numbering.getNum(currentInfo.getNumId());
            if (num == null) {
//                log.info("编号信息获取失败，num 为 null");
                return "";
            }

            XWPFAbstractNum abstractNum = numbering.getAbstractNum(num.getCTNum().getAbstractNumId().getVal());
            if (abstractNum == null) {
//                log.info("编号信息获取失败，abstractNum 为 null");
                return "";
            }

            CTLvl lvl = abstractNum.getCTAbstractNum().getLvlArray(level);
            if (lvl == null) {
//                log.info("编号信息获取失败，lvl 为 null");
                return "";
            }

            // 获取编号文本格式
            String lvlText = lvl.getLvlText().getVal();

            // 获取编号格式类型 - 处理枚举值
            String numFmt = "decimal"; // 默认格式
            if (lvl.isSetNumFmt()) {
                // 获取枚举值的字符串表示
                numFmt = lvl.getNumFmt().getVal().toString();
//                log.info("编号格式类型: " + numFmt);
            }


            // 检查是否是项目符号
            boolean isBullet = numFmt.contains("bullet");

            if (isBullet) {
//                log.info("检测到项目符号，保留原始格式");
                // 对于项目符号，不删除编号设置，直接返回空格
                return " ";  // 返回空格，让原始文本和项目符号一起显示
            }


            // 计算编号值
            int numValue = 1;
            for (XWPFParagraph p : doc.getParagraphs()) {
                if (p == paragraph) break;

                ParagraphNumberingInfo info = numberingInfoMap.get(p);
                if (info != null && info.getLevel() == level &&
                        info.getNumId() != null && info.getNumId().equals(currentInfo.getNumId())) {
//                    log.info("找到相同编号ID的段落：" + info.getNumId());
                    numValue++;
                }
            }

            // 根据格式类型转换数字
            String formattedNumber = formatNumberByType(numValue, numFmt);
//            log.info("编号值: " + numValue + ", 格式化后: " + formattedNumber);
            // 替换占位符并构建编号文本
            numberingText = lvlText.replace("%" + (level + 1), formattedNumber)
                    .replace("\t", " ");
        } catch (Exception e) {
            log.info("提取编号文本时出现异常", e);
            numberingText = "";
        }

        // 删除编号设置
//        paragraph.setNumID(null);//不再删除编号，而是直接对编号的样式进行设置
//        log.info("编号文本: " + numberingText);

        return numberingText;
    }


    /**
     * 根据编号格式类型和值，返回格式化的编号字符串
     * 处理枚举值字符串表示
     */
    private String formatNumberByType(int number, String numFmt) {
        // 转换为小写以便统一处理
        numFmt = numFmt.toLowerCase();

        if (numFmt.contains("decimal")) {
            if (numFmt.contains("enclosedcircle")) {
                return toEnclosedCircleNumber(number);
            } else if (numFmt.contains("enclosedparen")) {
                return toEnclosedParenNumber(number);
            } else {
                return String.valueOf(number);
            }
        } else if (numFmt.contains("roman")) {
            if (numFmt.contains("lower")) {
                return toRoman(number).toLowerCase();
            } else {
                return toRoman(number);
            }
        } else if (numFmt.contains("letter")) {
            if (numFmt.contains("lower")) {
                return toLetter(number).toLowerCase();
            } else {
                return toLetter(number);
            }
        } else if (numFmt.contains("chinese") || numFmt.contains("counting")) {
            return toChineseNumber(number);
        } else if (numFmt.contains("bullet")) {
            return "•"; // 项目符号
        } else {
            // 默认返回十进制数字
            return String.valueOf(number);
        }
    }

    /**
     * 转换数字为带圈数字 ①, ②, ③...
     */
    private String toEnclosedCircleNumber(int number) {
        if (number >= 1 && number <= 20) {
            // Unicode 带圈数字从 ① (U+2460) 开始
            return String.valueOf((char)(0x2460 + number - 1));
        }
        return String.valueOf(number);
    }

    /**
     * 转换数字为带括号数字 ⑴, ⑵, ⑶...
     */
    private String toEnclosedParenNumber(int number) {
        if (number >= 1 && number <= 20) {
            // Unicode 带括号数字从 ⑴ (U+2474) 开始
            return String.valueOf((char)(0x2474 + number - 1));
        }
        return "(" + number + ")";
    }

    /**
     * 转换数字为罗马数字
     */
    private String toRoman(int number) {
        if (number <= 0) return String.valueOf(number);

        String[] thousands = {"", "M", "MM", "MMM"};
        String[] hundreds = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
        String[] tens = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
        String[] units = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};

        return thousands[Math.min(number / 1000, 3)] +
                hundreds[Math.min((number % 1000) / 100, 9)] +
                tens[Math.min((number % 100) / 10, 9)] +
                units[Math.min(number % 10, 9)];
    }

    /**
     * 转换数字为字母 (1=A, 2=B, ...)
     */
    private String toLetter(int number) {
        if (number <= 0) return String.valueOf(number);

        StringBuilder result = new StringBuilder();
        while (number > 0) {
            number--;
            result.insert(0, (char)('A' + (number % 26)));
            number /= 26;
        }
        return result.toString();
    }

    /**
     * 转换数字为中文数字
     */
    private String toChineseNumber(int number) {
        if (number <= 0) return String.valueOf(number);

        String[] chineseDigits = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        String[] chineseUnits = {"", "十", "百", "千", "万"};

        if (number == 0) return chineseDigits[0];

        StringBuilder result = new StringBuilder();
        int unitPos = 0;
        boolean needZero = false;

        while (number > 0) {
            int digit = number % 10;
            if (digit == 0) {
                if (needZero) {
                    result.insert(0, chineseDigits[0]);
                    needZero = false;
                }
            } else {
                result.insert(0, chineseDigits[digit] + chineseUnits[unitPos]);
                needZero = true;
            }

            unitPos++;
            number /= 10;
        }

        return result.toString();
    }


    /**
     * 收集文档中所有段落的编号信息
     *
     * @param doc 文档对象
     * @return 段落与其编号信息的映射
     */
    private Map<XWPFParagraph, ParagraphNumberingInfo> collectParagraphNumberingInfo(XWPFDocument doc) {
        Map<XWPFParagraph, ParagraphNumberingInfo> infoMap = new HashMap<>();

        for (XWPFParagraph p : doc.getParagraphs()) {
            BigInteger numId = p.getNumID();
            BigInteger ilvl = p.getNumIlvl();

            if (numId != null) {
                int level = (ilvl != null) ? ilvl.intValue() : 0;
                infoMap.put(p, new ParagraphNumberingInfo(numId, level));
            }
        }

        return infoMap;
    }



    /**
     * 设置段落编号的多语言字体样式（完整版）
     * @param paragraph 要设置编号样式的段落
     * @param fontFamilyEastAsia 东亚文字字体（中文、日文、韩文等）
     * @param fontFamilyAscii ASCII字符字体（基本英文字母和数字）
     * @param fontFamilyHAnsi 高ANSI字符字体（带重音的欧洲字符）
     * @param fontFamilyCs 复杂脚本字体（阿拉伯文、希伯来文等）
     * @param fontSize 字体大小（单位：磅）
     * @param bold 是否加粗
     * @param italic 是否倾斜
     * @param underline 是否添加下划线
     * @param color 颜色（十六进制RGB值，例如"FF0000"表示红色）
     * @return 是否成功设置样式
     */
    public boolean setNumberingStyle(XWPFParagraph paragraph,
                                     String fontFamilyEastAsia,
                                     String fontFamilyAscii,
                                     String fontFamilyHAnsi,
                                     String fontFamilyCs,
                                     int fontSize,
                                     boolean bold,
                                     boolean italic,
                                     boolean underline,
                                     String color) {
        try {
            // 1. 检查段落是否有编号
            BigInteger numId = paragraph.getNumID();
            if (numId == null) {
                return false; // 该段落没有编号
            }

            // 2. 获取文档中的编号管理器
            XWPFDocument document = paragraph.getDocument();
            XWPFNumbering numbering = document.getNumbering();
            if (numbering == null) {
                return false; // 文档没有编号定义
            }

            // 3. 获取编号的抽象编号ID
            XWPFNum num = numbering.getNum(numId);
            if (num == null) {
                return false;
            }
            BigInteger abstractNumId = num.getCTNum().getAbstractNumId().getVal();

            // 4. 获取段落的编号级别
            CTP ctP = paragraph.getCTP();
            CTPPr pPr = ctP.getPPr();
            if (pPr == null || pPr.getNumPr() == null || pPr.getNumPr().getIlvl() == null) {
                return false; // 无法确定级别
            }
            BigInteger ilvl = pPr.getNumPr().getIlvl().getVal();

            // 5. 获取抽象编号定义
            CTAbstractNum abstractNum = numbering.getAbstractNum(abstractNumId).getCTAbstractNum();

            // 6. 获取指定级别的样式定义
            CTLvl level = null;
            List<CTLvl> lvlList = abstractNum.getLvlList();
            for (CTLvl lvl : lvlList) {
                if (lvl.getIlvl().equals(ilvl)) {
                    level = lvl;
                    break;
                }
            }

            // 如果指定级别不存在，则创建新级别
            if (level == null) {
                return false; // 级别不存在
//                level = abstractNum.addNewLvl();
//                level.setIlvl(ilvl);
//
//                // 通过检查段落属性判断是项目符号还是数字编号
//                boolean isBullet = isBulletList(paragraph);
//
//                // 设置编号格式，保留原始类型
//                CTNumFmt numFmt = level.addNewNumFmt();
//                if (isBullet) {
//                    numFmt.setVal(STNumberFormat.BULLET); // 项目符号
//
//                    // 设置项目符号文本
//                    CTLevelText lvlText = level.addNewLvlText();
//                    lvlText.setVal("•"); // 默认圆点符号
//
//                    // 可以根据需要设置不同级别的不同符号
//                    if (ilvl.intValue() == 1) {
//                        lvlText.setVal("○");
//                    } else if (ilvl.intValue() == 2) {
//                        lvlText.setVal("■");
//                    }
//                } else {
//                    numFmt.setVal(STNumberFormat.DECIMAL); // 数字编号
//
//                    CTLevelText lvlText = level.addNewLvlText();
//                    lvlText.setVal("%" + (ilvl.intValue() + 1) + ".");
//                }
//
//                CTPPr lvlPPr = (CTPPr) (level.isSetPPr() ? level.getPPr() : level.addNewPPr());
//                CTInd ind = lvlPPr.isSetInd() ? lvlPPr.getInd() : lvlPPr.addNewInd();
//                ind.setLeft(BigInteger.valueOf(720 * (ilvl.intValue() + 1)));
//                ind.setHanging(BigInteger.valueOf(360));
            }

            // 7. 检查是否为项目符号
            boolean isBullet = false;
            if (level.isSetNumFmt()) {
                isBullet = level.getNumFmt().getVal() == STNumberFormat.BULLET;
            }


            // 8. 设置编号样式
            CTRPr rPr = null;
            if (level.isSetRPr()) {
                rPr = level.getRPr();
            } else {
                rPr = level.addNewRPr();
            }

            if (isBullet) {
//                CTFonts fonts = rPr.sizeOfRFontsArray() > 0 ? rPr.getRFontsArray(0) : rPr.addNewRFonts();
//
//                // 使用通用字体确保项目符号可见
//                fonts.setAscii("Symbol");
//                fonts.setHAnsi("Symbol");
//                fonts.setEastAsia("Symbol");
//                fonts.setCs("Symbol");
//项目符号的字体不去设置，保留原本的字体
                // 确保项目符号字符存在
                if (!level.isSetLvlText()) {
                    CTLevelText lvlText = level.addNewLvlText();
                    lvlText.setVal("•"); // 默认项目符号
                }
            } else {
                // 对于编号，使用用户指定的字体
                CTFonts fonts = rPr.sizeOfRFontsArray() > 0 ? rPr.getRFontsArray(0) : rPr.addNewRFonts();

                if (fontFamilyEastAsia != null) {
                    fonts.setEastAsia(fontFamilyEastAsia);
                }
                if (fontFamilyAscii != null) {
                    fonts.setAscii(fontFamilyAscii);
                }
                if (fontFamilyHAnsi != null) {
                    fonts.setHAnsi(fontFamilyHAnsi);
                }
                if (fontFamilyCs != null) {
                    fonts.setCs(fontFamilyCs);
                }
            }


            // 设置字体大小
            if (fontSize > 0) {
                CTHpsMeasure sz = null;
                if (rPr.sizeOfSzArray() > 0) {
                    sz = rPr.getSzArray(0);
                } else {
                    sz = rPr.addNewSz();
                }
                sz.setVal(BigInteger.valueOf(fontSize* 2));

                CTHpsMeasure szCs = null;
                if (rPr.sizeOfSzCsArray() > 0) {
                    szCs = rPr.getSzCsArray(0);
                } else {
                    szCs = rPr.addNewSzCs();
                }
                szCs.setVal(BigInteger.valueOf(fontSize * 2));
            }

            // 设置加粗
            if (rPr.sizeOfBArray() > 0) {
                CTOnOff b = rPr.getBArray(0);
                b.setVal(XmlString.Factory.newValue(bold ? "true" : "false"));
            } else if (bold) {
                CTOnOff b = rPr.addNewB();
                b.setVal(XmlString.Factory.newValue("true"));
            }

            // 设置倾斜
            if (rPr.sizeOfIArray() > 0) {
                CTOnOff i = rPr.getIArray(0);
                i.setVal(XmlString.Factory.newValue(italic ? "true" : "false"));
            } else if (italic) {
                CTOnOff i = rPr.addNewI();
                i.setVal(XmlString.Factory.newValue("true"));
            }

            // 设置下划线
            if (underline) {
                CTUnderline u = null;
                if (rPr.sizeOfUArray() > 0) {
                    u = rPr.getUArray(0);
                } else {
                    u = rPr.addNewU();
                }
                u.setVal(STUnderline.SINGLE);
            }

            // 设置颜色
            if (color != null && !color.isEmpty()) {
                CTColor ctColor = null;
                if (rPr.sizeOfColorArray() > 0) {
                    ctColor = rPr.getColorArray(0);
                } else {
                    ctColor = rPr.addNewColor();
                }
                ctColor.setVal(color);
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }




    /**
     * 为段落中的数学公式设置样式
     */
    public boolean setMathFormulaStyle(XWPFParagraph paragraph,
                                       String fontFamilyEastAsia,
                                       String fontFamilyAscii,
                                       String fontFamilyHAnsi,
                                       String fontFamilyCs,
                                       int fontSize,
                                       boolean bold,
                                       boolean italic,
                                       boolean underline,
                                       String color) {
        try {
            // 获取段落的CTP
            CTP ctp = paragraph.getCTP();

            // 创建XmlCursor来查找数学公式
            XmlCursor cursor = ctp.newCursor();

            // 使用XPath选择所有数学公式元素
            cursor.selectPath("declare namespace m='http://schemas.openxmlformats.org/officeDocument/2006/math' .//m:oMath");

            boolean foundFormula = false;

            // 遍历找到的每个数学公式
            while (cursor.toNextSelection()) {
                foundFormula = true;

                // 获取当前oMath元素
                XmlObject mathObj = cursor.getObject();

                // 创建新的游标遍历该数学公式中的所有文本运行
                XmlCursor mathCursor = mathObj.newCursor();

                // 查找所有包含w:rPr元素的运行
                mathCursor.selectPath("declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' " +
                        "declare namespace m='http://schemas.openxmlformats.org/officeDocument/2006/math' " +
                        ".//w:rPr");

                // 遍历每个rPr元素并设置样式
                while (mathCursor.toNextSelection()) {
                    XmlObject rPrObj = mathCursor.getObject();

                    // 创建CTRPr对象以便使用POI API设置样式
                    CTRPr rPr = null;
                    if (rPrObj instanceof CTRPr) {
                        rPr = (CTRPr) rPrObj;
                    } else {
                        // 如果不是CTRPr类型，跳过
                        continue;
                    }

                    // 1. 设置字体族 - 修正方法调用
                    CTFonts fonts = null;
                    // 检查是否有字体设置
                    if (rPr.sizeOfRFontsArray() > 0) {
                        fonts = rPr.getRFontsArray(0);
                    } else {
                        fonts = rPr.addNewRFonts();
                    }

                    // 为不同的字符范围设置字体
                    if (fontFamilyEastAsia != null && !fontFamilyEastAsia.isEmpty()) {
                        fonts.setEastAsia(fontFamilyEastAsia);
                    }

                    if (fontFamilyAscii != null && !fontFamilyAscii.isEmpty()) {
                        fonts.setAscii(fontFamilyAscii);
                    }

                    if (fontFamilyHAnsi != null && !fontFamilyHAnsi.isEmpty()) {
                        fonts.setHAnsi(fontFamilyHAnsi);
                    }

                    if (fontFamilyCs != null && !fontFamilyCs.isEmpty()) {
                        fonts.setCs(fontFamilyCs);
                    }

                    // 2. 设置字体大小 - 修正方法调用
                    if (fontSize > 0) {
                        // Word中字体大小以半点为单位，所以乘以2
                        CTHpsMeasure sz = null;
                        if (rPr.sizeOfSzArray() > 0) {
                            sz = rPr.getSzArray(0);
                        } else {
                            sz = rPr.addNewSz();
                        }
                        sz.setVal(BigInteger.valueOf(fontSize * 2));

                        // 设置复杂脚本的字体大小
                        CTHpsMeasure szCs = null;
                        if (rPr.sizeOfSzCsArray() > 0) {
                            szCs = rPr.getSzCsArray(0);
                        } else {
                            szCs = rPr.addNewSzCs();
                        }
                        szCs.setVal(BigInteger.valueOf(fontSize * 2));
                    }

                    // 3. 设置加粗 - 修正方法调用
                    CTOnOff b = null;
                    if (rPr.sizeOfBArray() > 0) {
                        b = rPr.getBArray(0);
                        b.setVal(XmlString.Factory.newValue(bold ? "true" : "false"));
                    } else if (bold) {
                        b = rPr.addNewB();
                        b.setVal(XmlString.Factory.newValue("true"));
                    }

                    // 4. 设置斜体 - 修正方法调用
                    CTOnOff i = null;
                    if (rPr.sizeOfIArray() > 0) {
                        i = rPr.getIArray(0);
                        i.setVal(XmlString.Factory.newValue(italic ? "true" : "false"));
                    } else if (italic) {
                        i = rPr.addNewI();
                        i.setVal(XmlString.Factory.newValue("true"));
                    }

                    // 5. 设置下划线 - 修正方法调用
                    if (underline) {
                        CTUnderline u = null;
                        if (rPr.sizeOfUArray() > 0) {
                            u = rPr.getUArray(0);
                        } else {
                            u = rPr.addNewU();
                        }
                        u.setVal(STUnderline.SINGLE);
                    } else if (rPr.sizeOfUArray() > 0) {
                        // 如果不需要下划线且已存在，则移除
                        rPr.removeU(0);
                    }

                    // 6. 设置颜色 - 修正方法调用
                    if (color != null && !color.isEmpty()) {
                        CTColor ctColor = null;
                        if (rPr.sizeOfColorArray() > 0) {
                            ctColor = rPr.getColorArray(0);
                        } else {
                            ctColor = rPr.addNewColor();
                        }
                        ctColor.setVal(color);
                    }
                }

                mathCursor.dispose();
            }

            cursor.dispose();
            return foundFormula; // 返回是否找到并处理了公式

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 从MultipartFile修改特定段落中的数学公式样式
     *
//     * @param file MultipartFile类型的docx文档
     * @param paragraph 要处理的特定段落

     * @return 处理后的文档字节数组
     */
//    public XWPFDocument modifyMathFormulaInParagraph(XWPFDocument document, XWPFParagraph paragraph,
//                                               String fontFamily, int fontSize,
//                                               String color, boolean bold, boolean italic) throws Exception {
//        // 确保是docx文件
////        if (!file.getOriginalFilename().toLowerCase().endsWith(".docx")) {
////            throw new IllegalArgumentException("只支持DOCX格式文档");
////        }
//
//        // 从MultipartFile加载文档
////        XWPFDocument document = new XWPFDocument(file.getInputStream());
//
//        // 获取段落的paraId
//        String paraId = getParagraphId(paragraph);
//        if (paraId == null) {
//            throw new IllegalArgumentException("无法获取段落ID");
//        }
//
//        // 将文档转换为XML字符串
//        String documentXml = XmlUtils.marshaltoString(document.getDocument(), true);
//
//        // 准备XSLT转换
//        StringBuilder xsltBuilder = new StringBuilder();
//        xsltBuilder.append("<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\n");
//        xsltBuilder.append("    xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\"\n");
//        xsltBuilder.append("    xmlns:m=\"http://schemas.openxmlformats.org/officeDocument/2006/math\"\n");
//        xsltBuilder.append("    xmlns:w14=\"http://schemas.microsoft.com/office/word/2010/wordml\">\n\n");
//
//        xsltBuilder.append("    <!-- 默认复制所有节点 -->\n");
//        xsltBuilder.append("    <xsl:template match=\"@*|node()\">\n");
//        xsltBuilder.append("        <xsl:copy>\n");
//        xsltBuilder.append("            <xsl:apply-templates select=\"@*|node()\"/>\n");
//        xsltBuilder.append("        </xsl:copy>\n");
//        xsltBuilder.append("    </xsl:template>\n\n");
//
//        xsltBuilder.append("    <!-- 修改特定段落中的数学公式样式 -->\n");
//        xsltBuilder.append("    <xsl:template match=\"w:p[@w14:paraId='").append(paraId).append("']//w:rPr[ancestor::m:oMath or ancestor::m:oMathPara]\">\n");
//        xsltBuilder.append("        <w:rPr>\n");
//
//        // 添加字体设置
//        if (fontFamily != null && !fontFamily.isEmpty()) {
//            xsltBuilder.append("            <w:rFonts w:ascii=\"").append(fontFamily).append("\" ");
//            xsltBuilder.append("w:hAnsi=\"").append(fontFamily).append("\" ");
//            xsltBuilder.append("w:eastAsia=\"").append(fontFamily).append("\" ");
//            xsltBuilder.append("w:cs=\"").append(fontFamily).append("\"/>\n");
//        }
//
//        // 添加字体大小
//        if (fontSize > 0) {
//            xsltBuilder.append("            <w:sz w:val=\"").append(fontSize).append("\"/>\n");
//            xsltBuilder.append("            <w:szCs w:val=\"").append(fontSize).append("\"/>\n");
//        }
//
//        // 添加颜色
//        if (color != null && !color.isEmpty()) {
//            xsltBuilder.append("            <w:color w:val=\"").append(color).append("\"/>\n");
//        }
//
//        // 添加加粗设置
//        if (bold) {
//            xsltBuilder.append("            <w:b/>\n");
//            xsltBuilder.append("            <w:bCs/>\n");
//        } else {
//            xsltBuilder.append("            <w:b w:val=\"0\"/>\n");
//            xsltBuilder.append("            <w:bCs w:val=\"0\"/>\n");
//        }
//
//        // 添加斜体设置
//        if (italic) {
//            xsltBuilder.append("            <w:i/>\n");
//            xsltBuilder.append("            <w:iCs/>\n");
//        } else {
//            xsltBuilder.append("            <w:i w:val=\"0\"/>\n");
//            xsltBuilder.append("            <w:iCs w:val=\"0\"/>\n");
//        }
//
//        xsltBuilder.append("        </w:rPr>\n");
//        xsltBuilder.append("    </xsl:template>\n");
//        xsltBuilder.append("</xsl:stylesheet>");
//
//        String xslt = xsltBuilder.toString();
//
//        // 执行XSLT转换
//        TransformerFactory factory = TransformerFactory.newInstance();
//        Transformer transformer = factory.newTransformer(new StreamSource(new StringReader(xslt)));
//
//        StringWriter writer = new StringWriter();
//        transformer.transform(
//                new StreamSource(new StringReader(documentXml)),
//                new StreamResult(writer)
//        );
//
//        String modifiedXml = writer.toString();
//
//        // 将修改后的XML解析回文档对象
////        XWPFDocument newDocument = new XWPFDocument();
////        OPCPackage ctDoc = (OPCPackage) CTDocument1.Factory.parse(modifiedXml);
////        document = new XWPFDocument((OPCPackage) ctDoc);
//
//        // 将 XML 字符串转换为输入流
//        InputStream inputStream = new ByteArrayInputStream(modifiedXml.getBytes());
//        // 使用输入流创建 XWPFDocument 对象
//        return new XWPFDocument(inputStream);
////        return document;
//        // 将文档转换为字节数组
////        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
////        newDocument.write(outputStream);
////        newDocument.close();
////        document.close();
////
////        return outputStream.toByteArray();
//    }
//    /**
//     * 获取段落的 paraId 属性
//     */
//    public String getParagraphId(XWPFParagraph paragraph) {
//        try {
//            // 获取段落的 XML
//            String paragraphXml = paragraph.getCTP().xmlText();
//
//            // 查找 w14:paraId 属性
//            int paraIdIndex = paragraphXml.indexOf("w14:paraId=\"");
//            if (paraIdIndex != -1) {
//                int startIndex = paraIdIndex + "w14:paraId=\"".length();
//                int endIndex = paragraphXml.indexOf("\"", startIndex);
//                if (endIndex != -1) {
//                    return paragraphXml.substring(startIndex, endIndex);
//                }
//            }
//
//            // 直接从DOM节点获取
//            Node paragraphNode = paragraph.getCTP().getDomNode();
//            if (paragraphNode.getAttributes() != null) {
//                for (int i = 0; i < paragraphNode.getAttributes().getLength(); i++) {
//                    Node attr = paragraphNode.getAttributes().item(i);
//                    if (attr.getNodeName().contains("paraId")) {
//                        return attr.getNodeValue();
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
    /**
     * 使用XmlCursor检测段落中是否包含Office MathML数学公式
     * @param paragraph 要检查的段落
     * @return 如果包含MathML公式返回true，否则返回false
     */
    public boolean containsMathFormula(XWPFParagraph paragraph) {
        if (paragraph == null) {
            return false;
        }

        try {
            // 获取段落的CTP对象
            CTP ctp = paragraph.getCTP();

            // 创建XmlCursor来遍历XML结构
            try (XmlCursor cursor = ctp.newCursor()) {
                // 声明MathML命名空间并设置XPath查询
                cursor.selectPath(
                        "declare namespace m='http://schemas.openxmlformats.org/officeDocument/2006/math' " +
                                ".//m:oMath"
                );

                // 检查是否找到任何匹配的数学公式节点
                while (cursor.hasNextSelection()) {
                    cursor.toNextSelection();
                    return true;  // 找到至少一个数学公式
                }
            }
        } catch (Exception e) {
            // 发生异常时保守返回false
            e.printStackTrace();
        }

        return false;
    }

    public boolean hasImage(XWPFParagraph paragraph) {
        for (XWPFRun run : paragraph.getRuns()) {
            if (run.getEmbeddedPictures().size() > 0) {
                return true;  // 发现图片
            }
        }
        return false;
    }

    /**
     * 设置段落中所有图片的高度为指定厘米数，宽度等比例缩放
     * @param paragraph 段落对象
//     * @param targetHeightInCm 目标高度（厘米）
     */
//    public static void setImageHeight(XWPFParagraph paragraph, double targetHeightInCm) {
//        log.info("固定高度");
//        double targetHeightInEmu = targetHeightInCm * 360000;
//        for (XWPFRun run : paragraph.getRuns()) {
//            for (XWPFPicture picture : run.getEmbeddedPictures()) {
//                // 获取图片数据
//                XWPFPictureData pictureData = picture.getPictureData();
//                // 计算原始宽高比
//                double ratio = (double) picture.getCTPicture().getSpPr().getXfrm().getExt().getCx() /
//                        picture.getCTPicture().getSpPr().getXfrm().getExt().getCy();
//                // 计算新的宽度
//                int newWidth = (int) (targetHeightInEmu * ratio);
//                // 设置新的宽度和高度
//                log.info("newWidth: " + newWidth);
//                log.info("targetHeightInEmu: " + targetHeightInEmu);
//                picture.getCTPicture().getSpPr().getXfrm().getExt().setCx(newWidth);
//                picture.getCTPicture().getSpPr().getXfrm().getExt().setCy((long) targetHeightInEmu);
//            }
//        }
//    }
    public void setImageHeight(XWPFParagraph paragraph, double targetHeight) {
        imageService.resizeImages(paragraph, targetHeight,"height");
    }
    public void setImageWidth(XWPFParagraph paragraph, double targetWidth) {
//        double targetWidthInEmu = targetWidth * 360000;
        imageService.resizeImages(paragraph, targetWidth,"width");
    }


    //检查是否有编号
    public static boolean hasNumbering(XWPFParagraph paragraph) {
        if (paragraph == null) {
            return false;
        }

        // 获取段落的编号ID
        BigInteger numID = paragraph.getNumID();
        if (numID == null || numID.equals(BigInteger.ZERO)) {
            return false; // 无编号ID或默认值
        }

        // 获取文档的编号列表
        XWPFDocument doc = paragraph.getDocument();
        if (doc == null) {
            return false;
        }

        XWPFNumbering numbering = doc.getNumbering();
        if (numbering == null) {
            return false;
        }

        // 检查是否存在对应的编号定义
        XWPFNum num = numbering.getNum(numID);
        return num != null;
    }

}