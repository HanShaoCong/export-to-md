package com.ssymon.export.db.exporter;

import com.ssymon.export.db.config.AppConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySQLDatabaseExporter extends AbstractDatabaseExporter implements DatabaseExporter {

    public MySQLDatabaseExporter(AppConfig config) {
        super(config);
    }

    @Override
    protected PreparedStatement getTablePreparedStatement(Connection connection, String database) throws SQLException {
        return connection.prepareStatement(getTablesSQL(database));
    }

    @Override
    protected PreparedStatement getColumnPreparedStatement(Connection connection, String database) throws SQLException {
        return connection.prepareStatement(getColumnsSQL(database));
    }

    @Override
    protected void setColumnPreparedStatementParams(PreparedStatement ps, String tableName) throws SQLException {
        ps.setString(1, tableName);
    }


    private String getTablesSQL(String database) {
        return "SELECT `TABLE_NAME`,`TABLE_COMMENT` FROM `INFORMATION_SCHEMA`.`TABLES` " +
                "WHERE `TABLE_TYPE` = 'BASE TABLE' AND `TABLE_SCHEMA` = '" + database + "'";
    }


    private String getColumnsSQL(String database) {
        return "SELECT `ORDINAL_POSITION`,`COLUMN_NAME`,`COLUMN_TYPE`,`IS_NULLABLE`,`COLUMN_DEFAULT`,`COLUMN_COMMENT` " +
                "FROM `INFORMATION_SCHEMA`.`COLUMNS` WHERE `TABLE_SCHEMA`= '" + database +
                "' AND `TABLE_NAME`= ? ORDER BY `ORDINAL_POSITION`";
    }

    private String getPrimaryKeySQL(String database) {
        return "SELECT `COLUMN_NAME` FROM `INFORMATION_SCHEMA`.`KEY_COLUMN_USAGE` WHERE `TABLE_SCHEMA` = '" + database +
                "' AND `TABLE_NAME` = ? AND CONSTRAINT_NAME='PRIMARY'";
    }

    @Override
    protected List<String> getPrimaryKeyColumnNames(Connection connection, String database, String tableName) throws SQLException {
        List<String> names = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(getPrimaryKeySQL(database))) {
            preparedStatement.setString(1, tableName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    if (!names.contains(resultSet.getString(1))) {
                        names.add(resultSet.getString(1));
                    }
                }
            }
        }
        return names;
    }
}
