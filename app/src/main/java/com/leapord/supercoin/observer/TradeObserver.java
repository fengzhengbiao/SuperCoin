package com.leapord.supercoin.observer;

import android.util.Log;

import com.leapord.supercoin.app.CoinApplication;
import com.leapord.supercoin.entity.dao.Trade;
import com.leapord.supercoin.entity.dao.TradeDao;
import com.leapord.supercoin.util.ToastUtis;


/*********************************************
 *  Author  JokerFish 
 *  Create   2018/2/7
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/

public class TradeObserver extends CoinObserver<Trade> {
    @Override
    public void onNext(Trade trade) {
        Log.e("CoinProcess", ">>>   ******   -----   make one trade, type : " + trade.getSellType() + "  -----  *****    >>>");
        TradeDao tradeDao = CoinApplication.INSTANCE.getDaoSession().getTradeDao();
        tradeDao.save(trade);
    }

    @Override
    public void onError(Throwable e) {
        ToastUtis.showToast("交易失败：" + e.toString());
        Log.i(TAG, "onError: " + e.toString());
        super.onError(e);
    }
}
