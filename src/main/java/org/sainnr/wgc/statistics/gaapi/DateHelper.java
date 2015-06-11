package org.sainnr.wgc.statistics.gaapi;

import java.util.Calendar;

/**
 * @author sainnr
 * @since 07.05.14
 */
public class DateHelper {
    public final static long DAY = (long)1000 * 60 * 60 * 24;
    public final static long MONTH = DAY * 30;
    public final static long YEAR = MONTH * 12;

    public static String currentTimestamp(){
        return DateHelper.timestamp(System.currentTimeMillis());
    }

    public static String timestamp(long timestamp){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year + "-" + (month > 9 ? month : "0" + month) + "-" + (day > 9 ? day : "0" + day);
    }

}
