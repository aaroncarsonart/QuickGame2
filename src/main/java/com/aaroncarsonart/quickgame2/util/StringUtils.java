package com.aaroncarsonart.quickgame2.util;

public class StringUtils {
    public static String capitalize(String str) {
        return str.substring(0, 1) + str.substring(1, str.length()).toLowerCase();
    }
}
