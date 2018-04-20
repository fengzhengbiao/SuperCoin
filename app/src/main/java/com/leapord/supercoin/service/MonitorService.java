package com.leapord.supercoin.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.leapord.supercoin.entity.http.Order;
import com.leapord.supercoin.network.HttpUtil;
import com.leapord.supercoin.observer.CoinObserver;
import com.leapord.supercoin.util.LogUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/4/20
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/
public class MonitorService extends Service {

    private Disposable mDisposable;
    private String symbol;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        symbol = intent.getStringExtra("symbol");
        if (!TextUtils.isEmpty(symbol)) {
            Observable.interval(30, 30, TimeUnit.SECONDS)
                    .observeOn(Schedulers.io())
                    .subscribe(new CoinObserver<Long>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            mDisposable = d;
                            super.onSubscribe(d);
                        }

                        @Override
                        public void onNext(Long value) {
                            HttpUtil.createRequest()
                                    .fetchOrderInfo("-1", symbol)
                                    .filter(orderData -> orderData.isResult())
                                    .filter(orderData -> orderData.getOrders().size()>0)
                                    .map(orderData -> orderData.getOrders().get(0))
                                    .groupBy(new Function<Order, Boolean>() {
                                        @Override
                                        public Boolean apply(Order order) throws Exception {
                                            return null;
                                        }
                                    });

                        }
                    });
        }
        return START_NOT_STICKY;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i("CoinProccess", "monitor service create");
    }


    @Override
    public void onDestroy() {
        LogUtil.i("CoinProccess", "monitor service destroy");
        super.onDestroy();
    }
}
