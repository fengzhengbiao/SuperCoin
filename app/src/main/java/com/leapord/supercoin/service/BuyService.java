package com.leapord.supercoin.service;

import android.util.Log;

import com.leapord.supercoin.app.OkCoin;
import com.leapord.supercoin.core.TradeManager;
import com.leapord.supercoin.util.SpUtils;

import java.util.List;

/**
 * @author Biao
 * @version V1.0
 * @data 2018/4/5
 * @email sialovevoice@gmail.com
 */
public class BuyService extends TradeService {

    private static final String TAG = "BUY_SERVICE";

    @Override
    protected void onDataRefresh(List<double[]> value, String symbol) {
        boolean hasCross = calcCross(value);
        if (hasCross) {
            Log.i(TAG, "KDJ cross at end");
        }
        long lastSellTime = SpUtils.getLong(symbol + OkCoin.Trade.BUY_MARKET, 0l);
        boolean range = lastSellTime - System.currentTimeMillis() < timeDiff;
        if (range) {
            Log.i(TAG, "last sell time in range");
        }
        if (range && hasCross) {
            TradeManager.purchase(symbol);
            mDisposiable.dispose();
            stopSelf();
        }
    }
}
