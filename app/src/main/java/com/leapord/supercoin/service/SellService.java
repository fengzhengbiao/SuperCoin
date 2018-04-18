package com.leapord.supercoin.service;

import android.util.Log;

import com.leapord.supercoin.app.OkCoin;
import com.leapord.supercoin.core.Analyzer;
import com.leapord.supercoin.core.TradeManager;
import com.leapord.supercoin.entity.http.UserInfo;
import com.leapord.supercoin.network.HttpUtil;
import com.leapord.supercoin.observer.CoinObserver;
import com.leapord.supercoin.util.TimeUtils;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Biao
 * @version V1.0
 * @data 2018/4/5
 * @email sialovevoice@gmail.com
 */
public class SellService extends TradeService {

    private static final String TAG = "CoinProcess";

    @Override
    protected void onDataRefresh(List<double[]> value, String symbol) {
        boolean isKDJNegative = calcCross(value) || TENDENCY < 0;
        if (isKDJNegative) {
            Log.i(TAG, ">>> -------- KDJ Negative sell coin--------- >>>");
            TradeManager.sellCoins(symbol);
            mDisposiable.dispose();
            stopSelf();
        } else {
            Log.i(TAG, "---------sell service, onDataRefresh: no operation at: " + TimeUtils.getCurrentTime() + "  -------");
            HttpUtil.createRequest()
                    .fetchUserInfo()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CoinObserver<UserInfo>() {
                        @Override
                        public void onNext(UserInfo value) {
                            double remainCoin = Double.parseDouble(value.getInfo().getFunds().getFree().get(Analyzer.getCoinName(symbol)));
                            if (remainCoin < OkCoin.MIN_COIN_AMOUNT) {
                                Log.e(TAG, "------ coin not enough ------- <<<");
                                stopSelf();
                            }
                        }
                    });
        }
    }
}
