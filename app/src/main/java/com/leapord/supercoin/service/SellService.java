package com.leapord.supercoin.service;

import android.util.Log;

import com.leapord.supercoin.app.Const;
import com.leapord.supercoin.app.OkCoin;
import com.leapord.supercoin.core.TradeManager;
import com.leapord.supercoin.util.SpUtils;
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
    private int times;

    @Override
    protected void onDataRefresh(List<double[]> value, String symbol) {
        boolean isKDJNegative = calcCross(value) || TENDENCY < 0;
        if (isKDJNegative) {
            Log.i(TAG, ">>> -------- KDJ Negative sell coin--------- >>>");
            TradeManager.sellCoins(symbol);
            mDisposiable.dispose();
            stopSelf();
        } else {
            if (System.currentTimeMillis() - SpUtils.getLong(Const.SELL_SERVICESTART_TIME, 0l) > OkCoin.ONE_PERIOD) {
                Log.i(TAG, "------ sell service, stop self :" + TimeUtils.getCurrentTime() + "  ------- ");
            } else if (++times == 5) {
                Log.i(TAG, "------ sell service, stop self :" + TimeUtils.getCurrentTime() + "  ------- ");
                stopSelf();
                times = 0;
            } else {
                Log.i(TAG, "---------sell service, onDataRefresh: no operation at: " + TimeUtils.getCurrentTime() + "  -------");

            }
        }
    }
}
