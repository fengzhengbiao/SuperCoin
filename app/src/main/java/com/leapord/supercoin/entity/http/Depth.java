package com.leapord.supercoin.entity.http;

import java.util.List;

import lombok.Data;

/**
 * @author Biao
 * @date 2018/1/20
 * @description
 * @email fengzb0216@sina.com
 */
@Data
public class Depth {
    //卖方深度
    private List<double[]> asks;
    //买方深度
    private List<double[]> bids;


}
