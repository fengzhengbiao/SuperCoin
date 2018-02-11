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
    public Analysis intercept(Analysis value) {
        boolean increase = Analyzer.isContinuousIncrease(value.getOriginData().getKLineData(), count);
        boolean decrease = Analyzer.isContinuousDecrease(value.getOriginData().getKLineData(), count);
        value.setContinueIncrease(increase);
        value.setContinueDecrease(decrease);
        value.setCanPurchase(increase && value.canPurchase());
        value.setCanSell(decrease || value.canSell());
        value.setSellConformNum(decrease ? value.getSellConformNum() + 1 : value.getSellConformNum());
        return value;
    }
}
