package com.leapord.supercoin.network;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author Biao
 * @date 2018/1/20
 * @description
 * @email fengzb0216@sina.com
 */

public abstract class CoinObserver<T> implements Observer<T> {

    @Override
    public void onSubscribe(Disposable d) {
        com.orhanobut.logger.Logger.d("CoinObserver :   onSubscribe");
    }



    @Override
    public void onError(Throwable e) {
        com.orhanobut.logger.Logger.d("CoinObserver :   onError");
        com.orhanobut.logger.Logger.d("CoinObserver :  " + e.toString());
    }

    @Override
    public void onComplete() {
        com.orhanobut.logger.Logger.d("CoinObserver :   onComplete");
    }
}
