package com.xc0ffeelabs.taxicabdriver.utils;

public class Utils {
    public static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}