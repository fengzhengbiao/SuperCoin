package com.leapord.supercoin.ui;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;

import com.leapord.supercoin.R;
import com.leapord.supercoin.entity.http.OkCoin;
import com.leapord.supercoin.service.KeepAliveService;
import com.leapord.supercoin.service.LooperService;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_home:

                        return true;
                    case R.id.navigation_dashboard:

                        return true;
                    case R.id.navigation_notifications:

                        return true;
                }
                return false;
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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
    //                TradeManager.purchase(OkCoin.USDT.SWFTC, TradeManager.WAREHOUSE.HALF, new double[]{0.0070, 0.0069}, 2);
//                TradeManager.sellCoins(OkCoin.USDT.LIGHT,TradeManager.WAREHOUSE.HALF,new double[]{0.0070, 0.0069},2 );

}
