package com.leapord.supercoin.service;

import android.util.Log;

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
public class BuyService extends TradeService {

    private static final String TAG = "CoinProcess";

    @Override
    protected void onDataRefresh(List<double[]> value, String symbol) {
        boolean isKDJPosivive = calcCross(value) || TENDENCY > 0;
        if (isKDJPosivive) {
            Log.i(TAG, "<<<------ KDJ cross at end ------- <<<");
        } else {
            Log.i(TAG, "<<<------ KDJ not cross at end ------- <<<");
        }
        long lastSellTime = SpUtils.getLong(symbol + OkCoin.Trade.BUY_MARKET, 0l);
        boolean range = System.currentTimeMillis() - lastSellTime < timeDiff;
        if (range) {
            Log.i(TAG, "<<<------ last buy time in range ------- <<<");
        } else {
            Log.i(TAG, "<<<------ last buy time out of range ------- <<<");
        }
        if ((!range) && isKDJPosivive) {
            Log.e(TAG, "------ start purchase ------- <<<");
            TradeManager.purchase(symbol);
            mDisposiable.dispose();
            stopSelf();
        } else {
            Log.i(TAG, "------ buy service, onDataRefresh: no operation at:" + TimeUtils.getCurrentTime() + "  ------- ");
        }

    }
}
