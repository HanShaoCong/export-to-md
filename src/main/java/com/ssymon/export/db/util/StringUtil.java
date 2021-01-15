package com.ssymon.export.db.util;

public class StringUtil {

    private StringUtil() {

    }

    public static String replaceSep(String str) {
        return str.replace("\r\n", "")
                .replace("\n", "")
                .replace("\r", "");
    }

    public static String nullToBlank(String str) {
        return str == null ? "" : str;
    }

}
