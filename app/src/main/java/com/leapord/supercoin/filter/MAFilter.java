package com.leapord.supercoin.filter;

import com.leapord.supercoin.core.KlineCalculator;
import com.leapord.supercoin.entity.event.Analysis;

import java.util.List;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/2/11
 *  Description     移动平滑处理  弱规则
 *  Email fengzhengbiao@vcard100.com
 **********************************************/

public class MAFilter implements Filter {
    @Override
    public Analysis intercept(Analysis value) {
        KlineCalculator calculator = new KlineCalculator(value.getOriginData().getKLineData());
        List<Double> MA5 = calculator.computeMA5();
        List<Double> MA10 = calculator.computeMA10();
        int endIndex = MA5.size() - 1;
        Double endMa5 = MA5.get(endIndex);
        Double endMa10 = MA10.get(endIndex);
        if (endMa5 < endMa10) {
            value.setCanSell(true);
            value.increaseSellConformNum();
        }
        return value;
    }
}
