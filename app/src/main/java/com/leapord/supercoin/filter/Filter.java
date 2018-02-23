package com.leapord.supercoin.filter;

import com.leapord.supercoin.entity.event.Analysis;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/2/11
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/

public interface Filter {

    Analysis intercept(Analysis value,boolean isPurchaseSync,boolean isSellSync);

}
