package com.leapord.supercoin.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/1/22
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LiveData {
    private List<List<Double>> kLineData;
    private Depth depth;
}
