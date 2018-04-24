package com.leapord.supercoin.service;

import com.leapord.supercoin.app.OkCoin;
import com.leapord.supercoin.core.Analyzer;
import com.leapord.supercoin.core.TradeManager;
import com.leapord.supercoin.entity.http.UserInfo;
import com.leapord.supercoin.network.HttpUtil;
import com.leapord.supercoin.observer.CoinObserver;
import com.leapord.supercoin.util.LogUtil;
import com.leapord.supercoin.util.SpUtils;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Biao
 * @version V1.0
 * @data 2018/4/5
 * @email sialovevoice@gmail.com
 */
public class BuyService extends TradeService {

    private static final String TAG = "CoinProcess";

    @Override
    protected void onDataRefresh(List<double[]> value, String symbol) {
        boolean isKDJPosivive = calcCross(value) || TENDENCY > 0;
        if (isKDJPosivive) {
            LogUtil.d(TAG, "KDJ cross at end");
        } else {
            LogUtil.i(TAG, "KDJ not cross at end ");
        }
        long lastSellTime = SpUtils.getLong(symbol + OkCoin.Trade.BUY_MARKET, 0l);
        boolean range = System.currentTimeMillis() - lastSellTime < timeDiff;
        if (range) {
            LogUtil.i(TAG, "last buy time in range");
        } else {
            LogUtil.d(TAG, "last buy time out of range");
        }
        if ((!range) && isKDJPosivive) {
            LogUtil.d(TAG, " start purchase ");
            TradeManager.purchaseAuto(symbol);
            mDisposiable.dispose();
            stopSelf();
        } else {
            LogUtil.i(TAG, " no operation ");
            HttpUtil.createRequest()
                    .fetchUserInfo()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CoinObserver<UserInfo>() {
                        @Override
                        public void onNext(UserInfo value) {
                            double remainCoin = Double.parseDouble(value.getInfo().getFunds().getFree().get(Analyzer.getCoinZone(symbol)));
                            if (remainCoin < OkCoin.MIN_COIN_AMOUNT) {
                                LogUtil.e(TAG, " coin not enough ");
                                stopSelf();
                            }
                        }
                    });
        }

    }

}
