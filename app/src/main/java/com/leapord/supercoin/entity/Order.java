package com.leapord.supercoin.entity;

/**
 * @author Biao
 * @date 2018/1/20
 * @description
 * @email fengzb0216@sina.com
 */

public class Order {

    /**
     * amount : 0.1     委托数量
     * avg_price : 0        平均成交价
     * create_date : 1418008467000      委托时间
     * deal_amount : 0          成交数量
     * order_id : 10000591      订单ID
     * orders_id : 10000591
     * price : 500          委托价格
     * status : 0           -1:已撤销  0:未成交  1:部分成交  2:完全成交 3:撤单处理中
     * symbol : btc_usd
     * type : sell          buy_market:市价买入 / sell_market:市价卖出
     */

    private double amount;
    private int avg_price;
    private long create_date;
    private int deal_amount;
    private int order_id;
    private int orders_id;
    private int price;
    private int status;
    private String symbol;
    private String type;


}
