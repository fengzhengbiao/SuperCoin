package com.leapord.supercoin.ui;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.leapord.supercoin.R;
import com.leapord.supercoin.app.Const;
import com.leapord.supercoin.app.OkCoin;
import com.leapord.supercoin.core.TradeManager;
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

public class ActionFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener {

    @BindView(R.id.checkbox)
    CheckBox checkBox;
    @BindView(R.id.radiogroup)
    RadioGroup radioGroup;
    @BindView(R.id.rb_T)
    RadioButton rbT;
    @BindView(R.id.rb_period)
    RadioButton rbPeriod;
    @BindView(R.id.radiogroup2)
    RadioGroup radioGroup2;
    @BindView(R.id.rb_depth)
    RadioButton rbDepth;
    @BindView(R.id.rb_kline)
    RadioButton rbKline;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void init(View rootView) {
        super.init(rootView);
        checkBox.setChecked(SpUtils.getBoolean(Const.AUTO_TRANSACTION, false));
        checkBox.setOnCheckedChangeListener(this);
        radioGroup.setOnCheckedChangeListener(this);
        radioGroup2.setOnCheckedChangeListener(this);
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


    @OnClick({R.id.btn_start, R.id.btn_stop, R.id.btn_add, R.id.btn_remove})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                startLooper();
                break;
            case R.id.btn_stop:
                stopLooper();
                break;
            case R.id.btn_add:
            case R.id.btn_remove:
                ToastUtis.showToast("开发中");
                break;
        }
    }

    private void stopLooper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler jobScheduler = (JobScheduler) getActivity().getSystemService(JOB_SCHEDULER_SERVICE);
            jobScheduler.cancel(1);
        }
        Intent intent = new Intent(getContext(), LooperService.class);
        getActivity().stopService(intent);
        checkBox.setChecked(false);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        SpUtils.putBoolean(Const.AUTO_TRANSACTION, isChecked);
        ToastUtis.showToast("自动交易已：" + (isChecked ? "打开" : "关闭"));
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()) {
            case R.id.radiogroup:
                Intent intent = new Intent(getContext(), LooperService.class);
                switch (checkedId) {
                    case R.id.rb_period:
                        intent.putExtra("TRADE_TYPE", OkCoin.TradeType.P_PERIOD);
                        break;
                    case R.id.rb_T:
                        intent.putExtra("TRADE_TYPE", OkCoin.TradeType.T_THORT);
                        break;
                }
                getActivity().startService(intent);
                break;
            case R.id.radiogroup2:
                switch (checkedId) {
                    case R.id.rb_depth:
                        TradeManager.settMode(2);
                        break;
                    case R.id.rb_kline:
                        TradeManager.settMode(1);
                        break;
                }
                break;

        }
    }
}
