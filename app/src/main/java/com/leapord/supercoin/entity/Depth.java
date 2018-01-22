package com.leapord.supercoin.entity;

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

    private List<double[]> asks;

    private List<double[]> bids;


}
