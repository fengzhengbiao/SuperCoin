package com.leapord.supercoin.util;

import android.util.Log;

import com.leapord.supercoin.entity.event.LogEvent;

import org.greenrobot.eventbus.EventBus;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/4/20
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/
public class LogUtil {

    public static void i(String tag, String msg) {
        log(0, tag, msg);
    }

    public static void d(String tag, String msg) {
        log(2, tag, msg);
    }

    public static void e(String tag, String msg) {
        log(3, tag, msg);
    }

    public static void log(int lev, String tag, String msg) {
        Log.i(tag, "--------   " + msg + "  ------ " + TimeUtils.getCurrentTime());
        LogEvent event = new LogEvent();
        event.level = lev;
        event.msg = msg;
        event.time = System.currentTimeMillis();
        EventBus.getDefault().post(event);
    }
}
