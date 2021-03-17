package com.ssymon.export.holiday;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.ssymon.export.holiday.constant.HolidayConstant;
import com.ssymon.export.holiday.entity.HolidayEntity;
import com.ssymon.export.holiday.util.DateUtil;
import org.apache.http.HttpStatus;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Description: 入口
 *
 * @author symon
 * @since 14.01.2021
 */
public class App {

    public static void main(String[] args) {
        System.out.println("### Hello World");
        System.out.println("### 启动...");
        Scanner scanner = new Scanner(System.in);
        String filePath = getFilePath(scanner);
        String year = getYear(scanner);
        File mdFile = new File(filePath, HolidayConstant.HOLIDAY_FIELD + "-" + year + ".md");
        File sqlFile = new File(filePath, HolidayConstant.HOLIDAY_FIELD + "-" + year + ".sql");
        String current = DateUtil.dateToString(new Date(), DateUtil.SIMPLE_FMT);
        try (FileWriter fileWriter = new FileWriter(mdFile, false);
             FileWriter sqlFileWriter = new FileWriter(sqlFile, false)) {
            fileWriter.write(String.format(HolidayConstant.TITLE, year));
            fileWriter.write(System.lineSeparator());

            fileWriter.write(HolidayConstant.COMMENT);
            fileWriter.write(System.lineSeparator());
            fileWriter.write(System.lineSeparator());

            fileWriter.write(HolidayConstant.TABLE_HEADER);
            fileWriter.write(System.lineSeparator());
            fileWriter.write(HolidayConstant.TABLE_SEPARATOR);
            fileWriter.write(System.lineSeparator());

            sqlFileWriter.write(String.format(HolidayConstant.SQL_TITLE, year));
            sqlFileWriter.write(System.lineSeparator());
            sqlFileWriter.write("-- TRUNCATE SQL");
            sqlFileWriter.write(System.lineSeparator());
            sqlFileWriter.write(HolidayConstant.TRUNCATE_SQL);
            sqlFileWriter.write(System.lineSeparator());
            sqlFileWriter.write("-- INSERT SQL");
            sqlFileWriter.write(System.lineSeparator());

            List<HolidayEntity> holidays = getHolidays(year);
            for (HolidayEntity holidayEntity : holidays) {
                boolean weekday = DateUtil.isWeekday(DateUtil.stringToDate(holidayEntity.getDate(), DateUtil.SIMPLE_YMD));
                fileWriter.write(String.format(HolidayConstant.TABLE_BODY, holidayEntity.getDate(), weekday, holidayEntity.getHoliday().toString(), holidayEntity.getName()));
                fileWriter.write(System.lineSeparator());
                int type = -1;
                if (Boolean.TRUE.equals(holidayEntity.getHoliday())) {
                    if (weekday) type = 0;
                } else {
                    if (!weekday) type = 1;
                }
                if (type != -1) {
                    sqlFileWriter.write(String.format(HolidayConstant.INSERT_SQL, holidayEntity.getDate(), type, 0, current));
                    sqlFileWriter.write(System.lineSeparator());
                }
            }
            System.out.println("### Markdown导出成功：" + mdFile.getAbsolutePath());
            System.out.println("### SQL导出成功：" + sqlFile.getAbsolutePath());
        } catch (UnirestException | IOException e) {
            System.out.println("### 请求异常：" + e.getMessage());
        }
        System.out.println("### 退出...");

    }

    private static List<HolidayEntity> getHolidays(String year) throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.get("http://timor.tech/api/holiday/year/" + year).asJson();
        if (response.getStatus() == HttpStatus.SC_OK) {
            JSONObject holidayObject = response.getBody().getObject().getJSONObject(HolidayConstant.HOLIDAY_FIELD);
            Map<String, HolidayEntity> map = JSON.parseObject(holidayObject.toString(), new TypeReference<Map<String, HolidayEntity>>() {
            });
            List<HolidayEntity> holidayList = new ArrayList<>(map.values());
            holidayList.sort(Comparator.comparing(HolidayEntity::getDate));
            return holidayList;
        } else {
            System.out.println("### 请求失败：" + response.getBody().toString());
            return null;
        }
    }

    private static String getFilePath(Scanner scanner) {
        System.out.print("### 请输入导出文件夹：");
        String filePath = "";
        boolean isDir = false;
        while (!isDir) {
            filePath = scanner.next();
            File file = new File(filePath);
            if (file.isDirectory()) {
                isDir = true;
            } else {
                System.out.print("### 文件夹不存在，请重新输入：");
            }
        }
        return filePath;
    }

    private static String getYear(Scanner scanner) {
        System.out.print("### 请输入年份（默认当前年份）：");
        String year = scanner.next();
        System.out.println("### 数据获取中，请稍等...");
        if (year != null && year.length() > 0) {
            return year;
        }
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        return String.valueOf(instance.get(Calendar.YEAR));
    }
}
