package com.leapord.supercoin.network;

import com.leapord.supercoin.entity.BbTicker;
import com.leapord.supercoin.entity.CancelTradeResp;
import com.leapord.supercoin.entity.Depth;
import com.leapord.supercoin.entity.Trade;
import com.leapord.supercoin.entity.TradeResponse;
import com.leapord.supercoin.entity.UserInfo;

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
    @FormUrlEncoded
    @POST("/api/v1/userinfo.do")
    Observable<UserInfo> fetchUserInfo(@Field("api_key") String apiKey);

    @POST("/api/v1/trade.do")
        // 用户下单
    Observable<TradeResponse> makeTrade(@Field("api_key") String apiKey,
                                        @Field("amount") float amount,      //交易数量
                                        @Field("price") float price,        //限价单价格
                                        @Field("symbol") String symbol,
                                        @Field("type") String type          ////交易类型Okcoin.Trade
    );

    @POST("/api/v1/cancel_order.do")
        //取消订单
    Observable<CancelTradeResp> cancelTrade(@Field("api_key") String apiKey,
                                            @Field("amount") float amount,      //交易数量
                                            @Field("price") float price,        //限价单价格
                                            @Field("sign") String sign,         //请求参数签名
                                            @Field("symbol") String symbol,
                                            @Field("type") String type          ////交易类型Okcoin.Trade
    );

    @POST("/api/v1/order_info.do")
    Observable<CancelTradeResp> fetchOrderInfo(@Field("api_key") String apiKey,
                                               @Field("order_id") String order_id,
                                               @Field("sign") String sign,
                                               @Field("symbol") String symbol
    );

}
