package com.ssymon.export.holiday.constant;

public final class HolidayConstant {

    public static final String TITLE = "## 节假日年份 %s";
    public static final String COMMENT = "> 数据来源：http://timor.tech/api";
    public static final String TABLE_HEADER = "| 日期 | 是否周末 | 是否节假日 | 描述 |";
    public static final String TABLE_BODY = "| %s | %s | %s | %s |";
    public static final String TABLE_SEPARATOR = "| :--: | ---- | ---- | ---- | ";

    public static final String HOLIDAY_FIELD = "holiday";

    public static final String SQL_TITLE = "-- 节假日年份 %s";
    public static final String TRUNCATE_SQL = "TRUNCATE `special_date`;";
    public static final String INSERT_SQL = "INSERT INTO `special_date` (`date`, `type`, `create_user`, `create_time`) VALUES ('%s', %d, %d, '%s');";
}
