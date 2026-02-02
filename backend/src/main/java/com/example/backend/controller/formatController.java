package com.example.backend.controller;

import com.example.backend.formatConfig.DocumentFormatConfig;
import com.example.backend.service.DocumentFormatService;
import com.example.backend.service.ImageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@CrossOrigin
public class formatController {


    private final DocumentFormatService documentFormatService;

    private final ImageService testService;
    private final ObjectMapper objectMapper;

    public formatController(DocumentFormatService documentFormatService, ObjectMapper objectMapper , ImageService testService) {
        this.documentFormatService = documentFormatService;
        this.objectMapper = objectMapper;
        this.testService = testService;
    }

    @PostMapping("/api/process")
    public ResponseEntity<byte[]> uploadAndFormatDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("config") String configJson) throws Exception {
//        log.info("Received file: {} and config: {}", file.getOriginalFilename(), configJson);
        //json中的数据映射到DocumentFormatConfig类
        DocumentFormatConfig config = objectMapper.readValue(configJson, DocumentFormatConfig.class);
        log.info("解析得到的配置: {}", config);


        //在service设置文件格式变量
        documentFormatService.setParams(config);
//        testService.setParams(config);
        //测试图片功能
//        byte[] processedFile=testService.modifyMathStyles(file);


        byte[] processedFile = documentFormatService.applyFormat(file);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "processed_document.docx");

        return new ResponseEntity<>(processedFile, headers, HttpStatus.OK);
    }
}
