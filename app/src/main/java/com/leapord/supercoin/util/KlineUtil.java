package com.leapord.supercoin.util;

import com.leapord.supercoin.entity.Depth;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import java.util.List;

/**
 * @author Biao
 * @date 2018/1/20
 * @description
 * @email fengzb0216@sina.com
 */

public class KlineUtil {

    /**
     * @param depth
     * @return -2:肯定下跌  -1 ：可能下跌  0 ：平稳  1 : 可能上涨  2 ：上涨
     */
    public static int getDepthTendency(Depth depth) {
        List<double[]> asks = depth.getAsks();
        int size = asks.size();
        int stdAsk = size / 3;
        double askHight = 0, askMiddle = 0, askLow = 0;
        for (int i = 0; i < 3 * stdAsk; i++) {
            if (i < stdAsk) {
                askHight += asks.get(i)[1];
            } else if (i < 2 * stdAsk) {
                askMiddle += asks.get(i)[1];
            } else {
                askLow += asks.get(i)[1];
            }
        }
        List<double[]> bids = depth.getBids();
        double bidHight = 0, bidMiddle = 0, bidLow = 0;
        int sizeBids = bids.size();
        int stdBid = size / 3;
        for (int i = 0; i < 3 * sizeBids; i++) {
            if (i < stdAsk) {
                bidHight += asks.get(i)[1];
            } else if (i < 2 * stdAsk) {
                bidMiddle += asks.get(i)[1];
            } else {
                bidLow += asks.get(i)[1];
            }
        }
        if (askLow > askMiddle && askMiddle > askHight) {       //买盘下降
            if (bidHight > bidMiddle && bidMiddle > bidLow) {
                return 0;      //价格平稳
            } else if (bidHight < bidMiddle && bidMiddle < bidLow) {
                return -2;      //稳定下跌
            } else {
                return -1;      //可能下跌
            }
        } else if (askLow < askMiddle && askMiddle < askHight) {
            if (bidHight > bidMiddle && bidMiddle > bidLow) {
                return 2;      //稳定上涨
            } else if (bidHight < bidMiddle && bidMiddle < bidLow) {
                return 0;      //价格平稳
            } else {
                return 1;      //可能下跌
            }
        } else {
            if (bidHight > bidMiddle && bidMiddle > bidLow) {
                return 1;      //可能上涨
            } else if (bidHight < bidMiddle && bidMiddle < bidLow) {
                return 0;      //可能下跌
            } else {
                return 0;         //价格平稳
            }
        }
    }

    /**
     * [
     * 1417536000000,	时间戳
     * 2370.16,	开
     * 2380,		高
     * 2352,		低
     * 2367.37,	收
     * 17259.83	交易量
     * ]
     *
     * @param kNums
     * @param pointCount 取样点数量      3分钟线建议5个左右  可以根据趋势手动调整
     * @return 上涨点个数
     */
    public static int getIncreasePointCountByKline(List<double[]> kNums, int pointCount) {
        if (pointCount < 3) {
            throw new RuntimeException("采样点不能少于三个");
        }
        int endIndex = kNums.size() - 1;
        int tendency = 0;
        for (int i = endIndex; i > endIndex - pointCount; i++) {
            double[] kData = kNums.get(i);
            tendency += (kData[4] - kData[1]) > 0 ? 1 : 0;
        }
        return tendency;
    }

    /**
     * 获取一阶函数的趋势线
     *
     * @param kNums      K线数据
     * @param pointCount 建议>=5
     * @return -2:下跌  -1 ：可能下跌  0 ：平稳  1 : 可能上涨  2 ：上涨
     */
    public static int getTendencyByKline(List<double[]> kNums, int pointCount) {
        int startIndex = kNums.size() - 1 - pointCount;
        int middleIndex = startIndex + pointCount / 2 + pointCount % 2 == 0 ? 0 : 1;
        WeightedObservedPoints startPoints = new WeightedObservedPoints();
        WeightedObservedPoints fullPoints = new WeightedObservedPoints();
        WeightedObservedPoints points = new WeightedObservedPoints();
        // 将x-y数据元素调用points.add(x[i], y[i])加入到观察点序列中
        for (int i = startIndex; i < kNums.size(); i++) {
            double[] kPoint = kNums.get(i);
            if (i < middleIndex) {
                startPoints.add(kPoint[0], kPoint[1]);
            }
            fullPoints.add(kPoint[0], kPoint[1]);
        }

        // degree 指定多项式阶数
        PolynomialCurveFitter fitter = PolynomialCurveFitter.create(1);
        // 曲线拟合，结果保存于双精度数组中，由常数项至最高次幂系数排列
        double kFull = fitter.fit(points.toList())[1];
        double kStart = fitter.fit(points.toList())[1];
        if (kFull > 0) {
            return kStart < kFull ? 2 : 1;
        } else {
            return kStart < kFull ? -1 : -2;
        }
    }

    /**
     *
     * @param kNums     k线数据
     * @param tendency       k线
     *                       上涨或者下降趋势
     * @return
     */
    public static long getPredicateTime(List<double[]> kNums, int tendency) {
        if (tendency<0){

        }else {

        }
        return 0l;
    }



}
