package com.example.service;


import com.example.common.AlprConfig;
import com.openalpr.jni.*;
import com.openalpr.jni.AlprException;
import com.openalpr.jni.AlprResults;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


@Service
public class LicensePlateRecognitionService {


    private static final String COUNTRY = "us"; // 根据实际情况调整国家代码

    private  final String CONFIG_FILE = AlprConfig.getConfigFile();
    private  final String RUNTIME_DIR = AlprConfig.getRuntimeDir();
      public String recognizeLicensePlate(String imagePath) throws AlprException {
        System.out.println(CONFIG_FILE);
        System.out.println(RUNTIME_DIR);

        Alpr alpr = new Alpr(COUNTRY, CONFIG_FILE, RUNTIME_DIR);
        AlprResults results = alpr.recognize(imagePath);
        alpr.unload();

        if (results.getPlates().isEmpty()) {
            return null;
        } else {
            return results.getPlates().get(0).getBestPlate().getCharacters();
        }
    }
}