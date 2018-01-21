package com.leapord.supercoin.network;

import com.leapord.supercoin.entity.KlineAnalyzeInfo;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * @author Biao
 * @date 2018/1/20
 * @description K线处理
 * @email fengzb0216@sina.com
 */

public class KlineObserver extends CoinObserver<List<List<Float>>> {
    private String mSymbol;

    public KlineObserver(String symbol) {
        mSymbol = symbol;
    }

    @Override
    public void onNext(List<List<Float>> value) {
        Logger.d("处理K线数据");
//        int tendency = KlineUtil.getTendency(value);
//        long predicateTime = KlineUtil.getPredicateTime(value);
        KlineAnalyzeInfo klineAnalyzeInfo = new KlineAnalyzeInfo();
        Logger.d("更新界面数据");
        EventBus.getDefault().post(klineAnalyzeInfo);
    }
}
