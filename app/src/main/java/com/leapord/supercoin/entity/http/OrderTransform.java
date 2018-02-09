package com.leapord.supercoin.entity.http;

import com.leapord.supercoin.entity.event.OrderEvent;

import lombok.AllArgsConstructor;
import lombok.Data;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/2/8
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/
@Data
@AllArgsConstructor
public class OrderTransform {
    private OrderEvent event;
    private TradeResponse response;
}
