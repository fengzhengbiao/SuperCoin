package com.leapord.supercoin.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.leapord.supercoin.app.OkCoin;
import com.leapord.supercoin.entity.http.LiveData;
import com.leapord.supercoin.network.HttpUtil;
import com.leapord.supercoin.observer.CoinObserver;
import com.leapord.supercoin.observer.KlineObserver;
import com.leapord.supercoin.util.ToastUtis;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * @author Biao
 * @date 2018/1/19
 * @description
 * @email fengzb0216@sina.com
 */

public class LooperService extends Service {
    private final static int GRAY_SERVICE_ID = 1;
    private String PERIOD = OkCoin.TimePeriod.THREE_MIN;
    private List<String> SYMBOLS = new ArrayList<>();
    private Disposable mDisposiable;
    private int mTradeType = OkCoin.TradeType.T_THORT;

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
        Log.i("LooperService", "onCreate: 服务启动");
        ToastUtis.showToast("服务已开启");
        startLoop();
    }

    private void startLoop() {
        if (mDisposiable == null || mDisposiable.isDisposed()) {
            int period = mTradeType == OkCoin.TradeType.T_THORT ? 30 : 3 * 60;
            Observable.interval(10, period, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CoinObserver<Long>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            super.onSubscribe(d);
                            mDisposiable = d;
                        }

                        @Override
                        public void onNext(Long value) {
                            for (String symbol : SYMBOLS) {
                                Logger.d("更新数据：" + symbol);
                                Observable.zip(HttpUtil.createRequest().fetchKline(symbol, PERIOD).subscribeOn(Schedulers.io()),
                                        HttpUtil.createRequest().fetchDepth(symbol).subscribeOn(Schedulers.io()),
                                        (lists, depth) -> new LiveData(lists, depth))
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(KlineObserver.getObserver(symbol, mTradeType));
                            }
                        }
                    });
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        processIntent(intent);
        if (mDisposiable != null && !mDisposiable.isDisposed()) {
            mDisposiable.dispose();
            mDisposiable = null;
            startLoop();
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //API >18 ，此方法能有效隐藏Notification上的图标
            Intent innerIntent = new Intent(this, GrayInnerService.class);
            startService(innerIntent);
        }
        startForeground(GRAY_SERVICE_ID, new Notification());
        return START_STICKY;
    }

    private void processIntent(Intent intent) {
        if (intent != null) {
            String period = intent.getStringExtra("PERIOD");
            mTradeType = intent.getIntExtra("TRADE_TYPE", OkCoin.TradeType.T_THORT);
            ArrayList<String> symbols = intent.getStringArrayListExtra("SYMBOLS");
            if (!TextUtils.isEmpty(period)) {
                PERIOD = period;
                if (mTradeType == OkCoin.TradeType.T_THORT) {
                    PERIOD = OkCoin.TimePeriod.FIVE_MIN;
                }
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
    }

    @Override
    public void onDestroy() {
        if (!mDisposiable.isDisposed()) {
            mDisposiable.dispose();
            Log.i("LooperService", "onDestroy: dispose");
        }
        Log.i("LooperService", "onDestroy: 服务启动");
        ToastUtis.showToast("服务已关闭");
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
