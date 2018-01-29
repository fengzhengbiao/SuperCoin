package com.leapord.supercoin.ui;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.leapord.supercoin.R;
import com.leapord.supercoin.app.Const;
import com.leapord.supercoin.entity.http.OkCoin;
import com.leapord.supercoin.service.KeepAliveService;
import com.leapord.supercoin.service.LooperService;
import com.leapord.supercoin.util.SpUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.rv_coins);
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
        findViewById(R.id.btn_buy).setOnClickListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        startLooper();
    }


    public void startLooper() {
        Intent intent = new Intent(this, LooperService.class);
        ArrayList<String> symbols = new ArrayList<>();
        symbols.add(OkCoin.USDT.OF);
        intent.putStringArrayListExtra("SYMBOLS", symbols);
        startService(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            JobInfo jobInfo = new JobInfo.Builder(1, new ComponentName(getPackageName(), KeepAliveService.class.getName()))
                    .setPeriodic(2000)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build();
            jobScheduler.schedule(jobInfo);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                SpUtils.putBoolean(Const.AUTO_TRANSACTION, true);
                break;
            case R.id.btn_stop:
                SpUtils.putBoolean(Const.AUTO_TRANSACTION, false);
                break;
            case R.id.btn_buy:
//                TradeManager.purchase(OkCoin.USDT.SWFTC, TradeManager.WAREHOUSE.HALF, new double[]{0.0070, 0.0069}, 2);
//                TradeManager.sellCoins(OkCoin.USDT.LIGHT,TradeManager.WAREHOUSE.HALF,new double[]{0.0070, 0.0069},2 );
                break;
        }
    }
}
