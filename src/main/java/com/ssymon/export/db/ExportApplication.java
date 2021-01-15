package com.ssymon.export.db;

import com.ssymon.export.db.config.AppConfig;
import com.ssymon.export.db.exporter.DatabaseExporter;
import com.ssymon.export.db.exporter.MySQLDatabaseExporter;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class ExportApplication {

    public static void main(String[] args) {

        AppConfig appConfig;
        try {
            appConfig = loadConfig();
        } catch (Exception e) {
            log.error("Loading config exception ", e);
            return;
        }

        DatabaseExporter exporter = new MySQLDatabaseExporter(appConfig);
        try {
            exporter.export();
        } catch (Exception e) {
            log.error("Export exception ", e);
        }

    }

    private static AppConfig loadConfig() throws IOException {
        String application = "application.yml";
        log.info("Loading config - {}", application);
        InputStream configInputStream = ExportApplication.class.getClassLoader().getResourceAsStream(application);
        Yaml yaml = new Yaml(new Constructor(AppConfig.class));
        try (InputStream inputStream = configInputStream) {
            return yaml.load(inputStream);
        }
    }
}
