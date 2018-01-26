package com.leapord.supercoin.observer;

import android.text.TextUtils;

import com.leapord.supercoin.entity.OkCoin;
import com.leapord.supercoin.entity.TradeResponse;
import com.leapord.supercoin.util.SpUtils;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/1/26
 *  Description             交易结果监听器
 *  Email fengzhengbiao@vcard100.com
 **********************************************/

public class TradeObserver extends CoinObserver<TradeResponse> {
    private String symbol;
    private String tradeType;

    public TradeObserver(String symbol, String tradeType) {
        this.symbol = symbol;
        this.tradeType = tradeType;
    }

    @Override
    public void onNext(TradeResponse value) {
        if (TextUtils.equals(tradeType, OkCoin.Trade.BUY)) {
            if (value.isResult()) {
                String orderIds = SpUtils.getString(symbol, "");
                if (TextUtils.isEmpty(orderIds)) {
                    SpUtils.putString(symbol, value.getOrder_id());
                } else {
                    SpUtils.putString(symbol, orderIds + "," + value.getOrder_id());
                }
            }
        } else {
            if (value.isResult()) {
                String orderIds = SpUtils.getString(symbol, "");
                if (!TextUtils.isEmpty(orderIds)) {
                    String[] split = orderIds.split(",");
                    StringBuffer buffer = new StringBuffer();
                    for (int i = 0; i < split.length; i++) {
                        if (!TextUtils.equals(split[i], value.getOrder_id())) {
                            if (i != split.length - 1) {
                                buffer.append(split[i]);
                            } else {
                                buffer.append(split[i]);
                                buffer.append(",");
                            }
                        }
                    }
                }
            }
        }
    }
}
