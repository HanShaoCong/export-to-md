package com.symon.export.exporter;

import com.symon.export.config.DatabaseConfig;
import com.symon.export.config.AppConfig;
import com.symon.export.constant.PatternConstant;
import com.symon.export.domain.Column;
import com.symon.export.domain.Table;
import com.symon.export.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public abstract class AbstractDatabaseExporter implements DatabaseExporter {

    private final AppConfig config;

    private Connection connection;

    public AbstractDatabaseExporter(AppConfig config) {
        this.config = config;
    }

    @Override
    public void export() throws IOException, SQLException, ClassNotFoundException {
        List<String> databases = config.getDb().getDatabase();
        try (Connection conn = getConnection()) {
            for (String database : databases) {
                File markdownFile = export(conn, database);
                log.info("导出成功 ： {}", markdownFile.getAbsolutePath());
            }
        }
    }

    private File export(Connection connection, String database) throws IOException, SQLException {

        File markdownFile = getOutputFile(database);
        try (FileWriter fileWriter = new FileWriter(markdownFile, false)) {

            String title = String.format(PatternConstant.TITLE, database);
            fileWriter.write(title);
            writeLineSeparator(fileWriter, 2);

            String exportDate = String.format(PatternConstant.DATE, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            fileWriter.write(exportDate);
            writeLineSeparator(fileWriter, 2);

            List<Table> tables;
            try (PreparedStatement ps = getTablePreparedStatement(connection, database)) {
                tables = getTables(ps);
            }

            try (PreparedStatement ps = getColumnPreparedStatement(connection, database)) {
                for (Table table : tables) {
                    String tableName = table.getTableName();
                    setColumnPreparedStatementParams(ps, tableName);
                    List<Column> columns = getColumns(connection, ps, database, tableName);

                    fileWriter.write(String.format(PatternConstant.CATALOG, tableName));
                    writeLineSeparator(fileWriter, 1);
                    fileWriter.write(String.format(PatternConstant.COMMENT, table.getTableComment()));
                    writeLineSeparator(fileWriter, 2);
                    fileWriter.write(PatternConstant.TABLE_HEADER);
                    writeLineSeparator(fileWriter, 1);
                    fileWriter.write(PatternConstant.TABLE_SEPARATOR);
                    writeLineSeparator(fileWriter, 1);

                    for (Column column : columns) {
                        fileWriter.write(String.format(PatternConstant.TABLE_BODY, getColumnValues(column)));
                        writeLineSeparator(fileWriter, 1);
                    }

                    writeLineSeparator(fileWriter, 2);
                }
            }
        }
        return markdownFile;
    }

    protected abstract PreparedStatement getTablePreparedStatement(Connection connection, String database) throws SQLException;

    protected abstract PreparedStatement getColumnPreparedStatement(Connection connection, String database) throws SQLException;

    protected abstract void setColumnPreparedStatementParams(PreparedStatement ps, String tableName) throws SQLException;

    protected abstract List<String> getPrimaryKeyColumnNames(Connection connection, String database, String tableName) throws SQLException;

    protected Connection getConnection() throws ClassNotFoundException, SQLException {
        if (connection == null) {
            DatabaseConfig databaseConfig = config.getDb();
            Class.forName(databaseConfig.getDriver());
            connection = DriverManager.getConnection(databaseConfig.getUrl(), databaseConfig.getUsername(), databaseConfig.getPassword());
        }
        return connection;
    }

    protected List<Table> getTables(PreparedStatement ps) throws SQLException {
        List<Table> tables = new ArrayList<>();
        try (ResultSet tableResult = ps.executeQuery()) {
            while (tableResult.next()) {
                String tableName = tableResult.getString(1);
                String tableComment = StringUtils.nullToBlank(tableResult.getString(2));
                Table table = new Table();
                table.setTableName(tableName);
                table.setTableComment(StringUtils.replaceSep(tableComment));
                tables.add(table);
            }
        }
        return tables;
    }

    protected List<Column> getColumns(Connection connection, PreparedStatement ps, String database, String tableName) throws SQLException {
        List<Column> columns = new ArrayList<>();
        try (ResultSet resultSet = ps.executeQuery()) {

            List<String> primaryKeyColumnNames = getPrimaryKeyColumnNames(connection, database, tableName);
            String primaryKeyComment = getPrimaryKeyComment(primaryKeyColumnNames.size());

            while (resultSet.next()) {
                Column column = new Column();
                column.setOrdinal(resultSet.getInt(1));
                column.setName(resultSet.getString(2));
                column.setDataType(resultSet.getString(3));
                column.setAllowNull(resultSet.getString(4));
                column.setDefaultValue(StringUtils.nullToBlank(resultSet.getString(5)));
                String comment = StringUtils.nullToBlank(resultSet.getString(6));
                column.setComment(StringUtils.replaceSep(comment));
                if (primaryKeyColumnNames.contains(column.getName()) && !comment.contains(primaryKeyComment)) {
                    comment = comment.length() != 0 ? primaryKeyComment + "," + comment : primaryKeyComment;
                    column.setComment(comment);
                }
                columns.add(column);
            }
        }
        return columns;
    }

    private Object[] getColumnValues(Column column) {
        Object[] values = new Object[6];
        values[0] = column.getOrdinal();
        values[1] = column.getName();
        values[2] = column.getDataType();
        values[3] = column.getAllowNull();
        values[4] = column.getDefaultValue();
        values[5] = column.getComment();
        return values;
    }

    private File getOutputFile(String database) {
        String exportPath = config.getExport().getDir();
        return new File(exportPath, database + ".md");
    }

    private void writeLineSeparator(FileWriter fileWriter, int number) throws IOException {
        for (int i = 0; i < number; i++) {
            fileWriter.write(System.lineSeparator());
        }
    }

    private String getPrimaryKeyComment(int size) {
        if (size == 0) return "";
        if (size == 1) return "主键";
        return "联合主键";
    }
}
