package com.leapord.supercoin.filter;

import com.leapord.supercoin.core.KlineCalculator;
import com.leapord.supercoin.entity.event.Analysis;

import java.util.List;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/2/11
 *  Description     弱指标
 *  Email fengzhengbiao@vcard100.com
 **********************************************/

public class RSIFilter implements Filter {
    @Override
    public Analysis intercept(Analysis value, boolean isPurchaseSync, boolean isSellSync) {
        KlineCalculator calculator = new KlineCalculator(value.getOriginData().getKLineData());
        List<Double> RSI1 = calculator.computeRSI1();
        List<Double> RSI2 = calculator.computeRSI2();
        List<Double> RSI3 = calculator.computeRSI3();
        value.setRSI1(RSI1);
        value.setRSI2(RSI2);
        value.setRSI3(RSI3);
        int endIndex = RSI1.size() - 1;
        if (RSI1.get(endIndex) > 50 && RSI2.get(endIndex) > 50 && RSI3.get(endIndex) > 50) {
            value.setCanPurchase(true && value.canPurchase());
        }
        if (RSI1.get(endIndex) < 50 && RSI2.get(endIndex) < 50 && RSI3.get(endIndex) < 50) {
            value.setCanSell(true);
            value.increaseSellConformNum();
        }
        return value;
    }
}
