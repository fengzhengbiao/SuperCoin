package com.leapord.supercoin.ui;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.leapord.supercoin.R;
import com.leapord.supercoin.app.Const;
import com.leapord.supercoin.app.OkCoin;
import com.leapord.supercoin.service.KeepAliveService;
import com.leapord.supercoin.service.LooperService;
import com.leapord.supercoin.util.SpUtils;
import com.leapord.supercoin.util.ToastUtis;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/1/30
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/

public class ActionFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.checkbox)
    CheckBox checkBox;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void init(View rootView) {
        super.init(rootView);
        checkBox.setChecked(SpUtils.getBoolean(Const.AUTO_TRANSACTION, false));
        checkBox.setOnCheckedChangeListener(this);

    }


    public void startLooper() {
        Intent intent = new Intent(getContext(), LooperService.class);
        ArrayList<String> symbols = new ArrayList<>();
        symbols.add(OkCoin.USDT.OF);
        intent.putStringArrayListExtra("SYMBOLS", symbols);
        getActivity().startService(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler jobScheduler = (JobScheduler) getActivity().getSystemService(JOB_SCHEDULER_SERVICE);
            JobInfo jobInfo = new JobInfo.Builder(1, new ComponentName(getContext().getPackageName(), KeepAliveService.class.getName()))
                    .setPeriodic(2000)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build();
            jobScheduler.schedule(jobInfo);

        }
    }


    @OnClick({R.id.btn_start, R.id.btn_stop, R.id.btn_sell, R.id.btn_buy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                startLooper();
                break;
            case R.id.btn_stop:
                stopLooper();
                break;
            case R.id.btn_buy:
                break;
            case R.id.btn_sell:
                break;
        }
    }

    private void stopLooper() {
        Intent intent = new Intent(getContext(), LooperService.class);
        getActivity().stopService(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler jobScheduler = (JobScheduler) getActivity().getSystemService(JOB_SCHEDULER_SERVICE);
            jobScheduler.cancel(1);
        }
        checkBox.setChecked(false);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        SpUtils.putBoolean(Const.AUTO_TRANSACTION, isChecked);
        ToastUtis.showToast("自动交易已：" + (isChecked ? "打开" : "关闭"));
    }
}
