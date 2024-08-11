package com.example.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

@Component
public class AlprConfig {

    private static String CONFIG_FILE;
    private static String RUNTIME_DIR;

    @Autowired
    public AlprConfig(ResourceLoader resourceLoader) {
        try {
            Resource configFileResource = resourceLoader.getResource("classpath:openalpr/config/openalpr.conf");
            Resource runtimeDirResource = resourceLoader.getResource("classpath:openalpr/runtime_data");

            CONFIG_FILE = Paths.get(configFileResource.getURI()).toString();
            RUNTIME_DIR = Paths.get(runtimeDirResource.getURI()).toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load resources", e);
        }
    }

    public static String getConfigFile() {
        return CONFIG_FILE;
    }

    public static String getRuntimeDir() {
        return RUNTIME_DIR;
    }
}
