package com.leapord.supercoin.util;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * @author Biao
 * @date 2018/1/20
 * @description
 * @email fengzb0216@sina.com
 */

public class CommonUtil {

    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    public static String getTendency(int tendency) {
        switch (tendency) {
            case -2:
                return "高速下跌";
            case -1:
                return "慢速下跌";
            case 0:
                return "稳定";
            case 1:
                return "慢速上涨";
            case 2:
                return "高速上涨";
        }
        return null;
    }
}
