package com.ssymon.export.holiday.util;

import com.ssymon.export.holiday.entity.HolidayEntity;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: HolidayUtil
 *
 * @author symon
 * @since 15.01.2021
 */
@Slf4j
public class HolidayUtil {

    private HolidayUtil() {

    }



    private static String holidayStr = "一、元旦：2020年1月1日放假，共1天。\n" +
            "\n" +
            "二、春节：1月24日至30日放假调休，共7天。1月19日（星期日）、2月1日（星期六）上班。\n" +
            "\n" +
            "三、清明节：4月4日至6日放假调休，共3天。\n" +
            "\n" +
            "四、劳动节：5月1日至5日放假调休，共5天。4月26日（星期日）、5月9日（星期六）上班。\n" +
            "\n" +
            "五、端午节：6月25日至27日放假调休，共3天。6月28日（星期日）上班。\n" +
            "\n" +
            "六、国庆节、中秋节：10月1日至8日放假调休，共8天。9月27日（星期日）、10月10日（星期六）上班。";

    public static List<HolidayEntity> parseStr(String year, String holidayStr) {

        String str = holidayStr.trim();
        String[] split = str.split("\n");
        List<String> stringList = new ArrayList<>();
        for (String s : split) {
            if (!s.equals("")) stringList.add(s);
        }

        year += "年";
        List<HolidayEntity> holidayEntityList = new ArrayList<>();
        for (String s : stringList) {
            Matcher m = matcherOne(s);
            if (m.find()) {
                log.info("MATCH START:------------->" + s);

                Matcher mName = matcherName(m.group(1));
                if (mName.find()) {
                    String name = mName.group(2);

                    String group2 = m.group(2);
                    String date;
                    if (group2.contains("年")) {
                        date = group2.substring(group2.lastIndexOf("年") + 1);
                    } else
                        date = group2;
                    date = date.replace("放假", "");
                    String month = date.substring(0, date.indexOf("月"));
                    Date startDate, endDate;
                    if (date.contains("至")) {
                        String[] holidays = date.split("至");
                        startDate = DateUtil.stringToDate(year + holidays[0], DateUtil.SIMPLE_FMT_ZH);
                        endDate = DateUtil.stringToDate(year + month + "月" + holidays[1], DateUtil.SIMPLE_FMT_ZH);
                    } else {
                        startDate = endDate = DateUtil.stringToDate(year + date, DateUtil.SIMPLE_FMT_ZH);
                    }
                    List<Date> dateList = DateUtil.getBetweenDates(startDate, endDate);
                    for (Date holiday : dateList) {
                        HolidayEntity entity = new HolidayEntity();
                        entity.setName(name);
                        entity.setDate(DateUtil.dateToString(holiday, DateUtil.SIMPLE_YMD));
                        entity.setHoliday(true);
                        holidayEntityList.add(entity);
                    }

                    String group3 = m.group(3);
                    String s1 = group3.substring(group3.indexOf("。") + 1);
                    if (s1.trim().length() > 0) {
                        String s2 = s1.substring(0, s1.lastIndexOf("上班。"));
                        String[] leaveSplit = s2.split("、");
                        for (String s3 : leaveSplit) {
                            Matcher matcher = matcherLeave(s3);
                            if (matcher.find()) {
                                HolidayEntity holidayEntity = new HolidayEntity();
                                holidayEntity.setName(name + "调休");
                                holidayEntity.setHoliday(false);
                                holidayEntity.setDate(DateUtil.dateToString(DateUtil.stringToDate(year + matcher.group(), DateUtil.SIMPLE_FMT_ZH), DateUtil.SIMPLE_YMD));
                                holidayEntityList.add(holidayEntity);
                            }
                        }
                    }
                }
                log.info("MATCH END:------------->" + s);
            } else {
                log.info("NO MATCH:------------->" + s);
            }
        }
        return holidayEntityList;
    }


    public static void main(String[] args) {
        List<HolidayEntity> holidayEntityList = parseStr("2020", holidayStr);
        for (HolidayEntity holidayEntity : holidayEntityList) {
            System.out.println(holidayEntity);
        }
    }

    public static Matcher matcherOne(String s) {
        String pattern = "(\\D*：)(.*放假)(.*)";
        Pattern r = Pattern.compile(pattern);
        return r.matcher(s);
    }

    public static Matcher matcherName(String s) {
        String pattern = "(\\D*、)(.*)(：)";
        Pattern r = Pattern.compile(pattern);
        return r.matcher(s);
    }

    public static Matcher matcherLeave(String s) {
        String pattern = "\\d{1,2}月\\d{1,2}日";
        Pattern r = Pattern.compile(pattern);
        return r.matcher(s);
    }
}
