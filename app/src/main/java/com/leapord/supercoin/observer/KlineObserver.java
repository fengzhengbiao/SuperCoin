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

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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
        Observable.create((ObservableOnSubscribe<KlineAnalyzeInfo>) emitter -> {
            KlineAnalyzeInfo klineAnalyzeInfo = new KlineAnalyzeInfo();
            double[] priceFromDepth = Analyzer.getPriceFromDepth(value.getDepth());
            if (mTradeType == OkCoin.TradeType.T_THORT) {       //短线交易
                int tendencyByDepth = Analyzer.getDepthTendency(value.getDepth());
                double[] tendencyByKline = Analyzer.getTendencyByKline(value.getKLineData(), 7);
                boolean gentle = TradeManager.isFastChange(value.getKLineData());
                if (SpUtils.getBoolean(Const.AUTO_TRANSACTION, false)) {
                    Log.i(TAG, "onNext: auto trade opened,type is T");
                    TradeManager.autoTrade(mSymbol, tendencyByDepth, tendencyByKline, value);
                } else {
                    Log.i(TAG, "onNext: auto trade closed,type is T");
                }
                long timeByNearPoint = Analyzer.getPredicateTimeByNearPoint(value.getKLineData(), 5);
                klineAnalyzeInfo.setCoinName(mSymbol);
                klineAnalyzeInfo.setTendency(tendencyByDepth);
                klineAnalyzeInfo.setSellPrice(priceFromDepth[0]);
                klineAnalyzeInfo.setBuyPrice(priceFromDepth[1]);
                klineAnalyzeInfo.setTime(timeByNearPoint);
            } else {        //长线
                MACDProcessor.process(value.getKLineData());
                List<Double> dea = MACDProcessor.getDEA();
                List<Double> dif = MACDProcessor.getDIF();
                List<Double> macd = MACDProcessor.getMACD();
                MACDProcessor.process(value.getAsData());
                List<Double> asDea = MACDProcessor.getDEA();
                List<Double> asDif = MACDProcessor.getDIF();
                List<Double> asMacd = MACDProcessor.getMACD();
                klineAnalyzeInfo.setCoinName(mSymbol);
                klineAnalyzeInfo.setTendency(0);
                if (SpUtils.getBoolean(Const.AUTO_TRANSACTION, false)) {
                    Log.i(TAG, "onNext: auto trade opened,type is Period");
                    TradeManager.autoTrade(mSymbol, dif, dea, macd,asDif,asDea,asMacd);
                } else {
                    Log.i(TAG, "onNext: auto trade closed ,type is Period");
                }
                double[] tendencyByKline = Analyzer.getTendencyByKline(value.getKLineData(), 7);
                long time = Analyzer.getAutoPredicateTime(value.getKLineData(), (int) tendencyByKline[3]);
                klineAnalyzeInfo.setSellPrice(priceFromDepth[0]);
                klineAnalyzeInfo.setBuyPrice(priceFromDepth[1]);
                klineAnalyzeInfo.setTime(time);
            }
            emitter.onNext(klineAnalyzeInfo);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(info -> EventBus.getDefault().post(info),
                        throwable -> Log.i(TAG, "calc analyzer error: " + throwable.toString())
                );
    }
}
