package com.leapord.supercoin.core;

import java.util.ArrayList;
import java.util.List;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/2/9
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/

public final class KlineCalculator {

    private List<double[]> kPoints;     //K线数据

    public KlineCalculator(List<double[]> kPoints) {
        this.kPoints = kPoints;
    }


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


    public List<Double> computeMA5() {
        if (MA5 == null || MA5.size() == 0) {
            computeMA();
        }
        return MA5;
    }

    public List<Double> computeMA10() {
        if (MA10 == null || MA10.size() == 0) {
            computeMA();
        }
        return MA10;
    }

    public List<Double> computeMA20() {
        if (MA20 == null || MA20.size() == 0) {
            computeMA();
        }
        return MA20;
    }

    public List<Double> computeVOLUM_EMA5() {
        if (VOLUM_EMA5 == null || VOLUM_EMA5.size() == 0) {
            computeMA();
        }
        return VOLUM_EMA5;
    }

    public List<Double> computeVOLUME_MA10() {
        if (VOLUME_MA10 == null || VOLUME_MA10.size() == 0) {
            computeMA();
        }
        return VOLUME_MA10;
    }

    public List<Double> computeDEAS() {
        if (DEAS == null || DEAS.size() == 0) {
            computeMACD();
        }
        return DEAS;
    }

    public List<Double> computeDIFS() {
        if (DIFS == null || DIFS.size() == 0) {
            computeMACD();
        }
        return DIFS;
    }

    public List<Double> computeMACDS() {
        if (MACDS == null || MACDS.size() == 0) {
            computeMACD();
        }
        return MACDS;
    }

    public List<Double> computeUP() {
        if (UP == null || UP.size() == 0) {
            computeBOLL();
        }
        return UP;
    }

    public List<Double> computeMB() {
        if (MB == null || MB.size() == 0) {
            computeBOLL();
        }
        return MB;
    }


    public List<Double> computeRSI1() {
        if (RSI1 == null || RSI1.size() == 0) {
            computeRSI();
        }
        return RSI1;
    }

    public List<Double> computeRSI2() {
        if (RSI2 == null || RSI2.size() == 0) {
            computeRSI();
        }
        return RSI2;
    }

    public List<Double> computeRSI3() {
        if (RSI3 == null || RSI3.size() == 0) {
            computeRSI();
        }
        return RSI3;
    }

    public List<Double> computeDN() {
        if (DN == null || DN.size() == 0) {
            computeBOLL();
        }
        return DN;
    }


    public List<Double> computeK() {
        if (K == null || K.size() == 0) {
            computeKDJ();
        }
        return K;
    }

    public List<Double> computeD() {
        if (D == null || D.size() == 0) {
            computeKDJ();
        }
        return D;
    }

    public List<Double> computeJ() {
        if (J == null || J.size() == 0) {
            computeKDJ();
        }
        return J;
    }

    private void computeMA() {
        MA5 = new ArrayList<>();
        MA10 = new ArrayList<>();
        MA20 = new ArrayList<>();
        VOLUM_EMA5 = new ArrayList<>();
        VOLUME_MA10 = new ArrayList<>();
        double ma5 = 0;
        double ma10 = 0;
        double ma20 = 0;
        double volumeMa5 = 0;
        double volumeMa10 = 0;
        for (int i = 0; i < kPoints.size(); i++) {
            double[] kPoint = kPoints.get(i);
            ma5 += kPoint[4];
            ma10 += kPoint[4];
            ma20 += kPoint[4];
            volumeMa5 += kPoint[5];
            volumeMa10 += kPoint[5];
            if (i >= 5) {
                ma5 -= kPoints.get(i - 5)[4];
                MA5.add(ma5 / 5);
                volumeMa5 -= kPoints.get(i - 5)[5];
                VOLUM_EMA5.add(volumeMa5 / 5f);
            } else {
                MA5.add(ma5 / (i + 1f));
                VOLUM_EMA5.add(volumeMa5 / (i + 1f));
            }
            if (i >= 10) {
                ma10 -= kPoints.get(i - 10)[4];
                MA10.add(ma10 / 10f);
                volumeMa10 -= kPoints.get(i - 10)[5];
                VOLUME_MA10.add(volumeMa10 / 5f);
            } else {
                MA10.add(ma10 / (i + 1f));
                VOLUME_MA10.add(volumeMa10 / (i + 1f));
            }
            if (i >= 20) {
                ma20 -= kPoints.get(i - 20)[4];
                MA20.add(ma20 / 20f);
            } else {
                MA20.add(ma20 / (i + 1f));
            }
        }
    }


