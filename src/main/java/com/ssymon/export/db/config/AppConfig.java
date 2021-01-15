package com.ssymon.export.db.config;

import lombok.Data;

@Data
public class AppConfig {
    private DatabaseConfig db;
    private ExportConfig export;
}
