package com.leapord.supercoin.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.leapord.supercoin.entity.LiveData;
import com.leapord.supercoin.entity.OkCoin;
import com.leapord.supercoin.network.HttpUtil;
import com.leapord.supercoin.observer.KlineObserver;
import com.leapord.supercoin.util.ToastUtis;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * @author Biao
 * @date 2018/1/19
 * @description
 * @email fengzb0216@sina.com
 */

public class LooperService extends Service {
    private final static int GRAY_SERVICE_ID = 1001;
    private String PERIOD = OkCoin.TimePeriod.THREE_MIN;
    private List<String> SYMBOLS = new ArrayList<>();

    public LooperService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Observable.interval(0, 10, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    for (String symbol : SYMBOLS) {
                        Logger.d("更新数据：" + symbol);
                        Observable.zip(HttpUtil.createRequest().fetchKline(symbol, PERIOD),
                                HttpUtil.createRequest().fetchDepth(symbol),
                                (lists, depth) -> new LiveData(lists, depth))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(KlineObserver.getObserver(symbol));
                    }
                });
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String period = intent.getStringExtra("PERIOD");
            ArrayList<String> symbols = intent.getStringArrayListExtra("SYMBOLS");
            if (!TextUtils.isEmpty(period)) {
                PERIOD = period;
            }
            if (symbols != null && symbols.size() > 0) {
                SYMBOLS.clear();
                SYMBOLS.addAll(symbols);
                Logger.d("轮询的币种:");
                Logger.d(symbols);
            } else {
                if (SYMBOLS.size() == 0) {
                    ToastUtis.showToast("您还没有选中任何币种");
                }
            }
        } else {
            Logger.d("intent=null");
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //API < 18 ，此方法能有效隐藏Notification上的图标
            startForeground(GRAY_SERVICE_ID, new Notification());
        } else {
            Intent innerIntent = new Intent(this, GrayInnerService.class);
            startService(innerIntent);
            startForeground(GRAY_SERVICE_ID, new Notification());
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 给 API >= 18 的平台上用的灰色保活手段
     */
    public static class GrayInnerService extends Service {

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(GRAY_SERVICE_ID, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

    }

}
