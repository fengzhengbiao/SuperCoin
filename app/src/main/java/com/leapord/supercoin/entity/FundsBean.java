package com.leapord.supercoin.entity;

import java.util.Map;

import lombok.Data;

/**
 * @author Biao
 * @date 2018/1/20
 * @description
 * @email fengzb0216@sina.com
 */

@Data
public class FundsBean {

    /**
     * free : {"btc":"0","usd":"0","ltc":"0","eth":"0"}
     * freezed : {"btc":"0","usd":"0","ltc":"0","eth":"0"}
     */

    private Map<String,String> free;
    private Map<String,String> freezed;
}
