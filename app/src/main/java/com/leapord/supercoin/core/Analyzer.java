package com.leapord.supercoin.core;

import android.util.Log;

import com.leapord.supercoin.entity.http.Depth;
import com.leapord.supercoin.util.TimeUtils;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import java.util.List;

/**
 * @author Biao
 * @date 2018/1/20
 * @description
 * @email fengzb0216@sina.com
 */

public class Analyzer {

    private static final String TAG = "Analyzer";

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
        int stdBid = sizeBids / 3;
        for (int i = 0; i < 3 * stdBid; i++) {
            if (i < stdAsk) {
                bidHight += bids.get(i)[1];
            } else if (i < 2 * stdAsk) {
                bidMiddle += bids.get(i)[1];
            } else {
                bidLow += bids.get(i)[1];
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
                return 1;
            }
        } else {
            if (bidHight > bidMiddle && bidMiddle > bidLow) {
                return 1;      //可能上涨
            } else if (bidHight < bidMiddle && bidMiddle < bidLow) {
                return 0;      //可能下跌
            } else {
                return bidLow > 5 * bidHight ? -1 : 0;
            }
        }
    }

    /**
     * 根据买卖盘获取最新市价
     *
     * @param depth
     * @return [卖一价，买一价]
     */
    public static double[] getPriceFromDepth(Depth depth) {
        List<double[]> asks = depth.getAsks();
        return new double[]{asks.get(asks.size() - 1)[0], depth.getBids().get(0)[0]};
    }

    /**
     * 获取最低买入价格和最高买入数量
     *
     * @param depth
     * @return [价格，数量]
     */
    public static double[] getMinBuyDepth(Depth depth) {
        List<double[]> asks = depth.getAsks();
        return asks.get(asks.size() - 1);
    }


    /**
     * 获取最高卖出价格和最高卖出数量
     *
     * @param depth
     * @return [价格，数量]
     */
    public static double[] getMaxSellDepth(Depth depth) {
        List<double[]> bids = depth.getBids();
        return bids.get(0);
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
        if (pointCount < 2) {
            throw new RuntimeException("采样点不能少于三个");
        }
        int endIndex = kNums.size() - 1;
        int tendency = 0;
        for (int i = endIndex; i > endIndex - pointCount; i--) {
            double[] kData = kNums.get(i);
            tendency += (kData[4] - kData[1]) > 0 ? 1 : 0;
        }
        return tendency;
    }

    /**
     * 预测时间是否有效
     *
     * @param time
     * @return
     */
    public static boolean isTimeValid(long time) {
        return Math.abs(time - System.currentTimeMillis()) < 1 * 60 * 60 * 1000;
    }

    /**
     * 获取一阶函数的趋势线
     *
     * @param kNums      K线数据
     * @param pointCount 建议>=5
     * @return -2:下跌  -1 ：可能下跌  0 ：平稳  1 : 可能上涨  2 ：上涨
     */
    public static double[] getTendencyByKline(List<double[]> kNums, int pointCount) {
        int startIndex = kNums.size() - 1 - pointCount;
        int middleIndex = startIndex + pointCount / 2 + (pointCount % 2 == 0 ? 0 : 1);
        WeightedObservedPoints startPoints = new WeightedObservedPoints();
        WeightedObservedPoints endPoints = new WeightedObservedPoints();
        WeightedObservedPoints fullPoints = new WeightedObservedPoints();
        // 将x-y数据元素调用points.add(x[i], y[i])加入到观察点序列中

        for (int i = startIndex; i < kNums.size(); i++) {
            double[] kPoint = kNums.get(i);
            if (i < middleIndex) {
                startPoints.add(i, kPoint[1]);
            } else {
                endPoints.add(i - middleIndex, kPoint[1]);
            }
            fullPoints.add(i, kPoint[1]);
        }
        // degree 指定多项式阶数
        PolynomialCurveFitter fitter = PolynomialCurveFitter.create(1);
        // 曲线拟合，结果保存于双精度数组中，由常数项至最高次幂系数排列
        double kStart = fitter.fit(startPoints.toList())[1];
        double kEnd = fitter.fit(endPoints.toList())[1];
        double kFull = fitter.fit(fullPoints.toList())[1];
        double tendncy = 0;
        if (kFull > 0) {
            tendncy = kEnd < 0 ? -1 : (kStart < kFull ? 2 : 1);
        } else {
            tendncy = kEnd > 0 ? 1 : (kStart < kFull ? -1 : -2);
        }

        return new double[]{tendncy, kStart, kEnd, kFull};
    }


    public static double[] calcLineRatio(List<Double> lineData, int sampSize) {
        int startIndex = lineData.size() - 1 - sampSize;
        WeightedObservedPoints fullPoints = new WeightedObservedPoints();
        // 将x-y数据元素调用points.add(x[i], y[i])加入到观察点序列中
        for (int i = startIndex; i < lineData.size(); i++) {
            fullPoints.add(i, lineData.get(i));
        }
        // degree 指定多项式阶数
        PolynomialCurveFitter fitter = PolynomialCurveFitter.create(1);
        // 曲线拟合，结果保存于双精度数组中，由常数项至最高次幂系数排列
        return fitter.fit(fullPoints.toList());
    }


    /**
     * @param kNums k线数据
     *              <p>
     *              上涨或者下降趋势
     * @return 返回转折点对应的时间
     */
    public static long getPredicateTimeByNearPoint(List<double[]> kNums, int pointCount) {
        int endIndex = kNums.size() - 1;
        WeightedObservedPoints points = new WeightedObservedPoints();
        for (int i = endIndex - pointCount; i < endIndex; i++) {
            double[] doubles = kNums.get(i);
            if (i == endIndex - pointCount) {
                Log.i(TAG, "StartTime: " + TimeUtils.formatDate((long) doubles[0]));
            }
            points.add(doubles[0], doubles[1]);
        }
        PolynomialCurveFitter fitter = PolynomialCurveFitter.create(2);
        double[] fit = fitter.fit(points.toList());
        return getPoleX(fit);
    }

    /**
     * 自动取点获取预测时间
     *
     * @param kNums
     * @param tendency
     * @return
     */
    public static long getAutoPredicateTime(List<double[]> kNums, int tendency) {
        double[] tendencyByKline = getTendencyByKline(kNums, 7);
        if (tendency > 0) {
            if (tendencyByKline[1] < tendencyByKline[2]) {
                return System.currentTimeMillis() - 5 * 60 * 60 * 1000;
            }
        } else if (tendency > 0) {
            if (tendencyByKline[1] > tendencyByKline[2]) {
                return System.currentTimeMillis() - 5 * 60 * 60 * 1000;
            }
        } else {
            return System.currentTimeMillis();
        }
        double[] tendencyByKline2 = getTendencyByKline(kNums, 9);
        int observedPointCount = 9;
        if (tendency > 0) {
            while (tendencyByKline2[1] < tendencyByKline[1]) {
                tendencyByKline = tendencyByKline2;
                observedPointCount += 2;
                tendencyByKline2 = getTendencyByKline(kNums, observedPointCount);
            }
        } else {
            while (tendencyByKline2[1] > tendencyByKline[1]) {
                tendencyByKline = tendencyByKline2;
                observedPointCount += 2;
                tendencyByKline2 = getTendencyByKline(kNums, observedPointCount);
            }
        }
        int endIndex = kNums.size() - 1;
        WeightedObservedPoints points = new WeightedObservedPoints();
        for (int i = endIndex; i > endIndex - observedPointCount; i--) {
            double[] kPoint = kNums.get(i);
            points.add(kPoint[0], kPoint[1]);
        }
        Log.i(TAG, "AutoPredicatePointCount: " + observedPointCount + "   K start:" + tendencyByKline2[1]);
        PolynomialCurveFitter fitter = PolynomialCurveFitter.create(2);
        double[] fit = fitter.fit(points.toList());
        return getPoleX(fit);
    }


    /**
     * 是否连续几个点上涨
     *
     * @param kNums
     * @param continuCount
     * @return
     */
    public static boolean isContinuousIncrease(List<double[]> kNums, int continuCount) {
        int size = kNums.size();
        int endIndex = size - 1 - continuCount;
        for (int i = size - 1; i > endIndex; i--) {
            double[] kPoint = kNums.get(i);
            if (kPoint[4] < kPoint[1]) {
                if (i == size - 1) {
                    return false;
                } else {
                    double[] kPointPre = kNums.get(i);
                    double diffPre = kPointPre[4] - kPoint[1];
                    double diff = kPoint[1] - kPoint[4];
                    if (diffPre > 0 && diffPre > 10 * diff) {
                        endIndex--;
                        continue;
                    }
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 是否连续几个点下降
     *
     * @param kNums
     * @param continuCount
     * @return
     */
    public static boolean isContinuousDecrease(List<double[]> kNums, int continuCount) {
        int size = kNums.size();
        int endIndex = size - 1 - continuCount;
        for (int i = size - 1; i > endIndex; i--) {
            double[] kPoint = kNums.get(i);
            if (kPoint[4] > kPoint[1]) {
                if (i == size - 1) {
                    return false;
                } else {
                    double[] kPointPre = kNums.get(i);
                    double diffPre = kPointPre[1] - kPoint[4];
                    double diff = kPoint[4] - kPoint[1];
                    if (diffPre < 0 && diffPre > 10 * diff) {
                        endIndex--;
                        continue;
                    }
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 获取二次曲线的极点位置
     *
     * @param fit
     * @return 获取不到时默认返回0
     */
    public static long getPoleX(double[] fit) {
        if (fit != null && fit.length > 1) {
            return (long) (-fit[1] / (2 * fit[2]));
        }
        return 0L;
    }

    /**
     * 判断KDJ末尾处是否有交点
     *
     * @param K
     * @param D
     * @param J
     * @return
     */
    public static boolean hasCrossAtEnd(List<Double> K, List<Double> D, List<Double> J) {
        int endIndex = K.size() - 1;
        for (int i = endIndex; i >= endIndex - 2; i--) {
            if (K.get(i) == D.get(i) && D.get(i) == J.get(i)) {
                return true;
            }
        }
        if (J.get(endIndex) > K.get(endIndex) && K.get(endIndex) > D.get(endIndex)) {    //上涨
            if (J.get(endIndex - 1) < K.get(endIndex - 1) && K.get(endIndex - 1) < D.get(endIndex - 1)) {
                return true;
            }
        }
        if (J.get(endIndex) < K.get(endIndex) && K.get(endIndex) < D.get(endIndex)) {    //下跌
            if (J.get(endIndex - 1) > K.get(endIndex - 1) && K.get(endIndex - 1) > D.get(endIndex - 1)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 获取当前趋势
     *
     * @param K
     * @param D
     * @param J
     * @return
     */
    public static int getTendency(List<Double> K, List<Double> D, List<Double> J) {
        int endIndex = K.size() - 1;
        if (J.get(endIndex) > K.get(endIndex) && K.get(endIndex) > D.get(endIndex)) {    //上涨
            return 1;
        }
        if (J.get(endIndex) < K.get(endIndex) && K.get(endIndex) < D.get(endIndex)) {    //下跌
            return -1;
        }
        return 0;
    }

    /**
     * 是否穿过0点
     *
     * @param macd
     * @param balance 稳定点个数
     * @return
     */
    public static boolean hasCrossZero(List<Double> macd, int balance) {
        int endIndex = macd.size() - 1;
        if (macd.get(endIndex) >= 0) {
            for (int i = endIndex - 1 - balance; i <= endIndex - 1; i++) {
                if (macd.get(i) > 0) {
                    return false;
                }
            }
            Log.i(TAG, "hasCrossZero: 买入最佳点");
            return true;
        }
        if (macd.get(endIndex) < 0) {
            for (int i = endIndex - 1 - balance; i <= endIndex - 1; i++) {
                if (macd.get(i) < 0) {
                    return false;
                }
            }
            Log.i(TAG, "hasCrossZero: 卖出最佳点");
            return true;
        }
        return false;
    }

    public static boolean isMiddle(double value) {
        return value > 20 && value < 80;
    }

}
