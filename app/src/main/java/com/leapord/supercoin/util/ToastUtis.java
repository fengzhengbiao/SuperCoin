package com.leapord.supercoin.util;

import android.content.Context;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by JokerFish on 2017/07/11.
 * 吐司工具类
 */

public class ToastUtis {
    private static Toast sToast;
    private static Context sContext;

    public static void init(Context context){
        sContext = context.getApplicationContext();
    }


    public static void showToast( String msg) {
        if (sToast == null) {
            sToast = Toast.makeText(sContext, msg, Toast.LENGTH_SHORT);
            sToast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            sToast.setText(msg);
        }
        sToast.show();
    }

    public static void showToast( @StringRes int resId) {
        if (sToast == null) {
            sToast = Toast.makeText(sContext, sContext.getResources().getString(resId), Toast.LENGTH_SHORT);
            sToast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            sToast.setText(sContext.getResources().getString(resId));
        }
        sToast.show();
    }

    public static void cancleToast() {
        if (sToast != null) {
            sToast.cancel();
            sToast = null;
        }
    }
}
