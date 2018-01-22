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

import com.leapord.supercoin.R;
import com.leapord.supercoin.entity.OkCoin;
import com.leapord.supercoin.service.KeepAliveService;
import com.leapord.supercoin.service.LooperService;

import java.util.ArrayList;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.rv_coins);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        startLooper();
//        getUserInfo();

    }


    public void startLooper() {
        Intent intent = new Intent(this, LooperService.class);
        ArrayList<String> symbols = new ArrayList<>();
        symbols.add(OkCoin.USDT.SWFTC);
        intent.putStringArrayListExtra("SYMBOLS", symbols);
        startService(intent);
        startService(new Intent(this, KeepAliveService.class));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            JobInfo jobInfo = new JobInfo.Builder(1, new ComponentName(getPackageName(), KeepAliveService.class.getName()))
                    .setPeriodic(2000)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build();
            jobScheduler.schedule(jobInfo);
        }
    }

}
