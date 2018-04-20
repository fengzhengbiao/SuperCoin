package com.leapord.supercoin.util;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.leapord.supercoin.entity.event.LogEvent;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/4/20
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/
public class StringUtil {


    public static SpannableString tintColor(LogEvent event) {
        int color;
        switch (event.level) {
            case 1:
                color = Color.parseColor("#45b10f");
                break;
            case 2:
                color = Color.parseColor("#4174da");
                break;
            case 3:
                color = Color.parseColor("#d72d21");
                break;
            default:
                color = Color.parseColor("#3d3939");
                break;
        }
        return createColorSapn(event.msg, color);
    }


    public static SpannableString createColorSapn(String textString, int color) {
        if (TextUtils.isEmpty(textString)) {
            throw new RuntimeException("The text can not be null");
        } else {
            SpannableString ss = new SpannableString(textString);
            ss.setSpan(new ForegroundColorSpan(color), 0, textString.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return ss;
        }
    }
}
