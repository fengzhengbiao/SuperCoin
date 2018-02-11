package com.leapord.supercoin.entity.event;

import com.leapord.supercoin.entity.http.LiveData;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/2/11
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/
@Data
@NoArgsConstructor
public class Analysis {

    private LiveData originData;

    private int depthTrend;
    private int depthPrice[];
    private boolean continueIncrease;
    private boolean continueDecrease;
    private int kLineTrend;


    // MA 指标的三个属性
    private List<Double> MA5;
    private List<Double> MA10;
    private List<Double> MA20;
    // 量的5日平均和10日平均
    private List<Double> VOLUM_EMA5;
    private List<Double> VOLUME_MA10;

    // MACD 指标的三个属性
    private List<Double> DEAS;  //异同平均数(辅)
    private List<Double> DIFS;  //差离值(主）
    private List<Double> MACDS;
    private List<Double> subMacd;
    private List<Double> subDea;
    private List<Double> subDif;

    // KDJ 指标的三个属性
    private List<Double> K;
    private List<Double> D;
    private List<Double> J;

    // RSI 指标的三个属性
    private List<Double> RSI1;
    private List<Double> RSI2;
    private List<Double> RSI3;

    // BOLL 指标的三个属性
    private List<Double> UP; // 上轨线
    private List<Double> MB; // 中轨线
    private List<Double> DN; // 下轨线

    private boolean canPurchase = true;
    private boolean canSell;
    private int sellConformNum;

    public void increaseSellConformNum() {
        this.sellConformNum += 1;
    }


    public boolean canPurchase() {
        return canPurchase;
    }

    public boolean canSell() {
        return canSell;
    }

}
