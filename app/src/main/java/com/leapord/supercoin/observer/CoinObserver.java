package com.leapord.supercoin.observer;

import android.util.Log;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author Biao
 * @date 2018/1/20
 * @description
 * @email fengzb0216@sina.com
 */

public abstract class CoinObserver<T> implements Observer<T> {
    public static final String TAG = "CoinObserver";

    @Override
    public void onSubscribe(Disposable d) {
        Log.i(TAG, "onSubscribe");
    }


    @Override
    public void onError(Throwable e) {
        Log.e(TAG, "onError: " + e.toString());
    }

    @Override
    public void onComplete() {
        Log.i(TAG, "onComplete");
    }
}
