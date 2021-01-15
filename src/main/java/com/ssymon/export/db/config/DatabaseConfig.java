package com.ssymon.export.db.config;

import lombok.Data;

import java.util.List;

@Data
public class DatabaseConfig {
    private String driver;

    private String url;

    private String username;

    private String password;

    private List<String> database;
}
