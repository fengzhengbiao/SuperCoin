package com.leapord.supercoin.filter;

import com.leapord.supercoin.core.KlineCalculator;
import com.leapord.supercoin.entity.event.Analysis;

import java.util.List;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/2/11
 *  Description     布林线
 *  Email fengzhengbiao@vcard100.com
 **********************************************/

public class BollFilter implements Filter {


    @Override
    public Analysis intercept(Analysis value, boolean isPurchaseSync, boolean isSellSync) {
        List<double[]> subKlineData = value.getOriginData().getSubKlineData();
        KlineCalculator calculator = new KlineCalculator(subKlineData);
        List<Double> MB = calculator.computeMB();
        List<Double> UP = calculator.computeUP();
        List<Double> DN = calculator.computeDN();
        value.setMB(MB);
        value.setUP(UP);
        value.setDN(DN);
        int topCount = 0, dnCount = 0;
        for (int i = subKlineData.size() - 3; i < subKlineData.size(); i++) {
            if (subKlineData.get(i)[4] > 0) {
                topCount++;
            } else {
                dnCount++;
            }
        }
        if (topCount == 3) {
            value.increasePurchaseConformNum();
        } else if (dnCount == 3) {
            value.increaseSellConformNum();
            value.setCanPurchase(false);
        }
        return value;
    }
}
