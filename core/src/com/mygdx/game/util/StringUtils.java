package com.mygdx.game.util;

public class StringUtils {

    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static boolean isEqualIgnoringCase(Object constant, String input) {
        return constant.toString().equalsIgnoreCase(input);
    }
}
