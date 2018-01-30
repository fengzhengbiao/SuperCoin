package com.leapord.supercoin.observer;

import android.util.Log;

import com.leapord.supercoin.app.Const;
import com.leapord.supercoin.core.Analyzer;
import com.leapord.supercoin.core.TradeManager;
import com.leapord.supercoin.entity.http.KlineAnalyzeInfo;
import com.leapord.supercoin.entity.http.LiveData;
import com.leapord.supercoin.util.SpUtils;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Biao
 * @date 2018/1/20
 * @description K线处理
 * @email fengzb0216@sina.com
 */

public class KlineObserver extends CoinObserver<LiveData> {
    private String mSymbol;

    static Map<String, KlineObserver> observerMap = new HashMap<>();


    public static KlineObserver getObserver(String type) {
        KlineObserver klineObserver = observerMap.get(type);
        if (klineObserver == null) {
            klineObserver = new KlineObserver(type);
            observerMap.put(type, klineObserver);
        }
        return klineObserver;
    }

    public KlineObserver(String symbol) {
        mSymbol = symbol;
    }

    @Override
    public void onNext(LiveData value) {
        Logger.d("处理K线数据");
        int tendencyByDepth = Analyzer.getDepthTendency(value.getDepth());
        int increasePointCount = Analyzer.getIncreasePointCountByKline(value.getKLineData(), 5);
        double[] tendencyByKline = Analyzer.getTendencyByKline(value.getKLineData(), 7);
        if (SpUtils.getBoolean(Const.AUTO_TRANSACTION, false)) {
            TradeManager.autoTrade(mSymbol, tendencyByDepth, tendencyByKline, increasePointCount, value);
        } else {
            Log.i(TAG, "onNext: auto trade closed");
        }
//        long time = Analyzer.getPredicateTimeByNearPoint(value.getKLineData(), 5);
        long time = Analyzer.getAutoPredicateTime(value.getKLineData(), (int) tendencyByKline[3]);
        double[] priceFromDepth = Analyzer.getPriceFromDepth(value.getDepth());
        KlineAnalyzeInfo klineAnalyzeInfo = new KlineAnalyzeInfo();
        klineAnalyzeInfo.setCoinName(mSymbol);
        klineAnalyzeInfo.setTendency(tendencyByDepth);
        klineAnalyzeInfo.setSellPrice(priceFromDepth[0]);
        klineAnalyzeInfo.setBuyPrice(priceFromDepth[1]);
        klineAnalyzeInfo.setTime(time);
        EventBus.getDefault().post(klineAnalyzeInfo);
    }
}
