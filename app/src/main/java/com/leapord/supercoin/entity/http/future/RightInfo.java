package com.leapord.supercoin.entity.http.future;

import java.util.Map;

import lombok.Data;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/4/27
 *  Description 用户合约信息
 *  Email fengzhengbiao@vcard100.com
 **********************************************/
@Data
public class RightInfo {
    private Map<String, FutureInfo> info;
    private boolean result;
}
