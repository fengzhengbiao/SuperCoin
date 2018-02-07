package com.leapord.supercoin.observer;

import com.leapord.supercoin.app.CoinApplication;
import com.leapord.supercoin.entity.dao.Trade;
import com.leapord.supercoin.entity.dao.TradeDao;


/*********************************************
 *  Author  JokerFish 
 *  Create   2018/2/7
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/

public class TradeObserver extends CoinObserver<Trade> {
    @Override
    public void onNext(Trade trade) {
        TradeDao tradeDao = CoinApplication.INSTANCE.getDaoSession().getTradeDao();
        tradeDao.save(trade);
    }
}
