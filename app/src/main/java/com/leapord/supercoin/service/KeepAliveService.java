package com.leapord.supercoin.service;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.leapord.supercoin.app.Const;
import com.leapord.supercoin.app.OkCoin;
import com.leapord.supercoin.core.TradeManager;
import com.leapord.supercoin.util.CommonUtil;
import com.leapord.supercoin.util.SpUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

/**
 * @author Biao
 * @date 2018/1/20
 * @description
 * @email fengzb0216@sina.com
 */

public class KeepAliveService extends JobService {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("MyJobDaemonService", "jobService启动");
        scheduleJob(getJobInfo());
        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i("MyJobDaemonService", "执行了onStartJob方法");
        boolean serviceWork = CommonUtil.isServiceWork(this, LooperService.class.getName());
        if (!serviceWork) {
            Intent intent = new Intent(this, LooperService.class);
            //币种类
            ArrayList<String> symbols = new ArrayList<>();
            SpUtils.getString(Const.SELECTED_SYMBOL, OkCoin.USDT.OF);
            intent.putStringArrayListExtra("SYMBOLS", symbols);
            //策略
            int strategy = SpUtils.getInt(Const.SELECTED_STRATEGY, OkCoin.TradeType.T_THORT);
            intent.putExtra("TRADE_TYPE", strategy);

            //优先级
            int previous = SpUtils.getInt(Const.SELECTED_PREVIOUS, 1);
            TradeManager.settMode(previous);
            String period = SpUtils.getString(Const.SELECTED_PERIOD, OkCoin.TimePeriod.THREE_MIN);
            intent.putExtra("PERIOD", period);

            this.startService(intent);
            Logger.d("轮询服务重启");
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("MyJobDaemonService", "执行了onStopJob方法");
        scheduleJob(getJobInfo());
        return true;
    }

    //将任务作业发送到作业调度中去
    public void scheduleJob(JobInfo t) {
        Log.i("MyJobDaemonService", "调度job");
        JobScheduler tm =
                (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.schedule(t);
    }

    public JobInfo getJobInfo() {
        JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(this, KeepAliveService.class));
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
        builder.setPersisted(true);
        builder.setRequiresCharging(false);
        builder.setRequiresDeviceIdle(false);
        //间隔1000毫秒
        builder.setPeriodic(1000);
        return builder.build();
    }

}
