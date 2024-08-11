package com.example.common;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Configuration
@Component
public class TesseractConfig {

    @Value("${tesseract.dataPath}")
    private String tessDataPath;

    private final ResourceLoader resourceLoader;

    public TesseractConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    @Bean
    public Tesseract tesseract() throws  IOException {
        Tesseract tesseract = new Tesseract();

        // 使用ResourceLoader加载资源路径
        Resource resource = resourceLoader.getResource(tessDataPath);
        String absolutePath = resource.getFile().getAbsolutePath();
        tesseract.setDatapath(absolutePath);

        tesseract.setLanguage("chi_sim"); // 设置语言
        return tesseract;
    }
}