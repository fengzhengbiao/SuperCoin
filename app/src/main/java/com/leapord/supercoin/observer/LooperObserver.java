package com.leapord.supercoin.observer;

import com.leapord.supercoin.core.TradeManager;
import com.leapord.supercoin.entity.event.Analysis;
import com.leapord.supercoin.entity.http.LiveData;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/2/11
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/

public class LooperObserver extends CoinObserver<LiveData> {

    private static LooperObserver sLooperObserver = null;

    private LooperObserver() {
    }

    public static LooperObserver getInstance() {
        if (sLooperObserver == null) {
            synchronized (LooperObserver.class) {
                sLooperObserver = new LooperObserver();
            }
        }
        return sLooperObserver;
    }


    @Override
    public void onNext(LiveData value) {
        Analysis process = TradeManager.getInstance().process(value);
        if (process.canPurchase()) {

        } else {

        }
    }
}
