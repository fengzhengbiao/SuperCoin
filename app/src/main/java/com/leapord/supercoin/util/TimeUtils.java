package com.leapord.supercoin.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/1/24
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/

public class TimeUtils {
    private final static SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");

    /**
     * 时间格式化
     * @param timeMil
     * @return
     */
    public static String formatDate(long timeMil) {
        Date date = new Date(timeMil);
        return timeMil == 0 ? "----" : sdf.format(date);
    }
}
