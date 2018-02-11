package com.leapord.supercoin.filter;

import com.leapord.supercoin.core.KlineCalculator;
import com.leapord.supercoin.entity.event.Analysis;

import java.util.List;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/2/11
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/

public class MACDFilter implements Filter {
    @Override
    public Analysis intercept(Analysis value) {
        KlineCalculator calculator = new KlineCalculator(value.getOriginData().getKLineData());
        List<Double> deas = calculator.computeDEAS();
        List<Double> difs = calculator.computeDIFS();
        List<Double> macds = calculator.computeMACDS();
        KlineCalculator subCalculator = new KlineCalculator(value.getOriginData().getSubKlineData());
        value.setDEAS(deas);
        value.setDIFS(difs);
        value.setMACDS(macds);
        List<Double> subMacd = subCalculator.computeDEAS();
        value.setSubDea(subMacd);
        int endIndex = macds.size() - 1;
        Double macdEnd = macds.get(endIndex);
        Double macdEnPre = macds.get(endIndex - 1);
        if (macdEnd > 0 && macdEnPre > 0) {
            if (subMacd.get(subMacd.size() - 1) < subMacd.get(subMacd.size() - 3)) {
                value.setCanPurchase(false);
            }
        } else if (macdEnd < 0 && macdEnPre < 0) {
            value.setCanSell(true);
            value.setSellConformNum(value.getSellConformNum() + 1);
        } else {
            value.setCanPurchase(false);
        }
        return value;
    }
}
