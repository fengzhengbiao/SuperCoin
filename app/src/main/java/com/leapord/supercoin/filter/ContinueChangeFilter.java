package com.leapord.supercoin.filter;

import com.leapord.supercoin.core.Analyzer;
import com.leapord.supercoin.entity.event.Analysis;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/2/11
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/

public class ContinueChangeFilter implements Filter {
    //连续变化点个数
    private int count = 3;

    public ContinueChangeFilter(int count) {
        this.count = count;
    }


    @Override
    public Analysis intercept(Analysis value, boolean isPurchaseSync, boolean isSellSync) {
        boolean increase = Analyzer.isContinuousIncrease(value.getOriginData().getKLineData(), count);
        value.setContinueIncrease(increase);
        if (increase) {
            value.increasePurchaseConformNum();
        } else {
            boolean decrease = Analyzer.isContinuousDecrease(value.getOriginData().getKLineData(), count);
            if (decrease){
                value.increaseSellConformNum();
            }
        }
        return value;
    }
}
