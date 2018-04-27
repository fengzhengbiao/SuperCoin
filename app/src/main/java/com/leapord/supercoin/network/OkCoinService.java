package com.leapord.supercoin.network;

import com.leapord.supercoin.entity.http.Depth;
import com.leapord.supercoin.entity.http.current.BbTicker;
import com.leapord.supercoin.entity.http.current.CancelTradeResp;
import com.leapord.supercoin.entity.http.current.OrderData;
import com.leapord.supercoin.entity.http.current.Trade;
import com.leapord.supercoin.entity.http.TradeResponse;
import com.leapord.supercoin.entity.http.current.UserInfo;
import com.leapord.supercoin.entity.http.future.HoldAmount;
import com.leapord.supercoin.entity.http.future.HoldPosition;
import com.leapord.supercoin.entity.http.future.RightInfo;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @author Biao
 * @date 2018/1/19
 * @description OKCoin 的数据接口
 * @email fengzb0216@sina.com
 */

public interface OkCoinService {

    //    获取OKEx最新币币行情数据
    @GET("/api/v1/ticker.do")
    Observable<BbTicker> fetchBbDetail(@Query("symbol") String symbol);

    @GET("/api/v1/depth.do")
    Observable<Depth> fetchDepth(@Query("symbol") String symbol);

    @GET("/api/v1/trades.do")
    Observable<List<Trade>> fetchTrades(@Query("symbol") String symbol, @Query("since") String since);

    @GET("/api/v1/kline.do")
    Observable<List<double[]>> fetchKline(@Query("symbol") String symbol, @Query("type") String type);

    //    用于OKEx快速进行币币交易
    @POST("/api/v1/userinfo.do")
    Observable<UserInfo> fetchUserInfo();

    @FormUrlEncoded
    @POST("/api/v1/trade.do")
        // 用户下单
    Observable<TradeResponse> makeTrade(@Field("amount") double amount,      //交易数量
                                        @Field("price") double price,        //限价单价格
                                        @Field("symbol") String symbol,
                                        @Field("type") String type          ////交易类型Okcoin.Trade
    );

    @FormUrlEncoded
    @POST("/api/v1/trade.do")
        // 用户下单     市价买入  price传价格
    Observable<TradeResponse> purchaseMarket(@Field("price") double price,      //交易金额（花多少钱买）
                                             @Field("symbol") String symbol,
                                             @Field("type") String type          ////交易类型Okcoin.Trade
    );

    @FormUrlEncoded
    @POST("/api/v1/trade.do")
        // 用户下单     市价卖出  amount传价格
    Observable<TradeResponse> sellMarket(@Field("amount") double amount,      //卖出数量
                                         @Field("symbol") String symbol,
                                         @Field("type") String type          ////交易类型Okcoin.Trade
    );

    @FormUrlEncoded
    @POST("/api/v1/cancel_order.do")
        //取消订单
    Observable<CancelTradeResp> cancelTrade(@Field("symbol") String symbol,
                                            @Field("order_id") String orderId          ////交易类型Okcoin.Trade
    );

    @FormUrlEncoded
    @POST("/api/v1/order_info.do")
    Observable<OrderData> fetchOrderInfo(@Field("order_id") String order_id,
                                         @Field("symbol") String symbol
    );

    ////////////////////////合约////////////////////////////////////////////////////////////////////////////////////////

    @GET("/api/v1/future_ticker.do")
        //合约行情数据
    Observable<BbTicker> fetchFuDetail(@Query("symbol") String symbol, @Query("contract_type") String contractType);


    @GET("/api/v1/future_depth.do")
        //合约深度信息
    Observable<Depth> fetchFutureDepth(@Query("symbol") String symbol,
                                       @Query("contract_type") String contractType,
                                       @Query("size") int size);


    @GET("/api/v1/future_kline.do")
    Observable<List<double[]>> fetchFutureKline(@Query("symbol") String symbol,
                                                @Query("type") String kline,
                                                @Query("contract_type") String contract_type);

    @POST("/api/v1/future_userinfo.do")
        //用户合约权益
    Observable<RightInfo> fetchFutureUserRights();

    @FormUrlEncoded
    @POST("/api/v1/future_trade.do")
        //下单
    Observable<TradeResponse> makeFutureTrade(@Field("symbol") String symbol,
                                              @Field("contract_type") String contract_type,
                                              @Field("price") String price,
                                              @Field("amount") String amount,
                                              @Field("type") String type,
                                              @Field("match_price") String match_price);


    /**
     * 获取合约持仓数量
     *
     * @param symbol
     * @param contractType
     * @return
     */
    @GET("/api/v1/future_hold_amount.do")
    Observable<List<HoldAmount>> getHoldAmount(@Query("symbol") String symbol,
                                               @Query("contract_type") String contractType);

    @FormUrlEncoded
    @POST("/api/v1/future_position.do")
    Observable<HoldPosition> getHoldPosition(@Query("symbol") String symbol,
                                             @Query("contract_type") String contractType);

}
