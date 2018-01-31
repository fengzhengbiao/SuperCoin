package com.leapord.supercoin.observer;

import com.leapord.supercoin.app.CoinApplication;
import com.leapord.supercoin.entity.dao.Trade;
import com.leapord.supercoin.entity.dao.TradeDao;
import com.leapord.supercoin.entity.event.TradeChangeEvent;
import com.leapord.supercoin.entity.http.TradeResponse;
import com.leapord.supercoin.util.SpUtils;

import org.greenrobot.eventbus.EventBus;

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
        TradeDao tradeDao = CoinApplication.INSTANCE.getDaoSession().getTradeDao();
        Trade trade = new Trade();
        trade.setSymbol(symbol);
        trade.setOrderId(value.getOrder_id());
        trade.setSellType(tradeType);
        trade.setStatus(value.isResult());
        tradeDao.save(trade);
        EventBus.getDefault().post(new TradeChangeEvent(tradeType, symbol));
        if (value.isResult()){
            SpUtils.putLong(symbol+tradeType,System.currentTimeMillis());
        }
    }
}
