package com.leapord.supercoin.network;

import com.leapord.supercoin.entity.http.BbTicker;
import com.leapord.supercoin.entity.http.CancelTradeResp;
import com.leapord.supercoin.entity.http.Depth;
import com.leapord.supercoin.entity.http.OrderData;
import com.leapord.supercoin.entity.http.Trade;
import com.leapord.supercoin.entity.http.TradeResponse;
import com.leapord.supercoin.entity.http.UserInfo;

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
    Observable<TradeResponse> makeTrade(@Field("amount") float amount,      //交易数量
                                        @Field("price") float price,        //限价单价格
                                        @Field("symbol") String symbol,
                                        @Field("type") String type          ////交易类型Okcoin.Trade
    );

    @POST("/api/v1/trade.do")
        // 用户下单     市价交易
    Observable<TradeResponse> makeTrade(@Field("amount") float amount,      //交易数量
                                        @Field("symbol") String symbol,
                                        @Field("type") String type          ////交易类型Okcoin.Trade
    );

    @POST("/api/v1/cancel_order.do")
        //取消订单
    Observable<CancelTradeResp> cancelTrade(@Field("symbol") String symbol,
                                            @Field("order_id") String orderId          ////交易类型Okcoin.Trade
    );

    @POST("/api/v1/order_info.do")
    Observable<OrderData> fetchOrderInfo(@Field("order_id") String order_id,
                                         @Field("symbol") String symbol
    );

}
