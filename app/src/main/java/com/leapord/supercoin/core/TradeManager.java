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

import java.util.List;

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

    public static int TRADE_MODE = 1;       //1:优先深度  2：优先K线


    public static void settMode(int tMode) {
        TRADE_MODE = tMode;
    }

    /**
     * 自动交易
     *
     * @param mSymbol
     * @param tendencyByDepth 买卖盘趋势
     * @param tendencyByKline 【计算趋势，起始趋势，结束趋势，总体趋势】
     * @param value           此刻数据
     */
    public static void autoTrade(String mSymbol, int tendencyByDepth, double[] tendencyByKline, LiveData value) {
        switch (TRADE_MODE) {
            case 1:
                Log.i(TAG, "autoTrade: low risk");
                autoTradeOne(mSymbol, tendencyByDepth, tendencyByKline, value);
                break;
            case 2:
                Log.i(TAG, "autoTrade: high risk");
                autoTradeTwo(mSymbol, tendencyByDepth, tendencyByKline, value);
                break;
        }

    }

    /**
     * z
     *
     * @param mSymbol
     * @param tendencyByDepth
     * @param tendencyByKline
     * @param value
     */
    private static void autoTradeTwo(String mSymbol, int tendencyByDepth, double[] tendencyByKline, LiveData value) {
        if (tendencyByKline[3] > 0) {
            if (tendencyByKline[1] < tendencyByKline[2]) {
                if (tendencyByDepth > 0) {
                    purchase(mSymbol, WAREHOUSE.FULL);
                } else {
                    purchase(mSymbol, WAREHOUSE.HALF);
                }
            } else {
                if (tendencyByDepth > 0) {
                    purchase(mSymbol, WAREHOUSE.FULL, Analyzer.getPriceFromDepth(value.getDepth()), 1);
                } else {
                    purchase(mSymbol, WAREHOUSE.HALF, Analyzer.getPriceFromDepth(value.getDepth()), 1);
                }
            }
        } else if (tendencyByKline[3] < 0) {
            if (tendencyByKline[1] > tendencyByKline[2]) {
                if (tendencyByDepth < 0) {
                    sellCoins(mSymbol, WAREHOUSE.FULL);
                } else {
                    sellCoins(mSymbol, WAREHOUSE.HALF);
                }
            } else {
                if (tendencyByDepth < 0) {
                    sellCoins(mSymbol, WAREHOUSE.FULL, Analyzer.getPriceFromDepth(value.getDepth()), 1);
                } else {
                    sellCoins(mSymbol, WAREHOUSE.HALF, Analyzer.getPriceFromDepth(value.getDepth()), 1);
                }
            }
        } else {
            Log.i(TAG, "autoTradeTwo: match no rules");
        }
    }

    /**
     * 自动交易  优先深度
     *
     * @param mSymbol
     * @param tendencyByDepth
     * @param tendencyByKline
     * @param value
     */
    private static void autoTradeOne(String mSymbol, int tendencyByDepth, double[] tendencyByKline, LiveData value) {
        //        买入
        if (tendencyByDepth == 1 || tendencyByDepth == 2) {     //卖家高价卖出较多  买家低价较少
            switch ((int) tendencyByKline[0]) {
                case 2:    //立即买
                    Log.i(TAG, "autoTrade: purchase " + mSymbol + "---" + System.currentTimeMillis());
                    purchase(mSymbol, WAREHOUSE.FULL, Analyzer.getPriceFromDepth(value.getDepth()), 1);
                    break;
                case 1:
                case -1:
                    if (tendencyByKline[3] < 0 && Analyzer.isContinuousIncrease(value.getKLineData(), 3)) {
                        // 下跌回转点
                        Log.i(TAG, "autoTrade: purchase " + mSymbol + "---" + System.currentTimeMillis());
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
                    Log.i(TAG, "autoTrade: sell " + mSymbol + "---" + System.currentTimeMillis());
                    sellCoins(mSymbol, WAREHOUSE.FULL, Analyzer.getPriceFromDepth(value.getDepth()), 1);
                    break;
                case 1:
                case -1:
                    if (tendencyByKline[3] > 0 && Analyzer.isContinuousDecrease(value.getKLineData(), 3)) {
                        // 上涨回转点
                        Log.i(TAG, "autoTrade: sell " + mSymbol + "---" + System.currentTimeMillis());
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
        } else {
            Log.i(TAG, "autoTrade:  " + mSymbol + " match no trade rules");
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
                    return remainCoin > 0.01;
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
                    if (coinAmount<1){
                        amount = (float) (coinAmount/price);
                    }
                    Log.e(TAG, "purchase: " + coin_type + "  amount:" + amount + "  price:" + price);
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
     * 市价买卖
     *
     * @param symbol
     * @param warehouse
     */
    public static void purchase(String symbol, WAREHOUSE warehouse) {
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
                    String coin_type = getCoinZone(symbol);
                    double coinAmount = Double.parseDouble(userInfo.getInfo().getFunds().getFree().get(coin_type));
                    float amount = 0;
                    switch (warehouse) {
                        case ONE_FOUR:
                            amount = (float) (coinAmount / 4);
                            break;
                        case HALF:
                            amount = (float) (coinAmount / 2);
                            break;
                        case THREE_FOUR:
                            amount = (float) (coinAmount * 3 / 4);
                        case FULL:
                            amount = (float) coinAmount;
                            break;

                    }
                    Log.e(TAG, "purchase: " + coin_type + "  amount:" + amount + "  price: 市价");
                    return HttpUtil.createRequest()
                            .purchaseMarket(amount,
                                    symbol,
                                    OkCoin.Trade.BUY_MARKET);

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
                    Log.e(TAG, "sell: " + coin_type + "  amount:" + amount + "  price:" + price);
                    return HttpUtil.createRequest()
                            .makeTrade(amount, price,
                                    symbol,
                                    OkCoin.Trade.SELL);

                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new TradeObserver(symbol, OkCoin.Trade.SELL));
    }


    public static void sellCoins(String symbol, WAREHOUSE warehouse) {
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

                    String coin_type = getCoinName(symbol);
                    double coinAmount = Double.parseDouble(userInfo.getInfo().getFunds().getFree().get(coin_type));
                    double amount = 0;
                    switch (warehouse) {
                        case FULL:
                            amount = coinAmount;
                            break;
                        case THREE_FOUR:
                            amount = coinAmount * 3 / 4;
                            break;
                        case HALF:
                            amount = coinAmount / 2;
                            break;
                        case ONE_FOUR:
                            amount = coinAmount / 4;
                            break;
                    }
                    Log.e(TAG, "sell: " + coin_type + "  amount:" + amount + "  price: 市价");
                    return HttpUtil.createRequest()
                            .sellMarket(amount,
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

    public static void autoTrade(String symbol, List<Double> dif, List<Double> dea, List<Double> macd) {
        int endIndex = macd.size() - 1;
        if (macd.get(macd.size() - 1) == 0) {
            Double aDouble = macd.get(endIndex - 1);
            if (aDouble > 0) {
                for (int i = endIndex - 1; i > endIndex - 4; i++) {
                    if (macd.get(i) < 0) {
                        continue;
                    }
                    if (dif.get(i) > 0 && dea.get(i) > 0) {
                        purchase(symbol, WAREHOUSE.FULL);
                        Log.i(TAG, "autoTrade: purchase full warehouse");
                    } else {
                        purchase(symbol, WAREHOUSE.HALF);
                        Log.i(TAG, "autoTrade: purchase half warehouse");
                    }
                }
            } else {
                for (int i = endIndex - 1; i > endIndex - 4; i++) {
                    if (macd.get(i) > 0) {
                        continue;
                    }
                    if (dif.get(i) < 0 && dea.get(i) < 0) {
                        sellCoins(symbol, WAREHOUSE.FULL);
                        Log.i(TAG, "autoTrade: sell full warehouse");
                    } else {
                        sellCoins(symbol, WAREHOUSE.HALF);
                        Log.i(TAG, "autoTrade: sell half warehouse");
                    }
                }
            }
        } else if (isNearZero(macd.get(endIndex))) {
            Double aDouble = macd.get(endIndex - 1);
            if (aDouble > 0) {
                for (int i = endIndex - 1; i > endIndex - 4; i++) {
                    if (macd.get(i) < 0) {
                        continue;
                    }
                    if (dif.get(i) > 0 && dea.get(i) > 0) {
                        purchase(symbol, WAREHOUSE.FULL);
                        Log.i(TAG, "autoTrade: purchase full warehouse");
                    } else {
                        purchase(symbol, WAREHOUSE.HALF);
                        Log.i(TAG, "autoTrade: purchase half warehouse");
                    }
                }
            } else {
                for (int i = endIndex - 1; i > endIndex - 5; i++) {
                    if (macd.get(i) > 0) {
                        continue;
                    }
                    if (dif.get(i) < 0 && dea.get(i) < 0) {
                        sellCoins(symbol, WAREHOUSE.FULL);
                        Log.i(TAG, "autoTrade: sell full warehouse");
                    } else {
                        sellCoins(symbol, WAREHOUSE.HALF);
                        Log.i(TAG, "autoTrade: sell half warehouse");
                    }
                }
            }
        } else {
            Log.i(TAG, "autoTrade: match no rules");
        }
    }

    public static boolean isNearZero(double k) {
        return Math.abs(k) < 8E-2;
    }

    public enum WAREHOUSE {
        ONE_FOUR, HALF, FULL, THREE_FOUR
    }
}
