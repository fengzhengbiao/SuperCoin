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
public class OrderData<T> {
    private boolean result;
    private List<T> orders;
}
