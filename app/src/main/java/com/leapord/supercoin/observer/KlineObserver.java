package com.leapord.supercoin.observer;

import android.util.Log;

import com.leapord.supercoin.app.Const;
import com.leapord.supercoin.app.OkCoin;
import com.leapord.supercoin.core.Analyzer;
import com.leapord.supercoin.core.MACDProcessor;
import com.leapord.supercoin.core.TradeManager;
import com.leapord.supercoin.entity.http.KlineAnalyzeInfo;
import com.leapord.supercoin.entity.http.LiveData;
import com.leapord.supercoin.util.SpUtils;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.leapord.supercoin.R2.id.time;

/**
 * @author Biao
 * @date 2018/1/20
 * @description K线处理
 * @email fengzb0216@sina.com
 */

public class KlineObserver extends CoinObserver<LiveData> {
    private String mSymbol;
    private int mTradeType;

    static Map<String, KlineObserver> observerMap = new HashMap<>();


    public static KlineObserver getObserver(String type, int tradeType) {
        KlineObserver klineObserver = observerMap.get(tradeType + type);
        if (klineObserver == null) {
            klineObserver = new KlineObserver(type, tradeType);
            observerMap.put(tradeType + type, klineObserver);
        }
        return klineObserver;
    }

    public KlineObserver(String symbol, int tradeType) {
        mSymbol = symbol;
        mTradeType = tradeType;
    }

    @Override
    public void onNext(LiveData value) {
        Logger.d("处理K线数据");
        double[] priceFromDepth = Analyzer.getPriceFromDepth(value.getDepth());
        if (mTradeType == OkCoin.TradeType.T_THORT) {       //短线交易
            int tendencyByDepth = Analyzer.getDepthTendency(value.getDepth());
            double[] tendencyByKline = Analyzer.getTendencyByKline(value.getKLineData(), 7);
            if (SpUtils.getBoolean(Const.AUTO_TRANSACTION, false)) {
                TradeManager.autoTrade(mSymbol, tendencyByDepth, tendencyByKline, value);
            } else {
                Log.i(TAG, "onNext: auto trade closed");
            }
            long timeByNearPoint = Analyzer.getPredicateTimeByNearPoint(value.getKLineData(), 5);
            KlineAnalyzeInfo klineAnalyzeInfo = new KlineAnalyzeInfo();
            klineAnalyzeInfo.setCoinName(mSymbol);
            klineAnalyzeInfo.setTendency(tendencyByDepth);
            klineAnalyzeInfo.setSellPrice(priceFromDepth[0]);
            klineAnalyzeInfo.setBuyPrice(priceFromDepth[1]);
            klineAnalyzeInfo.setTime(timeByNearPoint);
            EventBus.getDefault().post(klineAnalyzeInfo);
        } else {        //长线
            MACDProcessor.process(value.getKLineData());
            List<Double> dea = MACDProcessor.getDEA();
            List<Double> dif = MACDProcessor.getDIF();
            List<Double> macd = MACDProcessor.getMACD();
            KlineAnalyzeInfo klineAnalyzeInfo = new KlineAnalyzeInfo();
            klineAnalyzeInfo.setCoinName(mSymbol);
            klineAnalyzeInfo.setTendency(0);
            if (SpUtils.getBoolean(Const.AUTO_TRANSACTION, false)) {
                TradeManager.autoTrade(mSymbol, dif, dea, macd);
            } else {
                Log.i(TAG, "onNext: auto trade closed");
            }
            double[] tendencyByKline = Analyzer.getTendencyByKline(value.getKLineData(), 7);
            long time = Analyzer.getAutoPredicateTime(value.getKLineData(), (int) tendencyByKline[3]);
            klineAnalyzeInfo.setSellPrice(priceFromDepth[0]);
            klineAnalyzeInfo.setBuyPrice(priceFromDepth[1]);
            klineAnalyzeInfo.setTime(time);
            EventBus.getDefault().post(klineAnalyzeInfo);
        }
    }
}
