package com.leapord.supercoin.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.leapord.supercoin.R;
import com.leapord.supercoin.entity.http.LiveData;
import com.leapord.supercoin.network.HttpUtil;
import com.leapord.supercoin.observer.CoinObserver;
import com.leapord.supercoin.observer.LooperObserver;
import com.leapord.supercoin.util.ToastUtis;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * @author Biao
 * @date 2018/1/19
 * @description
 * @email fengzb0216@sina.com
 */

public class LooperService extends Service {
    private List<String> SYMBOLS = new ArrayList<>();
    private List<String> KTIMES = new ArrayList<>();
    private int PERIOD = 3;
    private Disposable mDisposiable;


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
            if (SYMBOLS.size() != 0) {
                Logger.d(SYMBOLS);
                Logger.d(KTIMES);
                Logger.d(PERIOD);
                Observable.interval(0, PERIOD , TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .subscribe(new CoinObserver<Long>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                super.onSubscribe(d);
                                mDisposiable = d;
                            }

                            @Override
                            public void onNext(Long value) {
                                for (String symbol : SYMBOLS) {
                                    Observable.zip(HttpUtil.createRequest().fetchKline(symbol, KTIMES.get(0)).subscribeOn(Schedulers.io()),
                                            HttpUtil.createRequest().fetchKline(symbol, KTIMES.get(1)).subscribeOn(Schedulers.io()),
                                            HttpUtil.createRequest().fetchDepth(symbol).subscribeOn(Schedulers.io()), LiveData::new)
                                            .observeOn(Schedulers.io())
                                            .subscribe(LooperObserver.get(symbol));
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

        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                //设置通知标题
                .setContentTitle("SuperCoin")
                //设置通知内容
                .setContentText("is running");
        startForeground(5, builder.build());
        return START_STICKY;
    }

    private void processIntent(Intent intent) {
        if (intent != null) {
            ArrayList<String> symbols = intent.getStringArrayListExtra("SYMBOLS");
            ArrayList<String> ktimes = intent.getStringArrayListExtra("KTIMES");
            String periodStr = intent.getStringExtra("PERIOD");
            if (symbols != null && symbols.size() > 0) {
                SYMBOLS.clear();
                SYMBOLS.addAll(symbols);
            }
            if (ktimes != null && ktimes.size() > 0) {
                KTIMES.clear();
                KTIMES.addAll(ktimes);
            }
            if (!TextUtils.isEmpty(periodStr)) {
                PERIOD = Integer.parseInt(periodStr);
            }
        } else {
            Logger.d("intent=null");
        }
    }

    @Override
    public void onDestroy() {
        if (mDisposiable != null && !mDisposiable.isDisposed()) {
            mDisposiable.dispose();
            Log.i("LooperService", "onDestroy: dispose");
        }
        Log.i("LooperService", "onDestroy: 服务启动");
        ToastUtis.showToast("服务已关闭");
        super.onDestroy();
    }


}
