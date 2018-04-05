package com.leapord.supercoin.entity.http;

import java.io.Serializable;
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
public class LiveData implements Serializable {
    private List<double[]> kLineData;
    private List<double[]> subKlineData;
    private Depth depth;

    public LiveData(List<double[]> kLineData, Depth depth) {
        this.kLineData = kLineData;
        this.depth = depth;
    }
}
