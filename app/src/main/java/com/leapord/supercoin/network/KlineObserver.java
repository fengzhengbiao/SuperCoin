package com.leapord.supercoin.network;

import com.leapord.supercoin.entity.KlineAnalyzeInfo;
import com.leapord.supercoin.entity.LiveData;
import com.leapord.supercoin.util.KlineUtil;
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
        int tendency = KlineUtil.getDepthTendency(value.getDepth());
//        long predicateTime = KlineUtil.getPredicateTime(value);
        KlineAnalyzeInfo klineAnalyzeInfo = new KlineAnalyzeInfo();
        Logger.d("更新界面数据");
        EventBus.getDefault().post(klineAnalyzeInfo);
    }
}
