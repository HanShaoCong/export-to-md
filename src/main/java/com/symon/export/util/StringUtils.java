package com.symon.export.util;

public class StringUtils {

    private StringUtils() {

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
