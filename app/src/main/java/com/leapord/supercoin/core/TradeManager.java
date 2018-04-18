package com.leapord.supercoin.core;

import android.text.TextUtils;
import android.util.Log;

import com.leapord.supercoin.app.CoinApplication;
import com.leapord.supercoin.app.OkCoin;
import com.leapord.supercoin.entity.dao.Trade;
import com.leapord.supercoin.entity.dao.TradeDao;
import com.leapord.supercoin.entity.event.OrderEvent;
import com.leapord.supercoin.entity.http.Order;
import com.leapord.supercoin.entity.http.OrderTransform;
import com.leapord.supercoin.entity.http.UserWithDepth;
import com.leapord.supercoin.network.HttpUtil;
import com.leapord.supercoin.observer.TradeObserver;
import com.leapord.supercoin.util.SpUtils;
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
    private static final int STANDARD_DIFF_TIME = 10 * 60 * 1000;


    public static void purchase(String symbol) {
        Observable.zip(HttpUtil.createRequest().fetchUserInfo(),
                HttpUtil.createRequest().fetchDepth(symbol), UserWithDepth::new)
                .observeOn(Schedulers.io())
                .filter(userWithDepth ->
                        userWithDepth.getUserInfo().getResult()
                                && userWithDepth.getUserInfo().getInfo().getFunds() != null)
                .filter(userWithDepth -> {
                            String coin_type = Analyzer.getCoinZone(symbol);
                            double remainCoin = Double.parseDouble(userWithDepth.getUserInfo()
                                    .getInfo().getFunds().getFree().get(coin_type));
                            if (remainCoin < OkCoin.MIN_COIN_AMOUNT) {
                                ToastUtis.showToast("coin not enough：" + coin_type);
                                Log.i(TAG, "coin not enough：" + coin_type);
                            } else {
                                Log.i(TAG, "have many coins");
                            }
                            return remainCoin > OkCoin.MIN_COIN_AMOUNT;
                        }
                ).flatMap(userWithDepth -> {
            //获取法币类型
            String coin_type = Analyzer.getCoinZone(symbol);
            //获取法币数量
            double legaloinAmount = Double.parseDouble(userWithDepth.getUserInfo()
                    .getInfo().getFunds().getFree().get(coin_type));
//            double[] maxBid = Analyzer.getMaxBid(userWithDepth.getDepth());
            double[] maxBid = Analyzer.getBidAt(userWithDepth.getDepth(), 1);

            double canBuyCount = legaloinAmount / maxBid[0];
            double amount = Math.min(canBuyCount, maxBid[1]);
            return Observable.zip(Observable.just(new OrderEvent(amount, maxBid[0])),
                    HttpUtil.createRequest().makeTrade(amount, maxBid[0], symbol, OkCoin.Trade.BUY),
                    OrderTransform::new);
        }).map(oderTransform -> {
                    Trade trade = new Trade();
                    trade.setSymbol(symbol);
                    SpUtils.putLong(symbol + OkCoin.Trade.BUY_MARKET, System.currentTimeMillis());
                    Log.e("CoinProcess", "purchase: amount:" + oderTransform.getEvent().getAmount() + " price:" + oderTransform.getEvent().getPrice());
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


    /**
     * 卖出
     */
    public static void sellCoins(String symbol) {
        Observable.zip(HttpUtil.createRequest().fetchUserInfo(),
                HttpUtil.createRequest().fetchDepth(symbol), UserWithDepth::new)
                .filter(userWithDepth -> userWithDepth.getUserInfo().getResult()        //防止获取用户信息失败
                        && userWithDepth.getUserInfo().getInfo().getFunds() != null)
                .filter(userWithDepth -> {
                    boolean hasRemain = Double.parseDouble(userWithDepth.getUserInfo()     //确保存在该币种
                            .getInfo().getFunds().getFree().get(Analyzer.getCoinName(symbol))) > OkCoin.MIN_COIN_AMOUNT;
                    if (!hasRemain) {
                        Log.e(TAG, "hava no : " + Analyzer.getCoinName(symbol) + " remain");
                    }
                    return hasRemain;
                })
                .flatMap(userWithDepth -> {
                    String coin_name = Analyzer.getCoinName(symbol);
                    double canSellAmount = Double.parseDouble(userWithDepth.getUserInfo()
                            .getInfo().getFunds().getFree().get(coin_name));
//                    double[] minAsk = Analyzer.getMinAsk(userWithDepth.getDepth());
                    double[] minAsk = Analyzer.getAskAt(userWithDepth.getDepth(), 1);
                    double amount = Math.min(canSellAmount, minAsk[1]);
                    return Observable.zip(Observable.just(new OrderEvent(amount, minAsk[0])), HttpUtil.createRequest()
                            .makeTrade(amount, minAsk[0], symbol, OkCoin.Trade.SELL), OrderTransform::new);
                }).map(oderTransform -> {
            Trade trade = new Trade();
            trade.setSymbol(symbol);
            trade.setAmount(String.valueOf(oderTransform.getEvent().getAmount()));
            trade.setPrice(String.valueOf(oderTransform.getEvent().getPrice()));
            trade.setOrderId(oderTransform.getResponse().getOrder_id());
            trade.setSellType(OkCoin.Trade.SELL);
            trade.setStatus(oderTransform.getResponse().isResult());
            Log.e("CoinProcess", "sell: amount:" + oderTransform.getEvent().getAmount() + " price:" + oderTransform.getEvent().getPrice());
            return trade;
        }).subscribeOn(Schedulers.io())
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


    public static boolean isNearZero(double k) {
        return Math.abs(k) < 8E-10;
    }


}
