package com.leapord.supercoin.observer;

import android.util.Log;

import com.leapord.supercoin.app.Const;
import com.leapord.supercoin.core.Analyzer;
import com.leapord.supercoin.core.TradeManager;
import com.leapord.supercoin.entity.LiveData;
import com.leapord.supercoin.util.SpUtils;
import com.leapord.supercoin.util.TimeUtils;
import com.orhanobut.logger.Logger;

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
        long time = Analyzer.getPredicateTimeByNearPoint(value.getKLineData(), 5, tendencyByDepth);
            TradeManager.autoTrade(mSymbol, tendencyByDepth, tendencyByKline, increasePointCount, value);
        if (SpUtils.getBoolean(Const.AUTO_TRANSACTION, false)) {
        } else {
            Log.i(TAG, "onNext: auto trade closed");
        }
        Logger.d("深度趋势：" + tendencyByDepth + "\n上升点个数：" + increasePointCount + "\nK线预测趋势：" + tendencyByKline[0] + "\n预测时间：" + TimeUtils.formatDate(time));

//        KlineAnalyzeInfo klineAnalyzeInfo = new KlineAnalyzeInfo();
//        EventBus.getDefault().post(klineAnalyzeInfo);
    }
}
