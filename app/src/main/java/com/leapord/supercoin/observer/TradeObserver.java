package com.leapord.supercoin.observer;

import android.os.Looper;

import com.leapord.supercoin.app.CoinApplication;
import com.leapord.supercoin.entity.dao.Trade;
import com.leapord.supercoin.entity.dao.TradeDao;
import com.leapord.supercoin.entity.event.TradeChangeEvent;
import com.leapord.supercoin.util.LogUtil;
import com.leapord.supercoin.util.ToastUtis;

import org.greenrobot.eventbus.EventBus;


/*********************************************
 *  Author  JokerFish 
 *  Create   2018/2/7
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/

public class TradeObserver extends CoinObserver<Trade> {
    @Override
    public void onNext(Trade trade) {
        CoinApplication.INSTANCE.setLastOptimalTime(System.currentTimeMillis());
        LogUtil.e("CoinProcess", " >>>   ******  make one trade, type : " + trade.getSellType());
        TradeDao tradeDao = CoinApplication.INSTANCE.getDaoSession().getTradeDao();
        tradeDao.save(trade);
        EventBus.getDefault().post(new TradeChangeEvent(trade.getSellType(), trade.getSymbol()));
    }

    @Override
    public void onError(Throwable e) {
        new android.os.Handler(Looper.getMainLooper()).post(() -> ToastUtis.showToast("交易失败：" + e.toString()));
        LogUtil.i(TAG, "onError: " + e.toString());
        super.onError(e);
    }
}
