package com.studentguide.utils;

import com.studentguide.ParentObj;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Prismetric on 9/4/2015.
 */
public class StringUtils {

    public static boolean isNotEmpty(CharSequence str) {
        if (str == null || str.length() == 0 || str.equals("null") || str.equals(" ") || str.equals("none") || str.equals("") || str.equals("(null)")) {
            return false;
        } else {
            return true;
        }
    }

    public static int findPatternRepeatNumber(String main_str, String match_string) {
        Pattern pattern = Pattern.compile(match_string); //Pattern string you want to be matched
        Matcher matcher = pattern.matcher(main_str);

        int count = 0;
        while (matcher.find())
            count++; //count any matched pattern

        return count;
    }

   /* public static boolean islogin() {
        return isNotEmpty(ParentObj.getInstance().preferences.getUserId());
    }*/

    public static boolean isNullOrBlank(String s)
    {
        return (s==null || s.trim().equals(""));
    }
}
