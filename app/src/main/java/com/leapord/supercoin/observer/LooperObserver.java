package com.leapord.supercoin.observer;

import android.content.Intent;

import com.leapord.supercoin.app.CoinApplication;
import com.leapord.supercoin.app.Const;
import com.leapord.supercoin.app.OkCoin;
import com.leapord.supercoin.core.Analyzer;
import com.leapord.supercoin.core.KlineCalculator;
import com.leapord.supercoin.entity.http.current.LiveData;
import com.leapord.supercoin.service.BuyService;
import com.leapord.supercoin.service.SellService;
import com.leapord.supercoin.util.LogUtil;
import com.leapord.supercoin.util.SpUtils;

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

    private static Map<String, LooperObserver> observerMap = new HashMap<>();
    private String symbol;


    private static boolean RECENT_TENDENCY;

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
        KlineCalculator calculator = new KlineCalculator(value.getSubKlineData());
        List<Double> macds = calculator.computeMACDS();
        boolean crossZero = Analyzer.hasCrossZero(macds, CoinApplication.INSTANCE.getLastOptimalTime() == 0 ? 1 : 0);
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
                        LogUtil.i(TAG, " macd = 0 , increase");
                    } else {
                        startOptimalService(false);
                        LogUtil.i(TAG, "macd = 0 ,decrease");
                    }
                } else if (endMAcd > 0) {
                    startOptimalService(true);
                    LogUtil.i(TAG, "macd cross , increase");
                } else {
                    startOptimalService(false);
                    LogUtil.i(TAG, "macd cross , decrease");
                }
            } else {
                LogUtil.i(TAG, "rsi out of range ris1:" + rsi1.get(rsi1.size() - 1));
            }
        } else {
            LogUtil.i(TAG, "macd no cross");
            if (Analyzer.isMacdContinueDecrease(macds, 4)) {
                LogUtil.i(TAG, "macd continue decrease");
                if (Analyzer.isFastIncrease(value.getKLineData())) {
                    LogUtil.e(TAG, "sub kline fast increase");
//                    startOptimalService(true, true);
                } else {
                    LogUtil.i(TAG, "looper observer can sell");
//                    startOptimalService(false);
                }
            }
            if (Analyzer.isMacdContinueDecrease(macds, 4)) {
//                startOptimalService(false, true);
                LogUtil.e(TAG, "sub kline fast decrease");
            }
        }
    }


    public void startOptimalService(boolean in) {
        startOptimalService(in, false);
    }

    public void startOptimalService(boolean in, boolean ignoreTime) {
        Boolean autoTrasc = SpUtils.getBoolean(Const.AUTO_TRANSACTION, false);
        boolean isOutRangeTime = true;
        if (!ignoreTime) {
            isOutRangeTime = System.currentTimeMillis() - CoinApplication.INSTANCE.getLastOptimalTime() > OkCoin.ONE_PERIOD;
        }
        if (isOutRangeTime && autoTrasc) {
            Intent buyIntent = new Intent(CoinApplication.INSTANCE, BuyService.class);
            buyIntent.putExtra("symbol", symbol);
            Intent sellIntent = new Intent(CoinApplication.INSTANCE, SellService.class);
            sellIntent.putExtra("symbol", symbol);
            if (in) {
                SpUtils.putLong(Const.BUY_SERVICESTART_TIME, System.currentTimeMillis());
                LogUtil.d(TAG, "buy service star");
                CoinApplication.INSTANCE.startService(buyIntent);
                CoinApplication.INSTANCE.stopService(sellIntent);
            } else {
                LogUtil.d(TAG, "sell service start");
                SpUtils.putLong(Const.SELL_SERVICESTART_TIME, System.currentTimeMillis());
                CoinApplication.INSTANCE.startService(sellIntent);
                CoinApplication.INSTANCE.stopService(buyIntent);
            }
        }

        if (!ignoreTime)
            LogUtil.i(TAG, " optional in range :" + (System.currentTimeMillis() - CoinApplication.INSTANCE.getLastOptimalTime()));
    }

}
