package com.leapord.supercoin.filter;

import com.leapord.supercoin.core.Analyzer;
import com.leapord.supercoin.entity.event.Analysis;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/2/11
 *  Description     深度过滤器
 *  Email fengzhengbiao@vcard100.com
 **********************************************/

public class DepthFilter implements Filter {

    @Override
    public Analysis intercept(Analysis value, boolean isPurchaseSync, boolean isSellSync) {
        int tendency = Analyzer.getDepthTendency(value.getOriginData().getDepth());
        value.setDepthTrend(tendency);
        value.setCanSell(tendency < 0 || value.canSell());
        value.setSellConformNum(tendency < 0 ? value.getSellConformNum() + 1 : value.getSellConformNum());
        value.setCanPurchase(tendency > 0 && value.canPurchase());
        return value;
    }
}
