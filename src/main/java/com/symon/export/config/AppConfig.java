package com.symon.export.config;

import lombok.Data;

@Data
public class AppConfig {
    private DatabaseConfig db;
    private ExportConfig export;
}
