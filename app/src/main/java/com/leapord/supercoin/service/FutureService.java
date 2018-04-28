package com.leapord.supercoin.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.leapord.supercoin.app.OkCoin;
import com.leapord.supercoin.core.KlineCalculator;
import com.leapord.supercoin.network.HttpUtil;
import com.leapord.supercoin.observer.CoinObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/4/27
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/
public class FutureService extends Service {

    private ArrayList<String> symbols;
    private String period;
    private String kline;
    private Disposable disposable;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        proccessIntent(intent);
        return START_NOT_STICKY;
    }

    private void proccessIntent(Intent intent) {
        if (intent != null) {
            symbols = intent.getStringArrayListExtra("symbols");
            period = intent.getStringExtra("period");
            kline = intent.getStringExtra("kline");
            startLooper();
        } else {
            stopSelf();
        }
    }

    private void startLooper() {
        if (symbols != null && symbols.size() > 0) {
            for (String symbol : symbols) {
                Observable.interval(0, Integer.parseInt(period), TimeUnit.SECONDS)
                        .observeOn(Schedulers.io())
                        .subscribe(new CoinObserver<Long>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                super.onSubscribe(d);
                                if (disposable == null) {
                                    disposable = d;
                                } else {
                                    if (!disposable.isDisposed()) {
                                        disposable.dispose();
                                        disposable = d;
                                    }
                                }
                            }

                            @Override
                            public void onNext(Long value) {
                                HttpUtil.createRequest()
                                        .fetchFutureKline(symbol, kline, OkCoin.CONTRACT_TYPE.THIS_WEEK)
                                        .observeOn(Schedulers.io())
//                                        .subscribeOn(Schedulers.computation())
                                        .subscribe(new CoinObserver<List<double[]>>() {
                                            @Override
                                            public void onNext(List<double[]> value) {
                                                proccessKline(value);
                                            }
                                        });
                            }
                        });

            }
        }
    }


    /**
     * [
     * 1440308760000,	时间戳
     * 233.38,		开
     * 233.38,		高
     * 233.27,		低
     * 233.37,		收
     * 186,		交易量
     * 79.70234956		交易量转化BTC或LTC数量
     * ]
     *
     * @param value
     */
    private void proccessKline(List<double[]> value) {
        KlineCalculator calculator = new KlineCalculator(value);
        List<Double> K = calculator.computeK();
        List<Double> D = calculator.computeD();
        List<Double> J = calculator.computeJ();
        if (!isPrint) {
            double recentInterest = computeRecentInterest(value, K, D, J);
            isPrint = true;
        }
    }

    private boolean isPrint;
    private List<double[]> recentCrossPoint = new ArrayList<>();


    private double computeRecentInterest(List<double[]> value, List<Double> k, List<Double> d, List<Double> j) {
        int endIndex = k.size() - 1;
        for (int i = endIndex - 1; i > 0; i--) {
            double diff = Math.abs(k.get(i) - j.get(i));
            Log.i("vaule", "computeRecentInterest: " + String.format("%f", diff));
//            if (diff < 20) {
//                double preDiff = k.get(i + 1) - j.get(i + 1);
//                double nexDiff = k.get(i - 1) - j.get(i - 1);
//                if (preDiff * nexDiff < 0) {
//                    if (recentCrossPoint.size() == 11) {
//                        recentCrossPoint.remove(10);
//                    }
//                    recentCrossPoint.add(value.get(i));
//                    Log.i("vaule", "computeRecentInterest: " + k.get(i));
//                    if (recentCrossPoint.size() == 11) break;
//                }
//            }
        }
        double range = 0;

        return range;
    }

    public static void start(Context context, ArrayList<String> symbols,
                             String period, String kline, String zone) {
        Intent intent = new Intent(context, FutureService.class);
        intent.putStringArrayListExtra("symbols", symbols);
        intent.putExtra("period", period);
        intent.putExtra("kline", kline);
        context.startService(intent);
    }
}
