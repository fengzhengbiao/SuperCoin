package com.leapord.supercoin.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.leapord.supercoin.app.Const;
import com.leapord.supercoin.core.Analyzer;
import com.leapord.supercoin.core.KlineCalculator;
import com.leapord.supercoin.network.HttpUtil;
import com.leapord.supercoin.observer.CoinObserver;
import com.leapord.supercoin.util.SpUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Biao
 * @version V1.0
 * @data 2018/4/3
 * @email sialovevoice@gmail.com
 */
public abstract class TradeService extends Service {

    private static final String TAG = "Trade";
    public Disposable mDisposiable;
    private String symbol;
    private String ktime;
    public long timeDiff;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * 寻找最佳交易点
     */
    private void findBestTime() {
        Observable.interval(0, 40, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(new CoinObserver<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        mDisposiable = d;
                    }

                    @Override
                    public void onNext(Long value) {
                        HttpUtil.createRequest().fetchKline(symbol, ktime).subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io())
                                .subscribe(new CoinObserver<List<double[]>>() {
                                    @Override
                                    public void onNext(List<double[]> value) {
                                        onDataRefresh(value, symbol);
                                    }
                                });
                    }

                });
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    protected abstract void onDataRefresh(List<double[]> value, String symbol);

    /**
     * 计算交点
     *
     * @param value
     */
    public int TENDENCY = 0;

    public boolean calcCross(List<double[]> value) {
        KlineCalculator calculator = new KlineCalculator(value);
        List<Double> K = calculator.computeK();
        List<Double> D = calculator.computeD();
        List<Double> J = calculator.computeJ();
        TENDENCY = Analyzer.getTendency(K, D, J);
        return Analyzer.hasCrossAtEnd(K, D, J);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "开始寻找最佳买入点");
        proccessIntent(intent);
        return START_STICKY;
    }

    private void proccessIntent(Intent intent) {
        if (intent != null) {
            symbol = intent.getStringExtra("symbol");
            String ktimeStr = SpUtils.getString(Const.SELECTED_KTIME, "");
            Log.i(TAG, "onStartCommand: symbol:" + symbol + "  ktime:" + ktimeStr);
            if (!TextUtils.isEmpty(ktimeStr)) {
                ArrayList<String> ktimes = (ArrayList<String>) JSON.parseArray(ktimeStr, String.class);
                ktime = ktimes.get(0);
                switch (ktime) {
                    case "1min":
                        timeDiff = 60 * 1000;
                        break;
                    case "3min":
                        timeDiff = 3 * 60 * 1000;
                        break;
                    case "15min":
                        timeDiff = 15 * 60 * 1000;
                        break;
                    case "30min":
                        timeDiff = 30 * 60 * 1000;
                        break;
                    case "1hour":
                        timeDiff = 60 * 60 * 1000;
                        break;
                    case "2hour":
                        timeDiff = 2 * 60 * 60 * 1000;
                        break;
                    case "4hour":
                        timeDiff = 4 * 60 * 60 * 1000;
                        break;
                    case "6hour":
                        timeDiff = 6 * 60 * 60 * 1000;
                        break;
                    default:
                        timeDiff = 3 * 60 * 1000;
                        break;
                }
            }
            if (mDisposiable != null && !mDisposiable.isDisposed()) {
                mDisposiable.dispose();
            }
            findBestTime();

            Observable.timer(4 * timeDiff, TimeUnit.SECONDS, Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        Log.e(TAG, "------- unreach price，stopself ------");
                        stopSelf();
                    });

        } else {
            Log.i(TAG, "proccessIntent: intent = null");
        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "结束寻找买入点");
        if (mDisposiable != null && !mDisposiable.isDisposed()) {
            mDisposiable.dispose();
        }
        super.onDestroy();
    }
}
