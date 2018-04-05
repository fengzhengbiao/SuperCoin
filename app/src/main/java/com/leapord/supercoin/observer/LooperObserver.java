package com.leapord.supercoin.observer;

import android.util.Log;

import com.leapord.supercoin.core.Analyzer;
import com.leapord.supercoin.core.KlineCalculator;
import com.leapord.supercoin.entity.http.LiveData;

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
    private static final String TAG = "LooperObserver";

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
                        Log.i(TAG, "onNext: 寻找最佳买入点");
                    } else {
                        Log.i(TAG, "onNext: 寻找最佳卖出点");
                    }
                } else if (endMAcd > 0) {
                    Log.i(TAG, "onNext: 寻找最佳买入点");
                } else {
                    Log.i(TAG, "onNext: 寻找最佳卖出点");
                }
            } else {
                Log.i(TAG, "onNext:  RSI 不在交易范围内");
            }
        } else {
            Log.i(TAG, "onNext: MACD 没有交叉点，不合适交易");
        }
    }


}
