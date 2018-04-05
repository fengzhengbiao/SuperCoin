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
        boolean hasCross = calcCross(value);
        if (hasCross) {
            Log.i(TAG, "KDJ cross at end");
            TradeManager.sellCoins(symbol);
            mDisposiable.dispose();
            stopSelf();
        }
    }
}
