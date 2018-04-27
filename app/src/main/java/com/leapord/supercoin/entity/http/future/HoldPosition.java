package com.leapord.supercoin.entity.http.future;

import java.util.List;

import lombok.Data;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/4/27
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/
@Data
public class HoldPosition {
    private String force_liqu_price;
    private List<Holder> holding;
}
