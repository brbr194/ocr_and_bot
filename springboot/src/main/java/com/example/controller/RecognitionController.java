package com.example.controller;

import com.example.common.Result;
import com.example.entity.Recognize;
import com.example.service.RecognitionService;
import jakarta.annotation.Resource;

import com.github.pagehelper.PageInfo;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/orc")
public class RecognitionController {

    @Resource
    private Tesseract tesseract;

    @Resource
    private RecognitionService recognitionService;

    public MultipartFile recognizeLicenseFromUrl(@RequestParam("url") String fileUrl) {
        try {
            // 创建一个URL资源
            UrlResource resource = new UrlResource(fileUrl);
            // 打开资源的InputStream
            InputStream inputStream = resource.getInputStream();
            // 创建一个MultipartFile
            MultipartFile multipartFile = new MockMultipartFile("file",
                    resource.getFilename(),
                    "image/png",
                    inputStream);
            return multipartFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String recognizeLicensePlate(MultipartFile file) {
        try {
            File convFile = convert(file);
            String recognizedText = tesseract.doOCR(convFile);
            return recognizedText;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    
    public static File convert(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    @PostMapping("/add")
    public Result add(@RequestBody Recognize recognize){
        MultipartFile multipartFile = recognizeLicenseFromUrl(recognize.getImageUrl());
        String text = recognizeLicensePlate(multipartFile);
        recognize.setRecognitionResult(text);
        recognitionService.add(recognize);
        return Result.success();
    }

    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "5") Integer pageSize,
                             Recognize recognize){

        PageInfo<Recognize> recognizePageInfo = recognitionService.selectPage(pageNum, pageSize,recognize);
        return Result.success(recognizePageInfo);
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id){
        recognitionService.deleteById(id);
        return Result.success();
    }


}