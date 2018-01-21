package com.leapord.supercoin.util;

import java.util.List;

/**
 * @author Biao
 * @date 2018/1/20
 * @description
 * @email fengzb0216@sina.com
 */

public class KlineUtil {

    // Used to load the 'libKline' library on application startup.
    static {
        System.loadLibrary("kline");
    }

    public native static int getTendency(List<List<Float>> kNums);

    public native static long getFirstIncreaseTime(List<List<Float>> kNums);

    public native static long getFirstDecreaseTime(List<List<Float>> kNums);

    public native static long getSecondIncreaseTime(List<List<Float>> kNums);

    public native static long getSecondDecreaseTime(List<List<Float>> kNums);

}
