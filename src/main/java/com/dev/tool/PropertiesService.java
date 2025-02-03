package com.dev.tool;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

public class PropertiesService {
    private static final String PROPERTIES_FILE = "config.properties";
    private static Properties PROPERTIES;

    private static Properties getProperties() throws Exception {
        if (PROPERTIES == null) {
            PROPERTIES = new Properties();
            File file = new File(PROPERTIES_FILE);
            if (file.exists()) {
                try (InputStream inputStream = Files.newInputStream(file.toPath())) {
                    PROPERTIES.load(inputStream);
                }
            }
        }
        return PROPERTIES;
    }

    public static String getProperty(String key) throws Exception {
        return getProperties().getProperty(key);
    }

    public static void setProperty(String key, String value) throws Exception {
        File file = new File(PROPERTIES_FILE);
        if (!file.exists()) {
            file.createNewFile();
        }
        getProperties().setProperty(key, value);
    }
}
