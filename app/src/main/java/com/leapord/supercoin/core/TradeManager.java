package com.leapord.supercoin.core;

import android.text.TextUtils;
import android.util.Log;

import com.leapord.supercoin.app.CoinApplication;
import com.leapord.supercoin.entity.dao.Trade;
import com.leapord.supercoin.entity.dao.TradeDao;
import com.leapord.supercoin.entity.http.LiveData;
import com.leapord.supercoin.app.OkCoin;
import com.leapord.supercoin.entity.http.Order;
import com.leapord.supercoin.network.HttpUtil;
import com.leapord.supercoin.observer.TradeObserver;
import com.leapord.supercoin.util.ToastUtis;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
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
                    Log.e(TAG, "autoTrade: purchase " + mSymbol + "---" + System.currentTimeMillis());
                    purchase(mSymbol, WAREHOUSE.FULL, Analyzer.getPriceFromDepth(value.getDepth()), 1);
                    break;
                case 1:
                case -1:
                    if (tendencyByKline[3] < 0 && Analyzer.isContinuousIncrease(value.getKLineData(), 3)) {
                        // 下跌回转点
                        Log.e(TAG, "autoTrade: purchase " + mSymbol + "---" + System.currentTimeMillis());
                        purchase(mSymbol, WAREHOUSE.HALF, Analyzer.getPriceFromDepth(value.getDepth()), 2);
                    } else if (tendencyByKline[3] < 0) {
                        Log.i(TAG, "autoTrade:" + mSymbol + " match many pruchase rules");
                    } else if (Analyzer.isContinuousDecrease(value.getKLineData(), 3)) {
                        Log.i(TAG, "autoTrade: " + mSymbol + " match some sell rules");
                    } else {
                        Log.i(TAG, "autoTrade: " + mSymbol + " match no purchase rules");
                    }
                    break;
                default:
                    Log.i(TAG, "autoTrade: " + mSymbol + " match no purchase rules");
                    break;
            }
        } else if (tendencyByDepth == -1 || tendencyByDepth == -2) {    //卖出
            switch ((int) tendencyByKline[0]) {
                case -2:        //立即卖
                    Log.e(TAG, "autoTrade: sell " + mSymbol + "---" + System.currentTimeMillis());
                    sellCoins(mSymbol, WAREHOUSE.FULL, Analyzer.getPriceFromDepth(value.getDepth()), 1);
                    break;
                case 1:
                case -1:
                    if (tendencyByKline[3] > 0 && Analyzer.isContinuousDecrease(value.getKLineData(), 3)) {
                        // 上涨回转点
                        Log.e(TAG, "autoTrade: sell " + mSymbol + "---" + System.currentTimeMillis());
                        sellCoins(mSymbol, WAREHOUSE.HALF, Analyzer.getPriceFromDepth(value.getDepth()), 2);
                    } else if (tendencyByKline[3] > 0) {
                        Log.i(TAG, "autoTrade: " + mSymbol + " match many sell rules");
                    } else if (Analyzer.isContinuousDecrease(value.getKLineData(), 3)) {
                        Log.i(TAG, "autoTrade: " + mSymbol + " match some pruchase rules");
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
     * @param prices    买卖盘价格【卖一价，买一价】
     * @param priority  1:优先仓位  买入价格则是市买卖价格中间值  2：优先价格
     */
    public static void purchase(String symbol, WAREHOUSE warehouse, double[] prices, int priority) {
        HttpUtil.createRequest()
                .fetchUserInfo()
                .filter(userInfo -> userInfo.getResult() && userInfo.getInfo().getFunds() != null)     //获取个人信息成功
                .filter(userInfo -> {
                    String coin_type = getCoinZone(symbol);
                    double remainCoin = Double.parseDouble(userInfo.getInfo().getFunds().getFree().get(coin_type));
                    if (remainCoin < 0.001) {
                        ToastUtis.showToast("coin not enough：" + coin_type);
                        Log.i(TAG, "coin not enough：" + coin_type);
                    } else {
                        Log.i(TAG, "have many coins");
                    }
                    return remainCoin > 0.001;
                })    // 当前交易区数量不为0
                .flatMap(userInfo -> {
                    float amount = 0;
                    float price = 0;
                    String coin_type = getCoinZone(symbol);
                    double coinAmount = Double.parseDouble(userInfo.getInfo().getFunds().getFree().get(coin_type));
                    price = priority == 1 ? (float) ((prices[0] + prices[1]) / 2) : (float) (prices[0]);
                    switch (warehouse) {
                        case ONE_FOUR:
                            amount = (float) (coinAmount / (4 * price));
                            break;
                        case HALF:
                            amount = (float) (coinAmount / (2 * price));
                            break;
                        case THREE_FOUR:
                            amount = (float) (coinAmount * 3 / (4 * price));
                            break;
                        case FULL:
                            amount = (float) (coinAmount / price);
                            break;
                    }
                    Log.i(TAG, "purchase: " + coin_type + "  amount:" + amount + "  price:" + price);
                    return HttpUtil.createRequest()
                            .makeTrade(amount,
                                    price,
                                    symbol,
                                    OkCoin.Trade.BUY);

                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new TradeObserver(symbol, OkCoin.Trade.BUY));
    }


    /**
     * 买入
     *
     * @param warehouse 仓位
     * @param prices    买卖盘价格【卖一价，买一价】
     * @param priority  1:优先仓位  买入价格则是市买卖价格中间值  2：优先价格
     */
    public static void sellCoins(String symbol, WAREHOUSE warehouse, double[] prices, int priority) {
        HttpUtil.createRequest()
                .fetchUserInfo()
                .filter(userInfo -> userInfo.getResult() && userInfo.getInfo().getFunds() != null)     //获取个人信息成功
                .filter(userInfo -> {
                    String coin_type = getCoinName(symbol);
                    double remainFreeCoin = Double.parseDouble(userInfo.getInfo().getFunds().getFree().get(coin_type));
                    if (remainFreeCoin > 0.001) {
                        if (remainFreeCoin > 0.01) {
                            Log.i(TAG, symbol + "Freezed " + symbol + " coins still remain " + remainFreeCoin);
                        }
                    } else {
                        Log.i(TAG, "have no " + symbol + " coins Freezed");
                    }
                    return remainFreeCoin > 0.001;
                })
                .flatMap(userInfo -> {
                    float amount = 0;
                    float price = 0;
                    String coin_type = getCoinName(symbol);
                    double coinAmount = Double.parseDouble(userInfo.getInfo().getFunds().getFree().get(coin_type));
                    price = priority == 1 ? (float) ((prices[0] + prices[1]) / 2) : (float) (prices[1]);
                    switch (warehouse) {
                        case ONE_FOUR:
                            amount = (float) (coinAmount / (4 * price));
                            break;
                        case HALF:
                            amount = (float) (coinAmount / (2 * price));
                            break;
                        case THREE_FOUR:
                            amount = (float) (coinAmount * 3 / (4 * price));
                            break;
                        case FULL:
                            amount = (float) (coinAmount / price);
                            break;
                    }
                    amount = 100;
                    Log.i(TAG, "sell: " + coin_type + "  amount:" + amount + "  price:" + price);
                    return HttpUtil.createRequest()
                            .makeTrade(amount, price,
                                    symbol,
                                    OkCoin.Trade.SELL);

                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new TradeObserver(symbol, OkCoin.Trade.SELL));
    }

    /**
     * @param symbol
     * @param orderId 取消订单  针对单个订单
     */
    public static void cancelTrade(String symbol, String orderId) {
        Order order = new Order();
        order.setOrder_id(Long.parseLong(orderId));
        HttpUtil.createRequest()
                .fetchOrderInfo("-1", symbol)
                .filter(oderInfo -> oderInfo.getOrders().contains(order))     //获取个人信息成功
                .flatMap(orderData -> HttpUtil.createRequest().cancelTrade(symbol, orderId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(value -> {
                    TradeDao tradeDao = CoinApplication.INSTANCE.getDaoSession().getTradeDao();
                    Trade trade = new Trade();
                    trade.setSymbol(symbol);
                    trade.setOrderId(orderId);
                    trade.setSellType(OkCoin.Trade.CANCEL);
                    trade.setStatus(TextUtils.isEmpty(value.getError()));
                    tradeDao.save(trade);

                });
    }


    /**
     * 取消所有挂单
     */
    public static void cancelAllTrade(String symbol) {
        HttpUtil.createRequest()
                .fetchOrderInfo("-1", symbol)
                .flatMap(orderData -> Observable.fromIterable(orderData.getOrders()))
                .filter(orderData -> orderData.getStatus() == 0)     //获取个人信息成功
                .flatMap(orderData -> HttpUtil.createRequest().cancelTrade(symbol, String.valueOf(orderData.getOrder_id())))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cancelTradeResp -> {
                    String successOrders = cancelTradeResp.getSuccess();
                    TradeDao tradeDao = CoinApplication.INSTANCE.getDaoSession().getTradeDao();
                    if (successOrders.contains(",")) {
                        String[] split = successOrders.trim().split(",");
                        for (int i = 0; i < split.length; i++) {
                            Trade trade = new Trade();
                            trade.setSymbol(symbol);
                            trade.setOrderId(split[i]);
                            trade.setSellType(OkCoin.Trade.CANCEL);
                            trade.setStatus(true);
                            tradeDao.save(trade);
                        }
                    } else {
                        Trade trade = new Trade();
                        trade.setSymbol(symbol);
                        trade.setOrderId(successOrders);
                        trade.setSellType(OkCoin.Trade.CANCEL);
                        trade.setStatus(true);
                        tradeDao.save(trade);
                    }
                });
    }


    public static String getCoinName(String symbol) {
        return symbol.substring(0, symbol.indexOf('_'));
    }

    public static String getCoinZone(String symbol) {
        return symbol.substring(symbol.indexOf('_') + 1);
    }


    public enum WAREHOUSE {
        ONE_FOUR, HALF, FULL, THREE_FOUR
    }
}
