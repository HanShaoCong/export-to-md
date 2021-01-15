package com.ssymon.export.db.util;


import com.ssymon.export.db.ExportApplication;
import com.ssymon.export.db.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class ConfigUtil {

    private ConfigUtil(){

    }

    public static AppConfig loadConfig() {
        String application = "application.yml";
        log.info("Loading config - {}", application);
        InputStream configInputStream = ExportApplication.class.getClassLoader().getResourceAsStream(application);
        Yaml yaml = new Yaml(new Constructor(AppConfig.class));
        try (InputStream inputStream = configInputStream) {
            return yaml.load(inputStream);
        } catch (IOException e) {
            log.error("Loading config exception ", e);
            throw new RuntimeException("Loading config exception");
        }
    }
}
