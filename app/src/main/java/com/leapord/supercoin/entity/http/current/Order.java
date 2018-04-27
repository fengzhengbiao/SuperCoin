package com.leapord.supercoin.entity.http.current;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Biao
 * @date 2018/1/20
 * @description
 * @email fengzb0216@sina.com
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
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
    private double deal_amount;
    private Long order_id;
    private Long orders_id;
    private double price;
    private int status;
    private String symbol;
    private String type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Order order = (Order) o;
        return order_id == order.order_id;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(amount);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + avg_price;
        result = 31 * result + (int) (create_date ^ (create_date >>> 32));
        temp = Double.doubleToLongBits(deal_amount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (order_id != null ? order_id.hashCode() : 0);
        result = 31 * result + (orders_id != null ? orders_id.hashCode() : 0);
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + status;
        result = 31 * result + (symbol != null ? symbol.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
