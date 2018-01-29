package com.leapord.supercoin.entity.http;

import lombok.Data;

/**
 * @author Biao
 * @date 2018/1/20
 * @description
 * @email fengzb0216@sina.com
 */
@Data
public class CancelTradeResp {

    /**
     * success : 123456,123457
     * error : 123458,123459
     */

    private String success;
    private String error;


}
