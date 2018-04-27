package com.leapord.supercoin.entity.http.future;

import lombok.Data;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/4/27
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/
@Data
public class Holder {

    /**
     * buy_amount(double):多仓数量
     * buy_available:多仓可平仓数量
     * buy_price_avg(double):开仓平均价
     * buy_price_cost(double):结算基准价
     * buy_profit_real(double):多仓已实现盈余
     * contract_id(long):合约id
     * create_date(long):创建日期
     * lever_rate:杠杆倍数
     * sell_amount(double):空仓数量
     * sell_available:空仓可平仓数量
     * sell_price_avg(double):开仓平均价
     * sell_price_cost(double):结算基准价
     * sell_profit_real(double):空仓已实现盈余
     * symbol:btc_usd   ltc_usd    eth_usd    etc_usd    bch_usd
     * contract_type:合约类型
     * force_liqu_price:预估爆仓价
     */

    private int buy_amount;
    private int buy_available;
    private double buy_price_avg;
    private double buy_price_cost;
    private double buy_profit_real;
    private long contract_id;
    private String contract_type;
    private long create_date;
    private int lever_rate;
    private int sell_amount;
    private int sell_available;
    private int sell_price_avg;
    private int sell_price_cost;
    private int sell_profit_real;
    private String symbol;

}
