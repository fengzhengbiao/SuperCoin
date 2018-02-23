package com.leapord.supercoin.core;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.leapord.supercoin.app.CoinApplication;
import com.leapord.supercoin.app.OkCoin;
import com.leapord.supercoin.entity.dao.Trade;
import com.leapord.supercoin.entity.dao.TradeDao;
import com.leapord.supercoin.entity.event.Analysis;
import com.leapord.supercoin.entity.event.OrderEvent;
import com.leapord.supercoin.entity.http.LiveData;
import com.leapord.supercoin.entity.http.Order;
import com.leapord.supercoin.entity.http.OrderTransform;
import com.leapord.supercoin.filter.Filter;
import com.leapord.supercoin.network.HttpUtil;
import com.leapord.supercoin.observer.TradeObserver;
import com.leapord.supercoin.util.SpUtils;
import com.leapord.supercoin.util.ToastUtis;

import java.util.ArrayList;
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
    private static final int STANDARD_DIFF_TIME = 10 * 60 * 1000;
    private static final int PERIOD_DIFF_TIME = 10 * 60 * 1000;
    private static final double MIN_COIN_AMOUNT = 0.01;

    private static List<Filter> mFilters;
    private static TradeManager sTradeManager = null;
    private  boolean isPurchaseSync;
    private  boolean isSellSync;


    private TradeManager() {
    }

    public static TradeManager getInstance() {
        if (sTradeManager == null) {
            synchronized (TradeManager.class) {
                sTradeManager = new TradeManager();
            }
        }
        return sTradeManager;
    }

    public TradeManager addFilter(Filter filter) {
        if (mFilters == null) {
            mFilters = new ArrayList<>();
        }
        mFilters.add(filter);
        return this;
    }

    public TradeManager removeFilter(Filter filter) {
        if (mFilters != null) {
            mFilters.remove(filter);
        }
        return this;
    }

    public TradeManager clearFilter() {
        if (mFilters != null) {
            mFilters.clear();
        }
        return this;
    }

    public Analysis process(LiveData data) {
        Analysis analysis = new Analysis();
        analysis.setOriginData(data);
        for (Filter filter : mFilters) {
            analysis = filter.intercept(analysis,isPurchaseSync,isSellSync);
        }
        return analysis;
    }


    public void setPurchaseSync(boolean purchaseSync) {
        isPurchaseSync = purchaseSync;
    }

    public void setSellSync(boolean sellSync) {
        isSellSync = sellSync;
    }

    /**
     * 是否是快速变化
     *
     * @param kLineData
     * @return
     */
    public static boolean isFastChange(List<double[]> kLineData) {
        int endIndex = kLineData.size() - 1;
        double price = 0;
        for (int i = (endIndex - 11); i < (endIndex - 7); i++) {
            price += kLineData.get(i)[4];
        }
        price = price / 4;
        double v = Math.abs(price - kLineData.get(endIndex)[4]) / kLineData.get(endIndex)[4];
        return v > 0.05;
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
                    if (remainCoin < MIN_COIN_AMOUNT) {
                        ToastUtis.showToast("coin not enough：" + coin_type);
                        Log.i(TAG, "coin not enough：" + coin_type);
                    } else {
                        Log.i(TAG, "have many coins");
                    }
                    long diffTime = System.currentTimeMillis() - SpUtils.getLong(symbol + OkCoin.Trade.BUY, 0l);
                    return remainCoin > 0.01 && diffTime > STANDARD_DIFF_TIME;
                })    // 当前交易区数量不为0
                .flatMap(userInfo -> {
                    String coin_type = getCoinZone(symbol);
                    double coinAmount = Double.parseDouble(userInfo.getInfo().getFunds().getFree().get(coin_type));
                    double price = calculatePrice(priority, 1, prices);
                    double amount = calculateAmount(warehouse, coinAmount, price);
                    Log.e(TAG, "purchase: " + coin_type + "  amount:" + amount + "  price:" + price);
                    return Observable.zip(Observable.just(new OrderEvent(amount, price)),
                            HttpUtil.createRequest().makeTrade(amount, price, symbol, OkCoin.Trade.BUY),
                            OrderTransform::new);

                })
                .map(oderTransform -> {
                            Trade trade = new Trade();
                            trade.setSymbol(symbol);
                            trade.setAmount(String.valueOf(oderTransform.getEvent().getAmount()));
                            trade.setPrice(String.valueOf(oderTransform.getEvent().getPrice()));
                            trade.setOrderId(oderTransform.getResponse().getOrder_id());
                            trade.setSellType(OkCoin.Trade.BUY);
                            trade.setStatus(oderTransform.getResponse().isResult());
                            return trade;
                        }
                )
                .subscribeOn(Schedulers.io())
                .subscribe(new TradeObserver());
    }

    private static double calculateAmount(WAREHOUSE warehouse, double coinAmount, double price) {
        double amount = 0;
        switch (warehouse) {
            case ONE_FOUR:
                amount = coinAmount / (4 * price);
                break;
            case HALF:
                amount = coinAmount / (2 * price);
                break;
            case THREE_FOUR:
                amount = coinAmount * 3 / (4 * price);
                break;
            case FULL:
                amount = coinAmount / price;
                break;
        }
        if (coinAmount < 1) {
            amount = coinAmount / price;
        }
        return amount;
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
                    if (remainCoin < MIN_COIN_AMOUNT) {
                        ToastUtis.showToast("coin not enough：" + coin_type);
                        Log.i(TAG, "coin not enough：" + coin_type);
                    } else {
                        Log.i(TAG, "have many coins");
                    }
                    long diffTime = System.currentTimeMillis() - SpUtils.getLong(symbol + OkCoin.Trade.BUY_MARKET, 0l);
                    return remainCoin > 0.01 && diffTime > PERIOD_DIFF_TIME;
                })    // 当前交易区数量不为0
                .flatMap(info -> {
                    String coin_type = getCoinZone(symbol);
                    double coinAmount = Double.parseDouble(info.getInfo().getFunds().getFree().get(coin_type));
                    double amount = calcFreeCoin(warehouse, coinAmount);
                    Log.e(TAG, "purchase: " + coin_type + "  amount:" + amount + "  price: 市价");
                    return Observable.zip(Observable.just(new OrderEvent(amount, 0.00)),
                            HttpUtil.createRequest().purchaseMarket(amount, symbol, OkCoin.Trade.BUY_MARKET), OrderTransform::new);
                })
                .map(oderTransform -> {
                    Trade trade = new Trade();
                    trade.setSymbol(symbol);
                    trade.setAmount(String.valueOf(oderTransform.getEvent().getAmount()));
                    trade.setPrice(String.valueOf(oderTransform.getEvent().getPrice()));
                    trade.setOrderId(oderTransform.getResponse().getOrder_id());
                    trade.setSellType(OkCoin.Trade.BUY_MARKET);
                    trade.setStatus(oderTransform.getResponse().isResult());
                    return trade;
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new TradeObserver());
    }

    private static double calcFreeCoin(WAREHOUSE warehouse, double coinAmount) {
        double amount = 0;
        switch (warehouse) {
            case ONE_FOUR:
                amount = coinAmount / 4;
                break;
            case HALF:
                amount = coinAmount / 2;
                break;
            case THREE_FOUR:
                amount = coinAmount * 3 / 4;
                break;
            case FULL:
                amount = coinAmount;
                break;

        }
        if (amount < 8) {
            amount = coinAmount;
        }
        return amount;
    }

    /**
     * @param priority  优先级
     * @param tradeType 1：买 2：卖
     * @param prices    买卖盘价格【卖一价，买一价】
     * @return
     */
    public static double calculatePrice(int priority, int tradeType, double[] prices) {
        double calcPrice;
        if (priority == 1) {
            int avgPrice = (int) (100000 * (prices[0] + prices[1]) / 2);
            if (avgPrice % 10 == 0) {
                calcPrice = avgPrice * 0.00001;
            } else {
                if (tradeType == 1) {
                    calcPrice = Math.floor(avgPrice * 0.1) * 0.0001;
                } else {
                    calcPrice = Math.ceil(avgPrice * 0.1) * 0.0001;
                }
            }
        } else {
            calcPrice = tradeType == 1 ? prices[0] : prices[1];
        }
        return calcPrice;
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
                    if (remainFreeCoin > MIN_COIN_AMOUNT) {
                        if (remainFreeCoin > MIN_COIN_AMOUNT) {
                            Log.i(TAG, symbol + "Freezed " + symbol + " coins still remain " + remainFreeCoin);
                        }
                    } else {
                        Log.i(TAG, "have no " + symbol + " coins Freezed");
                    }
                    long diffTime = System.currentTimeMillis() - SpUtils.getLong(symbol + OkCoin.Trade.SELL, 0l);
                    return remainFreeCoin > MIN_COIN_AMOUNT && diffTime > STANDARD_DIFF_TIME;
                })
                .flatMap(info -> {
                    String coin_type = getCoinName(symbol);
                    double coinAmount = Double.parseDouble(info.getInfo().getFunds().getFree().get(coin_type));
                    double price = calculatePrice(priority, 2, prices);
                    double amount = calculateAmount(warehouse, coinAmount, price);
                    Log.e(TAG, "sell: " + coin_type + "  amount:" + amount + "  price:" + price);
                    return Observable.zip(Observable.just(new OrderEvent(amount, price)), HttpUtil.createRequest()
                            .makeTrade(amount, price,
                                    symbol,
                                    OkCoin.Trade.SELL), OrderTransform::new);

                })
                .map(oderTransform -> {
                    Trade trade = new Trade();
                    trade.setSymbol(symbol);
                    trade.setAmount(String.valueOf(oderTransform.getEvent().getAmount()));
                    trade.setPrice(String.valueOf(oderTransform.getEvent().getPrice()));
                    trade.setOrderId(oderTransform.getResponse().getOrder_id());
                    trade.setSellType(OkCoin.Trade.BUY_MARKET);
                    trade.setStatus(oderTransform.getResponse().isResult());
                    return trade;
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new TradeObserver());
    }


    public static void sellCoins(String symbol, WAREHOUSE warehouse) {
        HttpUtil.createRequest()
                .fetchUserInfo()
                .filter(userInfo -> userInfo.getResult() && userInfo.getInfo().getFunds() != null)     //获取个人信息成功
                .filter(userInfo -> {
                    String coin_type = getCoinName(symbol);
                    double remainFreeCoin = Double.parseDouble(userInfo.getInfo().getFunds().getFree().get(coin_type));
                    if (remainFreeCoin > MIN_COIN_AMOUNT) {
                        if (remainFreeCoin > MIN_COIN_AMOUNT) {
                            Log.i(TAG, symbol + "Freezed " + symbol + " coins still remain " + remainFreeCoin);
                        }
                    } else {
                        Log.i(TAG, "have no " + symbol + " coins Freezed");
                    }
                    long diffTime = System.currentTimeMillis() - SpUtils.getLong(symbol + OkCoin.Trade.SELL, 0l);
                    return remainFreeCoin > MIN_COIN_AMOUNT && diffTime > PERIOD_DIFF_TIME;
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
                    if (coinAmount < 5) {
                        amount = coinAmount;
                    }
                    Log.e(TAG, "sell: " + coin_type + "  amount:" + amount + "  price: 市价");
                    return Observable.zip(Observable.just(new OrderEvent(amount, 0.00)), HttpUtil.createRequest()
                            .sellMarket(amount, symbol, OkCoin.Trade.SELL_MARKET), OrderTransform::new);

                })
                .map(oderTransform -> {
                    Trade trade = new Trade();
                    trade.setSymbol(symbol);
                    trade.setAmount(String.valueOf(oderTransform.getEvent().getAmount()));
                    trade.setPrice(String.valueOf(oderTransform.getEvent().getPrice()));
                    trade.setOrderId(oderTransform.getResponse().getOrder_id());
                    trade.setSellType(OkCoin.Trade.SELL_MARKET);
                    trade.setStatus(oderTransform.getResponse().isResult());
                    return trade;
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new TradeObserver());
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


    @NonNull
    public static String getCoinName(String symbol) {
        return symbol.substring(0, symbol.indexOf('_'));
    }

    @NonNull
    public static String getCoinZone(String symbol) {
        return symbol.substring(symbol.indexOf('_') + 1);
    }

    public static void autoTrade(String symbol, List<Double> macd, List<Double> asMacd) {
        Double endMacd = macd.get(macd.size() - 1);
        Double endAsMACD = asMacd.get(asMacd.size() - 1);
        if (endAsMACD > 0 && endMacd > 0) {
            purchase(symbol, WAREHOUSE.FULL);
        } else if (endMacd < 0 && endAsMACD < 0) {
            sellCoins(symbol, WAREHOUSE.FULL);
        } else {
            Log.i(TAG, "autoTrade: MACD match no rules");
        }

    }

    public static void autoTrade(String symbol, List<Double> dif, List<Double> dea, List<Double> macd) {
        int endIndex = macd.size() - 1;
        Double endMacd = macd.get(endIndex);

        int tendency = 0;
        for (int i = endIndex - 3; i < endIndex; i++) {
            tendency += (macd.get(i) > 0 ? 1 : -1);
        }
        if (isNearZero(endMacd)) {
            if (tendency == -3) {
                Log.i(TAG, "MACD: cross , previous is negative ");
                if (dif.get(endIndex - 3) > 0 && dea.get(endIndex - 3) > 0) {
                    Log.i(TAG, "MACD: cross , buy full");
                    purchase(symbol, WAREHOUSE.FULL);
                } else {
                    purchase(symbol, WAREHOUSE.HALF);
                    Log.i(TAG, "MACD: cross , buy half");
                }

            } else if (tendency == 3) {

                Log.i(TAG, "MACD: cross , previous is positive ,sell all ");
                sellCoins(symbol, WAREHOUSE.FULL);

            } else {
                Log.i(TAG, "MACD: no cross , previous is " + (tendency > 0 ? "positive " : "negative"));
            }

        } else if (endMacd > 0) {

            if (tendency < 1) {
                Log.i(TAG, "MACD: cross , buy full ");
                purchase(symbol, WAREHOUSE.FULL);
            } else if (tendency < 2) {
                Log.i(TAG, "MACD: cross , buy half ");
                purchase(symbol, WAREHOUSE.HALF);
            } else {
                Log.i(TAG, "MACD: no cross nearby, near is " + (tendency > 0 ? "positive " : "negative"));
            }

        } else if (endMacd < 0) {

            if (tendency < -1) {
                if (dif.get(endIndex - 3) > 0 && dea.get(endIndex - 3) > 0) {
                    Log.i(TAG, "MACD: cross , sell full");
                    sellCoins(symbol, WAREHOUSE.FULL);
                } else {
                    Log.i(TAG, "MACD: cross , sell half");
                    sellCoins(symbol, WAREHOUSE.HALF);
                }
            } else if (tendency < 2) {
                Log.i(TAG, "MACD: cross , sell half ");
                sellCoins(symbol, WAREHOUSE.HALF);
            } else {
                Log.i(TAG, "MACD: no cross nearby, near is " + (tendency > 0 ? "positive " : "negative"));
            }

        }
    }

    public static boolean isNearZero(double k) {
        return Math.abs(k) < 8E-10;
    }


    public void autoTrade(String symbol, LiveData value) {
        int tendency = Analyzer.getDepthTendency(value.getDepth());
        double[] depth = Analyzer.getPriceFromDepth(value.getDepth());
        int increasePointCountByKline = Analyzer.getIncreasePointCountByKline(value.getKLineData(), 10);
        double[] tendencyByKline = Analyzer.getTendencyByKline(value.getKLineData(), 7);
        boolean continuousIncrease = Analyzer.isContinuousIncrease(value.getKLineData(), 10);
        boolean continuousDecrease = Analyzer.isContinuousDecrease(value.getKLineData(), 10);

        KlineCalculator calculator = new KlineCalculator(value.getKLineData());
        List<Double> macds = calculator.computeMACDS();
        List<Double> deas = calculator.computeDEAS();

    }


    public enum WAREHOUSE {
        ONE_FOUR, HALF, FULL, THREE_FOUR
    }
}
