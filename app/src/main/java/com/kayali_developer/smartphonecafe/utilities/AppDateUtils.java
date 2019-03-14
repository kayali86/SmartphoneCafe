package com.kayali_developer.smartphonecafe.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AppDateUtils {
    private static final String DE_DATE_FORMAT = "dd.MM.yyyy";
    private static final String DE_DATE_FORMAT_COMPLETE = "dd.MM.yyyy hh:mm";

    public static String longDateToDeFormat(long timeInMillis) {
        if (timeInMillis != -1){
            SimpleDateFormat fmtOut = new SimpleDateFormat(DE_DATE_FORMAT);
            return fmtOut.format(timeInMillis);
        }else {
            return null;
        }
    }

    public static String dateObjectToDeFormatComplete(Date date) {
        if (date != null){
            SimpleDateFormat fmtOut = new SimpleDateFormat(DE_DATE_FORMAT_COMPLETE);
            return fmtOut.format(date);
        }else {
            return null;
        }
    }
}
