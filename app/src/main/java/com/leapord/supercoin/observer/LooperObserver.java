package com.leapord.supercoin.observer;

import android.content.Intent;
import android.util.Log;

import com.leapord.supercoin.app.CoinApplication;
import com.leapord.supercoin.app.Const;
import com.leapord.supercoin.app.OkCoin;
import com.leapord.supercoin.core.Analyzer;
import com.leapord.supercoin.core.KlineCalculator;
import com.leapord.supercoin.entity.http.LiveData;
import com.leapord.supercoin.service.BuyService;
import com.leapord.supercoin.service.SellService;
import com.leapord.supercoin.util.SpUtils;
import com.leapord.supercoin.util.TimeUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/2/11
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/

public class LooperObserver extends CoinObserver<LiveData> {
    private static final String TAG = "CoinProcess";
    private long lastOptimalTime = 0;


    private static Map<String, LooperObserver> observerMap = new HashMap<>();
    private String symbol;

    private LooperObserver(String symbol) {
        this.symbol = symbol;
    }

    public static synchronized LooperObserver get(String type) {
        LooperObserver observer = observerMap.get(type);
        if (observer == null) {
            observer = new LooperObserver(type);
            observerMap.put(type, observer);
        }
        return observer;
    }


    @Override
    public void onNext(LiveData value) {
        KlineCalculator calculator = new KlineCalculator(value.getKLineData());
        List<Double> macds = calculator.computeMACDS();
        boolean crossZero = Analyzer.hasCrossZero(macds, 1);
        if (crossZero) {
            List<Double> rsi1 = calculator.computeRSI1();
            List<Double> rsi2 = calculator.computeRSI2();
            List<Double> rsi3 = calculator.computeRSI3();
            boolean isRSi1Mid = Analyzer.isMiddle(rsi1.get(rsi1.size() - 1));
            boolean isRSi2Mid = Analyzer.isMiddle(rsi2.get(rsi1.size() - 1));
            boolean isRSi3Mid = Analyzer.isMiddle(rsi3.get(rsi1.size() - 1));
            if (isRSi1Mid && isRSi2Mid && isRSi3Mid) {
                Double endMAcd = macds.get(macds.size() - 1);
                if (endMAcd == 0) {
                    if (macds.get(macds.size() - 2) < 0) {
                        startOptimalService(true);
                        Log.i(TAG, "<<<------ looper observer can buy,buy service start ------- <<<");
                    } else {
                        startOptimalService(false);
                        Log.i(TAG, ">>>------ looper observer can sell,sell service start------- >>>");
                    }
                } else if (endMAcd > 0) {
                    startOptimalService(true);
                    Log.i(TAG, "<<<------ looper observer can buy ------- <<<");
                } else {
                    startOptimalService(false);
                    Log.i(TAG, ">>>------ looper observer can sell ------- >>>");
                }
            } else {
                Log.i(TAG, "------ looper observer rsi out of range ris1:" + rsi1.get(rsi1.size() - 1) + "------- ");
            }
        } else {
            if (Analyzer.isContinuousDecrease(value.getKLineData(), 4)) {
                Log.i(TAG, "  <<<------ looper observer macd continue decrease at: " + TimeUtils.getCurrentTime() + "  -------<<< ");
                startOptimalService(false);
            }
            Log.i(TAG, "  ------ looper observer macd no cross at: " + TimeUtils.getCurrentTime() + "  ------- ");
        }
    }


    public void startOptimalService(boolean in) {
        Boolean autoTrasc = SpUtils.getBoolean(Const.AUTO_TRANSACTION, false);
        if (System.currentTimeMillis() - lastOptimalTime > OkCoin.ONE_PERIOD && autoTrasc) {
            lastOptimalTime = System.currentTimeMillis();
            Intent buyIntent = new Intent(CoinApplication.INSTANCE, BuyService.class);
            buyIntent.putExtra("symbol", symbol);
            Intent sellIntent = new Intent(CoinApplication.INSTANCE, SellService.class);
            sellIntent.putExtra("symbol", symbol);
            if (in) {
                SpUtils.putLong(Const.BUY_SERVICESTART_TIME, System.currentTimeMillis());
                Log.i(TAG, "<<<------ buy service start at： " + TimeUtils.getCurrentTime() + "  ------- <<<");
                CoinApplication.INSTANCE.startService(buyIntent);
                CoinApplication.INSTANCE.stopService(sellIntent);
            } else {
                Log.i(TAG, ">>>------ sell service start at： " + TimeUtils.getCurrentTime() + "  ------- >>>");
                SpUtils.putLong(Const.SELL_SERVICESTART_TIME, System.currentTimeMillis());
                CoinApplication.INSTANCE.startService(sellIntent);
                CoinApplication.INSTANCE.stopService(buyIntent);
            }
        }
    }

}
