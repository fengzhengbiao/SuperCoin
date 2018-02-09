package com.leapord.supercoin.entity.event;

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
public class OrderEvent {
    private double amount;
    private double price;
}
