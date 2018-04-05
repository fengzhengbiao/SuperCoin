package com.leapord.supercoin.entity.http;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Biao
 * @version V1.0
 * @data 2018/4/5
 * @email sialovevoice@gmail.com
 */
@Data
@AllArgsConstructor
public class UserWithDepth {
    private UserInfo userInfo;
    private Depth depth;
}
