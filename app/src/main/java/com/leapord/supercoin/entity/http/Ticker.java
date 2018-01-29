package com.leapord.supercoin.entity.http;

import lombok.Data;

/**
 * @author Biao
 * @date 2018/1/20
 * @description
 * @email fengzb0216@sina.com
 */
@Data
public class Ticker {

    /**
     * buy : 33.15   买一价
     * high : 34.15  最高价
     * last : 33.15  最新成交价
     * low : 32.05   最低价
     * sell : 33.16   卖一价
     * vol : 10532696.39199642   成交量(最近的24小时)
     */

    private float buy;
    private float high;
    private float last;
    private float low;
    private float sell;
    private float vol;

}
