package com.leapord.supercoin.entity.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Biao
 * @date 2018/1/20
 * @description
 * @email fengzb0216@sina.com
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class KlineAnalyzeInfo {

    private String coinName;
    private float sellPrice;
    private float buyPrice;
    private String suggestionAction;

    private float tendency;     //-1:下降,0：稳定,1上升

    private Long firstOrderIncreaseTime;        // 一阶上升时间
    private Long firstOrderDecreaseTime;        //一阶下降时间
    private Long secondOrderIncreaseTime;       //二阶上升时间
    private Long secondOrderDecreaseTime;       //二阶下降时间
}
