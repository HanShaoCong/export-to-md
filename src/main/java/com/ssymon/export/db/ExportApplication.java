package com.ssymon.export.db;

import com.ssymon.export.db.exporter.DatabaseExporter;
import com.ssymon.export.db.exporter.MySQLDatabaseExporter;
import com.ssymon.export.db.util.ConfigUtil;

public class ExportApplication {

    public static void main(String[] args) {
        DatabaseExporter exporter = new MySQLDatabaseExporter(ConfigUtil.loadConfig());
        exporter.export();
    }
}
