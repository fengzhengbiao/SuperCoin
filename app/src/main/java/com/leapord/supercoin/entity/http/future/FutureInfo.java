package com.leapord.supercoin.entity.http.future;

import lombok.Data;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/4/27
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/
@Data
public class FutureInfo {

    /**
     * account_rights:账户权益
     * keep_deposit：保证金
     * profit_real：已实现盈亏
     * profit_unreal：未实现盈亏
     * risk_rate：保证金率
     */

    private double account_rights;
    private double keep_deposit;
    private double profit_real;
    private double profit_unreal;
    private double risk_rate;


}
