package com.leapord.supercoin.entity.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/1/26
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeCancleResp {
    private String success;
    private String error;
}
