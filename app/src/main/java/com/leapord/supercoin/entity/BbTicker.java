package com.leapord.supercoin.entity;

import lombok.Data;

/**
 * @author Biao
 * @date 2018/1/20
 * @description
 * @email fengzb0216@sina.com
 */
@Data
public class BbTicker {
  private String date;
  private BbTicker ticker;
}
