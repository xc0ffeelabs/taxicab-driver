package com.xc0ffeelabs.taxicabdriver.utils;

public class Utils {
    public static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static String firstLetterUppercase(String inp) {
        StringBuilder rackingSystemSb = new StringBuilder(inp.toLowerCase());
        rackingSystemSb.setCharAt(0, Character.toUpperCase(rackingSystemSb.charAt(0)));
        return rackingSystemSb.toString();
    }
}