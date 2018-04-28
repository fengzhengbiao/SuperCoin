package com.leapord.supercoin.entity.http.future;

import lombok.Data;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/4/28
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/
@Data
public class FutureOrder {

//    amount: 委托数量
//    contract_name: 合约名称
//    create_date: 委托时间
//    deal_amount: 成交数量
//    fee: 手续费
//    order_id: 订单ID
//    price: 订单价格
//    price_avg: 平均价格
//    status: 订单状态(0等待成交 1部分成交 2全部成交 -1撤单 4撤单处理中 5撤单中)
//    symbol: btc_usd   ltc_usd    eth_usd    etc_usd    bch_usd
//    type: 订单类型 1：开多 2：开空 3：平多 4： 平空
//    unit_amount:合约面值
//    lever_rate: 杠杆倍数  value:10\20  默认10

    private double amount;
    private String contract_name;
    private long create_date;
    private double deal_amount;
    private double fee;
    private String order_id;
    private double price;
    private double price_avg;
    private int status;
    private String symbol;
    private String type;
    private double unit_amount;
    private int lever_rate;


}
