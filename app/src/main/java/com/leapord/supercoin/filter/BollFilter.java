package com.leapord.supercoin.filter;

import com.leapord.supercoin.core.KlineCalculator;
import com.leapord.supercoin.entity.event.Analysis;

import org.apache.commons.math3.fitting.WeightedObservedPoints;

import java.util.List;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/2/11
 *  Description     布林线
 *  Email fengzhengbiao@vcard100.com
 **********************************************/

public class BollFilter implements Filter {
    @Override
    public Analysis intercept(Analysis value) {
        List<double[]> subKlineData = value.getOriginData().getSubKlineData();
        KlineCalculator calculator = new KlineCalculator(subKlineData);
        List<Double> MB = calculator.computeMB();
        List<Double> UP = calculator.computeUP();
        List<Double> DN = calculator.computeDN();
        value.setMB(MB);
        value.setUP(UP);
        value.setDN(DN);
        WeightedObservedPoints mbPoints = new WeightedObservedPoints();
        return value;
    }
}
