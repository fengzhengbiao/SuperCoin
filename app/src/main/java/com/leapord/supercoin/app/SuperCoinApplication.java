package com.leapord.supercoin.app;

import android.app.Application;
import android.content.Context;

import com.leapord.supercoin.util.SpUtils;
import com.leapord.supercoin.util.ToastUtis;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.BuildConfig;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

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
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(1)         // (Optional) How many method line to show. Default 2
                .methodOffset(1)        // (Optional) Hides internal method calls up to offset. Default 5
                .tag("SuperCoin")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return !BuildConfig.DEBUG;
            }
        });
        ToastUtis.init(this);
        SpUtils.init(this);
    }
}
