package com.leapord.supercoin.entity;

import lombok.Data;

/**
 * @author Biao
 * @date 2018/1/20
 * @description
 * @email fengzb0216@sina.com
 */
@Data
public class Trade {

    /**
     * date : 1367130137
     * date_ms : 1367130137000
     * price : 787.71
     * amount : 0.003
     * tid : 230433
     * type : sell
     */

    private String date;
    private String date_ms;
    private float price;
    private float amount;
    private String tid;
    private String type;


}
