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
import com.leapord.supercoin.entity.UserInfo;
import com.leapord.supercoin.network.HttpUtil;
import com.leapord.supercoin.service.KeepAliveService;
import com.leapord.supercoin.service.LooperService;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.rv_coins);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        startLooper();
        getUserInfo();

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

    public void getUserInfo(){
        HttpUtil.createRequest()
                .fetchUserInfo(OkCoin.API.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UserInfo value) {
                        Logger.d("onNext");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.d("onError");
                    }

                    @Override
                    public void onComplete() {
                        Logger.d("onComplete");

                    }
                });
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
}
