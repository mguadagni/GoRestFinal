package com.careerdevs.gorestfinal.utils;

public class BasicUtils {

    public static boolean isStrNaN (String strNum) {
        if (strNum == null) {
            return true;
        }
        try {
            Integer.parseInt(strNum);
        } catch (NumberFormatException e) {
            return true;
        }
        return false;
    }
}