    private void computeMACD() {
        DEAS = new ArrayList<>();
        DIFS = new ArrayList<>();
        MACDS = new ArrayList<>();
        double ema12 = 0;
        double ema26 = 0;
        double diff = 0;
        double dea = 0;
        double macd = 0;
        for (int i = 0; i < kPoints.size(); i++) {
            double close = kPoints.get(i)[4];
            if (i == 0) {
                ema12 = close;
                ema26 = close;
            } else {
                // EMA（12） = 前一日EMA（12） X 11/13 + 今日收盘价 X 2/13
                // EMA（26） = 前一日EMA（26） X 25/27 + 今日收盘价 X 2/27
                ema12 = ema12 * 11f / 13f + close * 2f / 13f;
                ema26 = ema26 * 25f / 27f + close * 2f / 27f;
            }
            // DIF = EMA（12） - EMA（26） 。
            // 今日DEA = （前一日DEA X 8/10 + 今日DIF X 2/10）
            // 用（DIF-DEA）*2 即为 MACD 柱状图。
            diff = ema12 - ema26;
            dea = dea * 8f / 10f + diff * 2f / 10f;
            macd = (diff - dea) * 2f;

            DIFS.add(diff);
            DEAS.add(dea);
            MACDS.add(macd);
        }
    }


    private void computeBOLL() {
        if (MA20 == null || MA20.size() == 0) {
            computeMA();
        }
        MB = new ArrayList<>();
        UP = new ArrayList<>();
        DN = new ArrayList<>();
        for (int i = 0; i < kPoints.size(); i++) {
            double[] kPoint = kPoints.get(i);
            if (i == 0) {
                MB.add(kPoint[4]);
                UP.add(Double.NaN);
                DN.add(Double.NaN);
            } else {
                int n = 20;
                if (i < 20) {
                    n = i + 1;
                }
                double md = 0;
                for (int j = i - n + 1; j <= i; j++) {
                    double c = kPoints.get(j)[4];
                    double m = MA20.get(i);
                    double value = c - m;
                    md += value * value;
                }
                md = md / (n - 1);
                md = (float) Math.sqrt(md);
                MB.add(MA20.get(i));
                UP.add(MB.get(i) + 2f * md);
                DN.add(MB.get(i) - 2f * md);
            }
        }
    }


    private void computeRSI() {
        RSI1 = new ArrayList<>();
        RSI2 = new ArrayList<>();
        RSI3 = new ArrayList<>();
        double rsi1 = 0;
        double rsi2 = 0;
        double rsi3 = 0;
        double rsi1ABSEma = 0;
        double rsi2ABSEma = 0;
        double rsi3ABSEma = 0;
        double rsi1MaxEma = 0;
        double rsi2MaxEma = 0;
        double rsi3MaxEma = 0;

        for (int i = 0; i < kPoints.size(); i++) {
            double[] kPoint = kPoints.get(i);

            if (i == 0) {
                rsi1 = 0;
                rsi2 = 0;
                rsi3 = 0;
                rsi1ABSEma = 0;
                rsi2ABSEma = 0;
                rsi3ABSEma = 0;
                rsi1MaxEma = 0;
                rsi2MaxEma = 0;
                rsi3MaxEma = 0;
            } else {
                double Rmax = Math.max(0, kPoint[4] - kPoints.get(i - 1)[4]);
                double RAbs = Math.abs(kPoint[4] - kPoints.get(i - 1)[4]);

                rsi1MaxEma = (Rmax + (6f - 1) * rsi1MaxEma) / 6f;
                rsi1ABSEma = (RAbs + (6f - 1) * rsi1ABSEma) / 6f;

                rsi2MaxEma = (Rmax + (12f - 1) * rsi2MaxEma) / 12f;
                rsi2ABSEma = (RAbs + (12f - 1) * rsi2ABSEma) / 12f;

                rsi3MaxEma = (Rmax + (24f - 1) * rsi3MaxEma) / 24f;
                rsi3ABSEma = (RAbs + (24f - 1) * rsi3ABSEma) / 24f;

                rsi1 = (rsi1MaxEma / rsi1ABSEma) * 100;
                rsi2 = (rsi2MaxEma / rsi2ABSEma) * 100;
                rsi3 = (rsi3MaxEma / rsi3ABSEma) * 100;
            }

            RSI1.add(rsi1);
            RSI2.add(rsi2);
            RSI3.add(rsi3);
        }
    }


    private void computeKDJ() {
        K = new ArrayList<>();
        D = new ArrayList<>();
        J = new ArrayList<>();
        double k = 0;
        double d = 0;

        for (int i = 0; i < kPoints.size(); i++) {
            double[] kPoint = kPoints.get(i);

            int startIndex = i - 8;
            if (startIndex < 0) {
                startIndex = 0;
            }

            double max9 = Float.MIN_VALUE;
            double min9 = Float.MAX_VALUE;
            for (int index = startIndex; index <= i; index++) {
                max9 = Math.max(max9, kPoints.get(index)[2]);
                min9 = Math.min(min9, kPoints.get(index)[3]);
            }

            double rsv = 100f * (kPoint[4] - min9) / (max9 - min9);
            if (i == 0) {
                k = rsv;
                d = rsv;
            } else {
                k = (rsv + 2f * k) / 3f;
                d = (k + 2f * d) / 3f;
            }
            K.add(k);
            D.add(d);
            J.add(3f * k - 2 * d);
        }
    }

}
