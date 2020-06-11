package com.example.mapapp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilityClass {
    private static Pattern pat;
    private static Matcher match;

    private static final String EMAIL_VALIDATION = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@" +
        "[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$";

    public static boolean validate(String emailAddress) {
        pat = Pattern.compile(EMAIL_VALIDATION);
        match = pat.matcher(emailAddress);
        return match.matches();
    }

    public static boolean isNotNull(String string) {
        return string != null && string.trim().length() > 0 ? true : false;
    }
}
