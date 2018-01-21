package com.leapord.supercoin.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.leapord.supercoin.service.LooperService;
import com.leapord.supercoin.util.CommonUtil;

/**
 * @author Biao
 * @date 2018/1/20
 * @description
 * @email fengzb0216@sina.com
 */

public class ReactiveReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean serviceWork = CommonUtil.isServiceWork(context, LooperService.class.getName());
        if (!serviceWork) {
            Intent looperIntent = new Intent(context, LooperService.class);
            context.startService(looperIntent);
        }
    }
}
