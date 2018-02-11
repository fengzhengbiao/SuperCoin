package com.leapord.supercoin.filter;

import com.leapord.supercoin.core.KlineCalculator;
import com.leapord.supercoin.entity.event.Analysis;

import java.util.List;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/2/11
 *  Description 适合于短期分析     强规则
 *  Email fengzhengbiao@vcard100.com
 **********************************************/

public class KDJFilter implements Filter {
    @Override
    public Analysis intercept(Analysis value) {
        KlineCalculator calculator = new KlineCalculator(value.getOriginData().getKLineData());
        List<Double> K = calculator.computeK();
        List<Double> D = calculator.computeD();
        List<Double> J = calculator.computeJ();
        value.setK(K);
        value.setD(D);
        value.setJ(J);
        int endIndex = K.size() - 1;
        double kEnd = K.get(endIndex);
        double dEnd = D.get(endIndex);
        double jEnd = J.get(endIndex);
        if (jEnd > kEnd && kEnd > dEnd) {
            value.setCanPurchase(true);
        } else if (jEnd < kEnd && kEnd < dEnd) {
            value.setCanSell(true);
            value.setSellConformNum(value.getSellConformNum() + 1);
        } else {
            value.setCanPurchase(false);
            value.setCanSell(false);
        }
        return value;
    }
}
