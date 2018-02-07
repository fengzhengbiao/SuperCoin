package com.leapord.supercoin.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.leapord.supercoin.app.Const;
import com.leapord.supercoin.app.OkCoin;
import com.leapord.supercoin.entity.http.LiveData;
import com.leapord.supercoin.network.HttpUtil;
import com.leapord.supercoin.observer.CoinObserver;
import com.leapord.supercoin.observer.KlineObserver;
import com.leapord.supercoin.util.SpUtils;
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
    private String AS_PERIOD = OkCoin.TimePeriod.FIFTEEN_MIN;
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

    }

    private void startLoop() {
        if (mDisposiable == null || mDisposiable.isDisposed()) {
            int period = 30;
            if (mTradeType == OkCoin.TradeType.P_PERIOD) {
                switch (PERIOD) {
                    case OkCoin.TimePeriod.THREE_MIN:
                        period = 1 * 60;
                        break;
                    case OkCoin.TimePeriod.FIVE_MIN:
                        period = 3 * 60;
                        break;
                    case OkCoin.TimePeriod.FIFTEEN_MIN:
                        period = 8 * 60;
                        break;
                    case OkCoin.TimePeriod.THITY_MIN:
                        period = 15 * 60;
                        break;
                    case OkCoin.TimePeriod.ONE_HOUR:
                        period = 30 * 60;
                        break;
                }
            }
            if (SYMBOLS.size() != 0) {
                Logger.d("Refresh： delay：" + 10 + "  period : " + period + " seconds  tradeType : " +
                        (SpUtils.getInt(Const.SELECTED_STRATEGY, 1) == 1 ? "T" : "Period") + "  symbol : " + SYMBOLS.get(0));
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
                                if (mTradeType == OkCoin.TradeType.T_THORT) {
                                    for (String symbol : SYMBOLS) {
                                        Observable.zip(HttpUtil.createRequest().fetchKline(symbol, PERIOD).subscribeOn(Schedulers.io()),
                                                HttpUtil.createRequest().fetchDepth(symbol).subscribeOn(Schedulers.io()),
                                                (lists, depth) -> new LiveData(lists, null, depth))
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(KlineObserver.getObserver(symbol, mTradeType));
                                    }
                                } else {
                                    for (String symbol : SYMBOLS) {
                                        Observable.zip(HttpUtil.createRequest().fetchKline(symbol, PERIOD).subscribeOn(Schedulers.io()),
                                                HttpUtil.createRequest().fetchKline(symbol, AS_PERIOD).subscribeOn(Schedulers.io()),
                                                HttpUtil.createRequest().fetchDepth(symbol).subscribeOn(Schedulers.io()),
                                                (lists, asData, depth) -> new LiveData(lists, asData, depth))
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(KlineObserver.getObserver(symbol, mTradeType));
                                    }
                                }
                            }
                        });
            } else {
                ToastUtis.showToast("请重新开启服务");
            }
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ToastUtis.showToast("服务已开启");
        if (intent != null) {
            processIntent(intent);
            if (mDisposiable != null && !mDisposiable.isDisposed()) {
                mDisposiable.dispose();
                mDisposiable = null;
            }
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
            }
            if (mTradeType == OkCoin.TradeType.P_PERIOD) {
                switch (PERIOD) {
                    case OkCoin.TimePeriod.THREE_MIN:
                        AS_PERIOD = OkCoin.TimePeriod.FIVE_MIN;
                        break;
                    case OkCoin.TimePeriod.FIVE_MIN:
                        AS_PERIOD = OkCoin.TimePeriod.FIFTEEN_MIN;
                        break;
                    case OkCoin.TimePeriod.FIFTEEN_MIN:
                        AS_PERIOD = OkCoin.TimePeriod.THITY_MIN;
                        break;
                    case OkCoin.TimePeriod.THITY_MIN:
                        AS_PERIOD = OkCoin.TimePeriod.ONE_HOUR;
                        break;
                    case OkCoin.TimePeriod.ONE_HOUR:
                        AS_PERIOD = OkCoin.TimePeriod.TWO_HOUR;
                        break;
                    case OkCoin.TimePeriod.TWO_HOUR:
                        AS_PERIOD = OkCoin.TimePeriod.FOUR_HOUR;
                        break;
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
