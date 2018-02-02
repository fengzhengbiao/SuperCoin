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
    @BindView(R.id.radiogroup_strategy)
    RadioGroup radioGroup;
    @BindView(R.id.rb_T)
    RadioButton rbT;
    @BindView(R.id.rb_period)
    RadioButton rbPeriod;
    @BindView(R.id.radiogroup_previous)
    RadioGroup radioGroup2;
    @BindView(R.id.rb_depth)
    RadioButton rbDepth;
    @BindView(R.id.rb_kline)
    RadioButton rbKline;
    @BindView(R.id.radiogroup_period)
    RadioGroup radioGroup3;
    @BindView(R.id.radiogroup_coin)
    RadioGroup radioGroupCoin;

    @BindView(R.id.rb_of)
    RadioButton rbOf;
    @BindView(R.id.rb_light)
    RadioButton rbLight;
    @BindView(R.id.rb_swftc)
    RadioButton rbSwftc;

    @BindView(R.id.rb_3)
    RadioButton rb3;
    @BindView(R.id.rb_5)
    RadioButton rb5;
    @BindView(R.id.rb_15)
    RadioButton rb15;
    @BindView(R.id.rb_30)
    RadioButton rb30;
    @BindView(R.id.rb_60)
    RadioButton rb60;


    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void init(View rootView) {
        super.init(rootView);
        checkBox.setOnCheckedChangeListener(this);
        radioGroupCoin.setOnCheckedChangeListener(this);
        radioGroup.setOnCheckedChangeListener(this);
        radioGroup2.setOnCheckedChangeListener(this);
        radioGroup3.setOnCheckedChangeListener(this);
        restoreUI();
    }

    private void restoreUI() {
        checkBox.setChecked(SpUtils.getBoolean(Const.AUTO_TRANSACTION, false));
        String symbol = SpUtils.getString(Const.SELECTED_SYMBOL, OkCoin.USDT.OF);
        switch (symbol) {
            case OkCoin.USDT.OF:
                rbOf.setChecked(true);
                break;
            case OkCoin.USDT.LIGHT:
                rbLight.setChecked(true);
                break;
            case OkCoin.USDT.SWFTC:
                rbSwftc.setChecked(true);
                break;
        }
        int stragy = SpUtils.getInt(Const.SELECTED_STRATEGY, OkCoin.TradeType.T_THORT);
        switch (stragy) {
            case OkCoin.TradeType.T_THORT:
                rbT.setChecked(true);
                break;
            case OkCoin.TradeType.P_PERIOD:
                rbPeriod.setChecked(true);
                break;
        }
        String period = SpUtils.getString(Const.SELECTED_PERIOD, OkCoin.TimePeriod.THREE_MIN);
        switch (period) {
            case OkCoin.TimePeriod.THREE_MIN:
                rb3.setChecked(true);
                break;
            case OkCoin.TimePeriod.FIVE_MIN:
                rb5.setChecked(true);
                break;
            case OkCoin.TimePeriod.FIFTEEN_MIN:
                rb15.setChecked(true);
                break;
            case OkCoin.TimePeriod.THITY_MIN:
                rb30.setChecked(true);
                break;
            case OkCoin.TimePeriod.ONE_HOUR:
                rb60.setChecked(true);
                break;
        }
    }

    public void startLooper() {

        Intent intent = new Intent(getContext(), LooperService.class);
        //币种类
        ArrayList<String> symbols = new ArrayList<>();
        int coinRb = radioGroupCoin.getCheckedRadioButtonId();
        switch (coinRb) {
            case R.id.rb_of:
                symbols.add(OkCoin.USDT.OF);
                SpUtils.putString(Const.SELECTED_SYMBOL, OkCoin.USDT.OF);
                break;
            case R.id.rb_light:
                symbols.add(OkCoin.USDT.LIGHT);
                SpUtils.putString(Const.SELECTED_SYMBOL, OkCoin.USDT.LIGHT);
                break;
            case R.id.rb_swftc:
                symbols.add(OkCoin.USDT.SWFTC);
                SpUtils.putString(Const.SELECTED_SYMBOL, OkCoin.USDT.SWFTC);
                break;
        }
        intent.putStringArrayListExtra("SYMBOLS", symbols);

        //策略
        int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        int strategy = checkedRadioButtonId == R.id.rb_T ? OkCoin.TradeType.T_THORT : OkCoin.TradeType.P_PERIOD;
        intent.putExtra("TRADE_TYPE", checkedRadioButtonId == R.id.rb_T ? OkCoin.TradeType.T_THORT : OkCoin.TradeType.P_PERIOD);
        SpUtils.putInt(Const.SELECTED_STRATEGY, strategy);

        //优先级
        int radioButtonId = radioGroup2.getCheckedRadioButtonId();
        int previous = radioButtonId == R.id.rb_depth ? 1 : 2;
        TradeManager.settMode(previous);
        SpUtils.putInt(Const.SELECTED_PREVIOUS, previous);

        //波长
        switch (radioGroup3.getCheckedRadioButtonId()) {
            case R.id.rb_3:
                intent.putExtra("PERIOD", OkCoin.TimePeriod.THREE_MIN);
                SpUtils.putString(Const.SELECTED_PERIOD, OkCoin.TimePeriod.THREE_MIN);
                break;
            case R.id.rb_5:
                intent.putExtra("PERIOD", OkCoin.TimePeriod.FIVE_MIN);
                SpUtils.putString(Const.SELECTED_PERIOD, OkCoin.TimePeriod.FIVE_MIN);
                break;
            case R.id.rb_15:
                intent.putExtra("PERIOD", OkCoin.TimePeriod.FIFTEEN_MIN);
                SpUtils.putString(Const.SELECTED_PERIOD, OkCoin.TimePeriod.THITY_MIN);
                break;
            case R.id.rb_30:
                intent.putExtra("PERIOD", OkCoin.TimePeriod.THITY_MIN);
                SpUtils.putString(Const.SELECTED_PERIOD, OkCoin.TimePeriod.THITY_MIN);
                break;
            case R.id.rb_60:
                intent.putExtra("PERIOD", OkCoin.TimePeriod.ONE_HOUR);
                SpUtils.putString(Const.SELECTED_PERIOD, OkCoin.TimePeriod.ONE_HOUR);
                break;
        }


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


    @OnClick({R.id.btn_start, R.id.btn_stop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                startLooper();
                break;
            case R.id.btn_stop:
                stopLooper();
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
            case R.id.radiogroup_strategy:
                switch (checkedId) {
                    case R.id.rb_period:
                        break;
                    case R.id.rb_T:
                        break;
                }
                break;
            case R.id.radiogroup_previous:
                switch (checkedId) {
                    case R.id.rb_depth:

                        break;
                    case R.id.rb_kline:

                        break;
                }
                break;
            case R.id.radiogroup_period:
                switch (checkedId) {
                    case R.id.rb_15:
                        break;
                    case R.id.rb_30:
                        break;
                    case R.id.rb_60:
                        break;
                }
                break;

        }
    }
}
