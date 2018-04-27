package com.leapord.supercoin.entity.http.future;

import com.leapord.supercoin.entity.http.Depth;

import lombok.AllArgsConstructor;
import lombok.Data;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/4/27
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/
@Data
@AllArgsConstructor
public class RightWithDepth {
    private RightInfo rightInfo;
    private Depth depth;
}
