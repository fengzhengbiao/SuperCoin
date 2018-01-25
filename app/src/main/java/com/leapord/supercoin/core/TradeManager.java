package com.leapord.supercoin.core;

import android.text.TextUtils;
import android.util.Log;

import com.leapord.supercoin.entity.LiveData;
import com.leapord.supercoin.entity.OkCoin;
import com.leapord.supercoin.entity.TradeResponse;
import com.leapord.supercoin.entity.UserInfo;
import com.leapord.supercoin.network.HttpUtil;
import com.leapord.supercoin.util.ToastUtis;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/1/24
 *  Description
 *  Email fengzhengbiao@vcard100.com
 **********************************************/

public class TradeManager {
    private static final String TAG = "TradeManager";

    /**
     * 自动交易
     *
     * @param mSymbol
     * @param tendencyByDepth    买卖盘趋势
     * @param tendencyByKline    【计算趋势，起始趋势，结束趋势，总体趋势】
     * @param increasePointCount 7点增长线
     * @param value              此刻数据
     */
    public static void autoTrade(String mSymbol, int tendencyByDepth, double[] tendencyByKline, int increasePointCount, LiveData value) {
        //        买入
        if (tendencyByDepth == 1 || tendencyByDepth == 2) {     //卖家高价卖出较多  买家低价较少
            switch ((int) tendencyByKline[0]) {
                case 2:    //立即买

                    break;
                case 1:
                case -1:
                    if (tendencyByKline[3] < 0 && Analyzer.isContinuousIncrease(value.getKLineData(), 3)) {
                        // 下跌回转点
                    } else if (tendencyByKline[3] < 0) {
                        Log.i(TAG, "autoTrade:" + mSymbol + " match many pruchase rules");
                    } else if (Analyzer.isContinuousDecrease(value.getKLineData(), 3)) {
                        Log.i(TAG, "autoTrade: " + mSymbol + " match some sell rules");
                    } else {
                        Log.i(TAG, "autoTrade: " + mSymbol + "match no purchase rules");
                    }
                    break;
                default:
                    Log.i(TAG, "autoTrade: " + mSymbol + " match no purchase rules");
                    break;
            }
        } else if (tendencyByDepth == -1 || tendencyByDepth == -2) {    //卖出
            switch ((int) tendencyByKline[0]) {
                case -2:        //立即卖
                    break;
                case 1:
                case -1:
                    if (tendencyByKline[3] > 0 && Analyzer.isContinuousDecrease(value.getKLineData(), 3)) {
                        // 上涨回转点
                    } else if (tendencyByKline[3] > 0) {
                        Log.i(TAG, "autoTrade: " + mSymbol + " match many sell rules");
                    } else if (Analyzer.isContinuousDecrease(value.getKLineData(), 3)) {
                        Log.i(TAG, "autoTrade: " + mSymbol + "match some pruchase rules");
                    } else {
                        Log.i(TAG, "autoTrade: " + mSymbol + " match no sell rules");
                    }
                    break;
                default:
                    Log.i(TAG, "autoTrade:  " + mSymbol + " match no purchase rules");
                    break;
            }
        }
    }


    /**
     * 买入
     *
     * @param warehouse 仓位
     * @param prices
     */
    public static void purchase(String symbol, WAREHOUSE warehouse, double[] prices) {
        HttpUtil.createRequest()
                .fetchUserInfo(OkCoin.API.API_KEY)
                .filter(userInfo -> userInfo.getResult() && userInfo.getInfo().getFunds() != null)     //获取个人信息成功
                .filter(userInfo -> {
                    String coin_type = symbol.substring(symbol.indexOf("_"));
                    boolean hasMoreCoin = !TextUtils.equals(userInfo.getInfo().getFunds().getFree().get(coin_type), "0");
                    if (!hasMoreCoin) {
                        ToastUtis.showToast("coin not enough：" + coin_type);
                    }
                    return hasMoreCoin;
                })    // 当前交易区数量不为0
                .flatMap((Function<UserInfo, ObservableSource<TradeResponse>>) userInfo -> {
                    switch (warehouse) {
                        case ONE_FOUR:
                        case HALF:
                        case THREE_FOUR:
                        case FULL:
                    }
                    return null;

                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }


//    userInfo -> HttpUtil.createRequest()
//            .makeTrade(OkCoin.API.API_KEY,
//                                100f, 1.2f,
//                       OkCoin.USDT.SWFTC,
//                       OkCoin.Trade.BUY)

    enum WAREHOUSE {
        ONE_FOUR, HALF, FULL, THREE_FOUR
    }
}
