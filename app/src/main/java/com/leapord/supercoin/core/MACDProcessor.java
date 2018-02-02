package com.leapord.supercoin.core;

import java.util.ArrayList;
import java.util.List;

/**
 * MACD平滑指标异同移动平均线
 * EMA（12）=前一日EMA（12）×11/13＋今日收盘价×2/13
 * EMA（26）=前一日EMA（26）×25/27＋今日收盘价×2/27
 * Author: liuk
 * Created at: 16/4/22
 */
public class MACDProcessor {
    private MACDProcessor() {
    }

    private static List<Double> DEAs;  //异同平均数(辅)
    private static List<Double> DIFs;  //差离值(主）
    private static List<Double> MACDs;


    public static void process(List<double[]> kNums) {
        DEAs = new ArrayList<>();
        DIFs = new ArrayList<>();
        MACDs = new ArrayList<>();


        double ema12 = 0;
        double ema26 = 0;
        double diff = 0;
        double dea = 0;
        double macd = 0;

        for (int i = 0; i < kNums.size(); i++) {
            double close = kNums.get(i)[4];

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

            DIFs.add(diff);
            DEAs.add(dea);
            MACDs.add(macd);
        }
    }


    public static List<Double> getDEA() {
        return DEAs;
    }

    public static List<Double> getDIF() {
        return DIFs;
    }

    public static List<Double> getMACD() {
        return MACDs;
    }

}
