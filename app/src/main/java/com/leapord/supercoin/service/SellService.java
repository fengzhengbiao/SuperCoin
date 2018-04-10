package com.leapord.supercoin.service;

import android.util.Log;

import com.leapord.supercoin.core.TradeManager;

import java.util.List;

/**
 * @author Biao
 * @version V1.0
 * @data 2018/4/5
 * @email sialovevoice@gmail.com
 */
public class SellService extends TradeService {

    private static final String TAG = "SELL_SERVICE";

    @Override
    protected void onDataRefresh(List<double[]> value, String symbol) {
        boolean isKDJNegative = calcCross(value) || TENDENCY < 0;
        if (isKDJNegative) {
            Log.i(TAG, "KDJ Negative");
            TradeManager.sellCoins(symbol);
            mDisposiable.dispose();
            stopSelf();
        } else {
            Log.i(TAG, "onDataRefresh: no operation");
        }
    }
}
