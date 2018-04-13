package com.leapord.supercoin.service;

import android.util.Log;

import com.leapord.supercoin.core.TradeManager;
import com.leapord.supercoin.util.TimeUtils;

import java.util.List;

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
            Log.i(TAG, "---------sell service, onDataRefresh: no operation at:" + TimeUtils.getCurrentTime() + "  -------");
        }
    }
}
