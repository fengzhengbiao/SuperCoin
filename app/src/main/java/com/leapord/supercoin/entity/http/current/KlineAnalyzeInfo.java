package com.leapord.supercoin.entity.http.current;

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
    private double sellPrice;
    private double buyPrice;
    private String suggestionAction;

    private int tendency;     //-1:下降,0：稳定,1上升
    private long time;

}
