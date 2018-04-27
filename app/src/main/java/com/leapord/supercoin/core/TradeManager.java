package com.leapord.supercoin.core;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.leapord.supercoin.app.CoinApplication;
import com.leapord.supercoin.app.OkCoin;
import com.leapord.supercoin.entity.dao.Trade;
import com.leapord.supercoin.entity.dao.TradeDao;
import com.leapord.supercoin.entity.event.OrderEvent;
import com.leapord.supercoin.entity.event.TradeChangeEvent;
import com.leapord.supercoin.entity.http.OrderTransform;
import com.leapord.supercoin.entity.http.TradeResponse;
import com.leapord.supercoin.entity.http.current.CancelTradeResp;
import com.leapord.supercoin.entity.http.current.Order;
import com.leapord.supercoin.entity.http.current.UserWithDepth;
import com.leapord.supercoin.entity.http.future.HoldPosition;
import com.leapord.supercoin.entity.http.future.Holder;
import com.leapord.supercoin.entity.http.future.RightWithDepth;
import com.leapord.supercoin.network.HttpUtil;
import com.leapord.supercoin.observer.TradeObserver;
import com.leapord.supercoin.util.LogUtil;
import com.leapord.supercoin.util.SpUtils;
import com.leapord.supercoin.util.ToastUtis;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Observable;
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
                                LogUtil.e(TAG, "coin not enough：" + coin_type);
                            } else {
                                LogUtil.i(TAG, "have many coins");
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


    public static void purchaseAuto(String symbol) {
        Observable.zip(HttpUtil.createRequest().fetchUserInfo(),
                HttpUtil.createRequest().fetchDepth(symbol), UserWithDepth::new)
                .observeOn(Schedulers.io())
                .filter(userWithDepth ->
                        userWithDepth.getUserInfo().getResult()
                                && userWithDepth.getUserInfo().getInfo().getFunds() != null)
                .filter(userWithDepth -> {
                            String coinZone = Analyzer.getCoinZone(symbol);
                            double remainZoneCoin = Double.parseDouble(userWithDepth.getUserInfo()
                                    .getInfo().getFunds().getFree().get(coinZone));
                            String coinName = Analyzer.getCoinName(symbol);
                            double remainFreezedCoin = Double.parseDouble(userWithDepth.getUserInfo()
                                    .getInfo().getFunds().getFree().get(coinName));
                            boolean hasLegalCoin = remainZoneCoin > OkCoin.MIN_COIN_AMOUNT;
                            if (hasLegalCoin) {
                                LogUtil.i(TAG, "have many coins");
                            } else {
                                ToastUtis.showToast("coin not enough：" + coinZone);
                                LogUtil.e(TAG, "coin not enough：" + coinZone);
                            }
                            boolean hasFreezedOrder = remainFreezedCoin > OkCoin.MIN_COIN_AMOUNT;
                            if (hasFreezedOrder)
                                LogUtil.e(TAG, "have order freezed: " + remainFreezedCoin);
                            return hasLegalCoin || hasFreezedOrder;
                        }
                ).groupBy(userWithDepth -> {
            String coinName = Analyzer.getCoinName(symbol);
            double remainFreezedCoin = Double.parseDouble(userWithDepth.getUserInfo()
                    .getInfo().getFunds().getFree().get(coinName));
            return remainFreezedCoin > OkCoin.MIN_COIN_AMOUNT;
        }).subscribe(observable -> {

            observable.flatMap(userWithDepth -> {
                //获取法币类型
                String coin_type = Analyzer.getCoinZone(symbol);
                //获取法币数量
                double legaloinAmount = Double.parseDouble(userWithDepth.getUserInfo()
                        .getInfo().getFunds().getFree().get(coin_type));
//            double[] maxBid = Analyzer.getMaxBid(userWithDepth.getDepth());
                double[] maxBid = Analyzer.getBidAt(userWithDepth.getDepth(), 1);

                double canBuyCount = legaloinAmount / maxBid[0];
                double amount = Math.min(canBuyCount, maxBid[1]);
                return Observable.zip(Observable.just(new OrderEvent(amount, maxBid[0])), observable.getKey() ?
                                HttpUtil.createRequest()
                                        .fetchOrderInfo("-1", symbol)
                                        .flatMap(orderData -> Observable.fromIterable(orderData.getOrders()))
                                        .filter(orderData -> orderData.getStatus() == 0)
                                        .flatMap(orderData -> HttpUtil.createRequest().cancelTrade(symbol, String.valueOf(orderData.getOrder_id())))
                                        .flatMap((Function<CancelTradeResp, ObservableSource<TradeResponse>>) cancelTradeResp -> {
                                            if (!TextUtils.isEmpty(cancelTradeResp.getSuccess()))
                                                LogUtil.e(TAG, "------- cancle trade success ------- ");
                                            return HttpUtil.createRequest().makeTrade(amount, maxBid[0], symbol, OkCoin.Trade.BUY);
                                        })
                                : HttpUtil.createRequest().makeTrade(amount, maxBid[0], symbol, OkCoin.Trade.BUY),
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

        });


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
                        LogUtil.e(TAG, "hava no : " + Analyzer.getCoinName(symbol) + " remain");
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
            Trade trade = createTrade(symbol, oderTransform);
            return trade;
        }).subscribeOn(Schedulers.io())
                .subscribe(new TradeObserver());

    }


    /**
     * 卖出  能取消挂单并且重新下单
     */
    public static void sellCoinsAuto(String symbol) {
        Observable.zip(HttpUtil.createRequest().fetchUserInfo(),
                HttpUtil.createRequest().fetchDepth(symbol), UserWithDepth::new)
                .filter(userWithDepth -> userWithDepth.getUserInfo().getResult()
                        && userWithDepth.getUserInfo().getInfo().getFunds() != null)
                .groupBy(userWithDepth -> Double.parseDouble(userWithDepth.getUserInfo().getInfo().getFunds().getFreezed().get(Analyzer.getCoinName(symbol))) > 0)
                .subscribe(observable -> {
                    observable.flatMap(userWithDepth -> {
                        String coin_name = Analyzer.getCoinName(symbol);
                        double canSellAmount = Double.parseDouble(userWithDepth.getUserInfo()
                                .getInfo().getFunds().getFree().get(coin_name));
                        double[] minAsk = Analyzer.getAskAt(userWithDepth.getDepth(), observable.getKey() ? 0 : 1);
                        double amount = Math.min(canSellAmount, minAsk[1]);
                        return Observable.zip(Observable.just(new OrderEvent(amount, minAsk[0])), observable.getKey() ?
                                HttpUtil.createRequest()
                                        .fetchOrderInfo("-1", symbol)
                                        .flatMap(orderData -> Observable.fromIterable(orderData.getOrders()))
                                        .filter(orderData -> orderData.getStatus() == 0)
                                        .flatMap(orderData -> HttpUtil.createRequest().cancelTrade(symbol, String.valueOf(orderData.getOrder_id())))
                                        .flatMap((Function<CancelTradeResp, ObservableSource<TradeResponse>>) cancelTradeResp -> {
                                            if (!TextUtils.isEmpty(cancelTradeResp.getSuccess()))
                                                LogUtil.e(TAG, "------- cancle trade success ------- ");
                                            return HttpUtil.createRequest().makeTrade(amount, minAsk[0], symbol, OkCoin.Trade.SELL);
                                        })
                                : HttpUtil.createRequest().makeTrade(amount, minAsk[0], symbol, OkCoin.Trade.SELL), OrderTransform::new);
                    }).map(oderTransform -> {
                        Trade trade = createTrade(symbol, oderTransform);
                        return trade;
                    }).filter(trade -> Double.parseDouble(trade.getAmount()) > OkCoin.MIN_COIN_AMOUNT)
                            .subscribeOn(Schedulers.io())
                            .subscribe(new TradeObserver());

                });

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
                    if (!TextUtils.isEmpty(successOrders) && successOrders.contains(",")) {
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


    ////////////////////////////////////////合约方法////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 开仓
     *
     * @param symbol 币种
     * @param type   类型 int OPEN_INCREASE=1; //开多   int OPEN_DECREASE=2;/开空
     */
    public static void openTrade(String symbol, final String type) {
        Observable.zip(HttpUtil.createRequest().fetchFutureUserRights(),
                HttpUtil.createRequest().fetchFutureDepth(symbol, OkCoin.CONTRACT_TYPE.THIS_WEEK, 10), RightWithDepth::new)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.computation())
                .filter(rightWithDepth -> rightWithDepth.getRightInfo().getInfo()
                        .get(Analyzer.getCoinName(symbol)).getAccount_rights() > OkCoin.MIN_COIN_RIGHT)
                .flatMap((Function<RightWithDepth, ObservableSource<OrderTransform>>) rightWithDepth ->
                {
                    double accountRights = rightWithDepth.getRightInfo().getInfo().get(symbol).getAccount_rights();
                    double[] depth = TextUtils.equals("1", type) ?
                            Analyzer.getMinAsk(rightWithDepth.getDepth())
                            : Analyzer.getMaxBid(rightWithDepth.getDepth());
                    double amount = Math.min(accountRights, depth[1]);
                    return Observable.zip(Observable.just(new OrderEvent(amount, depth[0])),
                            HttpUtil.createRequest().makeFutureTrade(symbol, OkCoin.CONTRACT_TYPE.THIS_WEEK, "-2",
                                    String.format("%.3f", amount), type, "1"), OrderTransform::new);
                }).map(oderTransform -> {
            Trade trade = new Trade();
            trade.setSymbol(symbol);
            trade.setAmount(String.valueOf(oderTransform.getEvent().getAmount()));
            trade.setPrice(String.valueOf(oderTransform.getEvent().getPrice()));
            trade.setOrderId(oderTransform.getResponse().getOrder_id());
            trade.setSellType(OkCoin.Trade.BUY);
            trade.setStatus(oderTransform.getResponse().isResult());
            LogUtil.e("CoinProcess", "buy: amount:" + oderTransform.getEvent().getAmount() + " price:" + oderTransform.getEvent().getPrice());
            return trade;
        }).subscribeOn(Schedulers.io())
                .subscribe(new TradeObserver());
    }

    /**
     * @param symbol
     * @param type   int CLOSE_INCREASE=3;平多         int CLOSE_DECREASE=4;//平空
     */
    public static void closeTrade(String symbol, final String type) {
        HttpUtil.createRequest()
                .getHoldPosition(symbol, OkCoin.CONTRACT_TYPE.THIS_WEEK)
                .flatMap((Function<HoldPosition, ObservableSource<OrderTransform>>) holdPosition -> {
                    Holder holder = holdPosition.getHolding().get(0);
                    double amount = Math.max(holder.getBuy_available(), holder.getSell_available());
                    return Observable.zip(Observable.just(new OrderEvent(amount, 0.00)),
                            HttpUtil.createRequest().makeFutureTrade(symbol, OkCoin.CONTRACT_TYPE.THIS_WEEK, "-2",
                                    String.format("%.3f", amount), type, "1"), OrderTransform::new);
                }).map(oderTransform -> {
            Trade trade = createTrade(symbol, oderTransform);
            return trade;
        }).subscribeOn(Schedulers.io())
                .subscribe(new TradeObserver());

    }

    /**
     * @param symbol 市价平仓
     */
    public static void closeTrade(String symbol) {
        HttpUtil.createRequest()
                .getHoldPosition(symbol, OkCoin.CONTRACT_TYPE.THIS_WEEK)
                .filter(holdPosition -> holdPosition.getHolding().size() > 0)
                .flatMap((Function<HoldPosition, ObservableSource<OrderTransform>>) holdPosition -> {
                    Holder holder = holdPosition.getHolding().get(0);
                    double amount = Math.max(holder.getBuy_available(), holder.getSell_available());
                    String type = holder.getBuy_available() > 0 ? OkCoin.FUTURE_TYPE.CLOSE_INCREASE : OkCoin.FUTURE_TYPE.CLOSE_DECREASE;
                    return Observable.zip(Observable.just(new OrderEvent(amount, 0.00)),
                            HttpUtil.createRequest().makeFutureTrade(symbol, OkCoin.CONTRACT_TYPE.THIS_WEEK, "-2",
                                    String.format("%.3f", amount), type, "1"), OrderTransform::new);
                }).map(oderTransform -> createTrade(symbol, oderTransform)).subscribeOn(Schedulers.io())
                .subscribe(new TradeObserver());

    }


    public static void closeAndOpenNew(String symbol, String openType) {
        HttpUtil.createRequest()
                .getHoldPosition(symbol, OkCoin.CONTRACT_TYPE.THIS_WEEK)
                .filter(holdPosition -> holdPosition.getHolding().size() > 0)
                .flatMap((Function<HoldPosition, ObservableSource<OrderTransform>>) holdPosition -> {
                    Holder holder = holdPosition.getHolding().get(0);
                    double amount = Math.max(holder.getBuy_available(), holder.getSell_available());
                    String type = holder.getBuy_available() > 0 ? OkCoin.FUTURE_TYPE.CLOSE_INCREASE : OkCoin.FUTURE_TYPE.CLOSE_DECREASE;
                    return Observable.zip(Observable.just(new OrderEvent(amount, 0.00)),
                            HttpUtil.createRequest().makeFutureTrade(symbol, OkCoin.CONTRACT_TYPE.THIS_WEEK, "-2",
                                    String.format("%.3f", amount), type, "1"), OrderTransform::new);
                }).map(oderTransform -> createTrade(symbol, oderTransform))
                .doOnNext(trade -> {
                    CoinApplication.INSTANCE.setLastOptimalTime(System.currentTimeMillis());
                    LogUtil.e("CoinProcess", " >>>   ******  make one trade, type : " + trade.getSellType());
                    TradeDao tradeDao = CoinApplication.INSTANCE.getDaoSession().getTradeDao();
                    tradeDao.save(trade);
                    EventBus.getDefault().post(new TradeChangeEvent(trade.getSellType(), trade.getSymbol()));
                }).observeOn(Schedulers.io()).flatMap((Function<Trade, ObservableSource<RightWithDepth>>) trade ->
                Observable.zip(HttpUtil.createRequest().fetchFutureUserRights(),
                        HttpUtil.createRequest().fetchFutureDepth(symbol, OkCoin.CONTRACT_TYPE.THIS_WEEK, 10), RightWithDepth::new))
                .filter(rightWithDepth -> rightWithDepth.getRightInfo().getInfo()
                        .get(Analyzer.getCoinName(symbol)).getAccount_rights() > OkCoin.MIN_COIN_RIGHT)
                .flatMap((Function<RightWithDepth, ObservableSource<OrderTransform>>) rightWithDepth ->
                {
                    double accountRights = rightWithDepth.getRightInfo().getInfo().get(symbol).getAccount_rights();
                    double[] depth = TextUtils.equals("1", openType) ?
                            Analyzer.getMinAsk(rightWithDepth.getDepth())
                            : Analyzer.getMaxBid(rightWithDepth.getDepth());
                    double amount = Math.min(accountRights, depth[1]);
                    return Observable.zip(Observable.just(new OrderEvent(amount, depth[0])),
                            HttpUtil.createRequest().makeFutureTrade(symbol, OkCoin.CONTRACT_TYPE.THIS_WEEK, "-2",
                                    String.format("%.3f", amount), openType, "1"), OrderTransform::new);
                }).map(oderTransform -> createTrade(symbol, oderTransform))
                .subscribeOn(Schedulers.io())
                .subscribe(new TradeObserver());
    }

    @NonNull
    private static Trade createTrade(String symbol, OrderTransform oderTransform) {
        Trade trade = new Trade();
        trade.setSymbol(symbol);
        trade.setAmount(String.valueOf(oderTransform.getEvent().getAmount()));
        trade.setPrice(String.valueOf(oderTransform.getEvent().getPrice()));
        trade.setOrderId(oderTransform.getResponse().getOrder_id());
        trade.setSellType(OkCoin.Trade.SELL);
        trade.setStatus(oderTransform.getResponse().isResult());
        LogUtil.e("CoinProcess", "sell: amount:" + oderTransform.getEvent().getAmount() + " price:" + oderTransform.getEvent().getPrice());
        return trade;
    }


}
