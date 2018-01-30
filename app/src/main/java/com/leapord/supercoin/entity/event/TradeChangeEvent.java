package com.leapord.supercoin.entity.event;

import lombok.AllArgsConstructor;
import lombok.Data;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/1/30
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/
@Data
@AllArgsConstructor
public class TradeChangeEvent {

    private String tradeType;
    private String symbol;

}
