package com.leapord.supercoin.app;

import android.app.Application;
import android.content.Context;

import com.leapord.supercoin.util.SpUtils;
import com.leapord.supercoin.util.ToastUtis;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.BuildConfig;
import com.orhanobut.logger.Logger;

/**
 * @author Biao
 * @date 2018/1/19
 * @description
 * @email fengzb0216@sina.com
 */

public class SuperCoinApplication extends Application {

    public static Context CONTEXT;

    @Override
    public void onCreate() {
        super.onCreate();
        CONTEXT = this;
        Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return !BuildConfig.DEBUG;
            }
        });
        ToastUtis.init(this);
        SpUtils.init(this);
    }
}
