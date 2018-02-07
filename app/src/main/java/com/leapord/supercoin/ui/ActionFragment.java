package com.leapord.supercoin.ui;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.alibaba.fastjson.JSON;
import com.leapord.supercoin.R;
import com.leapord.supercoin.app.Const;
import com.leapord.supercoin.core.TradeManager;
import com.leapord.supercoin.service.KeepAliveService;
import com.leapord.supercoin.service.LooperService;
import com.leapord.supercoin.util.CommonUtil;
import com.leapord.supercoin.util.SpUtils;
import com.leapord.supercoin.util.ToastUtis;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/1/30
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/

public class ActionFragment extends BaseFragment {


    @BindView(R.id.rb_btc)
    RadioButton rbBtc;
    @BindView(R.id.rb_eth)
    RadioButton rbEth;
    @BindView(R.id.rb_usdt)
    RadioButton rbUsdt;
    @BindView(R.id.rb_bch)
    RadioButton rbBch;
    @BindView(R.id.rg_trade_zone)
    RadioGroup rgTradeZone;
    @BindView(R.id.cb_of)
    CheckBox cbOf;
    @BindView(R.id.cb_light)
    CheckBox cbLight;
    @BindView(R.id.cb_swftc)
    CheckBox cbSwftc;
    @BindView(R.id.cb_show)
    CheckBox cbShow;
    @BindView(R.id.cb_btc)
    CheckBox cbBtc;
    @BindView(R.id.cb_eth)
    CheckBox cbEth;
    @BindView(R.id.cb_etc)
    CheckBox cbEtc;
    @BindView(R.id.cb_eos)
    CheckBox cbEos;
    @BindView(R.id.cb_3min)
    CheckBox cb3min;
    @BindView(R.id.cb_5min)
    CheckBox cb5min;
    @BindView(R.id.cb_15min)
    CheckBox cb15min;
    @BindView(R.id.cb_30min)
    CheckBox cb30min;
    @BindView(R.id.cb_1hour)
    CheckBox cb1hour;
    @BindView(R.id.cb_2hour)
    CheckBox cb2hour;
    @BindView(R.id.cb_4hour)
    CheckBox cb4hour;
    @BindView(R.id.cb_6hour)
    CheckBox cb6hour;
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
    @BindView(R.id.rg_period)
    RadioGroup rgPeriod;
    @BindView(R.id.checkbox)
    CheckBox checkbox;


    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void init(View rootView) {
        super.init(rootView);
        restoreUI();
    }

    private void restoreUI() {

        String symbol = SpUtils.getString(Const.SELECTED_SYMBOL, "");
        if (TextUtils.isEmpty(symbol)) {
            return;
        }
        List<String> symbols = JSON.parseArray(symbol, String.class);
        for (int i = 0; i < symbols.size(); i++) {
            String s = symbols.get(i);
            if (i == 0) {
                String zone = TradeManager.getCoinZone(s);
                setZone(zone);
            }
            String coinName = TradeManager.getCoinName(s);
            setName(coinName);
        }
        String ktime = SpUtils.getString(Const.SELECTED_KTIME, "");
        List<String> ktimes = JSON.parseArray(ktime, String.class);
        for (String s : ktimes) {
            setKTime(s);
        }

        String period = SpUtils.getString(Const.SELECTED_PERIOD, "");
        switch (period) {
            case "3":
                rb3.setChecked(true);
                break;
            case "5":
                rb5.setChecked(true);
                break;
            case "15":
                rb15.setChecked(true);
                break;
            case "30":
                rb30.setChecked(true);
                break;
            case "60":
                rb60.setChecked(true);
                break;

        }
        checkbox.setChecked(SpUtils.getBoolean(Const.AUTO_TRANSACTION, checkbox.isChecked()));
    }

    private void setKTime(String ktime) {
        switch (ktime) {
            case "3min":
                cb3min.setChecked(true);
                break;
            case "5min":
                cb5min.setChecked(true);
                break;
            case "15min":
                cb15min.setChecked(true);
                break;
            case "30min":
                cb30min.setChecked(true);
                break;
            case "1hour":
                cb1hour.setChecked(true);
                break;
            case "2hour":
                cb2hour.setChecked(true);
                break;
            case "4hour":
                cb4hour.setChecked(true);
                break;
            case "6hour":
                cb6hour.setChecked(true);
                break;
        }
    }

    private void setName(String coinName) {
        switch (coinName) {
            case "of":
                cbOf.setChecked(true);
                break;
            case "light":
                cbLight.setChecked(true);
                break;
            case "swftc":
                cbSwftc.setChecked(true);
                break;
            case "show":
                cbShow.setChecked(true);
                break;
            case "btc":
                cbBtc.setChecked(true);
                break;
            case "eth":
                cbEtc.setChecked(true);
                break;
            case "etc":
                cbEtc.setChecked(true);
                break;
            case "eos":
                cbEos.setChecked(true);
                break;
        }
    }

    private void setZone(String zone) {
        switch (zone) {
            case "btc":
                rbBtc.setChecked(true);
                break;
            case "eth":
                rbEth.setChecked(true);
                break;
            case "bch":
                rbBch.setChecked(true);
                break;
            case "usdt":
                rbUsdt.setChecked(true);
                break;
        }
    }

    public void startLooper() {
        Intent intent = new Intent(getContext(), LooperService.class);
        //币种类
        ArrayList<String> symbols = new ArrayList<>();
        String coinZone;
        switch (rgTradeZone.getCheckedRadioButtonId()) {
            case R.id.rb_btc:
                coinZone = "_btc";
                break;
            case R.id.rb_eth:
                coinZone = "_eth";
                break;
            case R.id.rb_bch:
                coinZone = "_bch";
                break;
            default:
                coinZone = "_usdt";
                break;
        }
        if (cbOf.isChecked()) {
            symbols.add(cbOf.getText().toString().toLowerCase() + coinZone);
        }
        if (cbLight.isChecked()) {
            symbols.add(cbLight.getText().toString().toLowerCase() + coinZone);
        }
        if (cbSwftc.isChecked()) {
            symbols.add(cbSwftc.getText().toString().toLowerCase() + coinZone);
        }
        if (cbShow.isChecked()) {
            symbols.add(cbShow.getText().toString().toLowerCase() + coinZone);
        }
        if (cbBtc.isChecked()) {
            symbols.add(cbBtc.getText().toString().toLowerCase() + coinZone);
        }
        if (cbEtc.isChecked()) {
            symbols.add(cbEtc.getText().toString().toLowerCase() + coinZone);
        }
        if (cbEos.isChecked()) {
            symbols.add(cbEos.getText().toString().toLowerCase() + coinZone);
        }
        if (cbEth.isChecked()) {
            symbols.add(cbEth.getText().toString().toLowerCase() + coinZone);
        }
        if (symbols.size() == 0) {
            ToastUtis.showToast("请选择币种");
            return;
        }
        intent.putStringArrayListExtra("SYMBOLS", symbols);
        SpUtils.putString(Const.SELECTED_SYMBOL, JSON.toJSONString(symbols));

        ArrayList<String> kTimes = new ArrayList<>();
        if (cb3min.isChecked()) {
            kTimes.add(cb3min.getText().toString().toLowerCase());
        }
        if (cb5min.isChecked()) {
            kTimes.add(cb5min.getText().toString().toLowerCase());
        }
        if (cb15min.isChecked()) {
            kTimes.add(cb15min.getText().toString().toLowerCase());
        }
        if (cb30min.isChecked()) {
            kTimes.add(cb30min.getText().toString().toLowerCase());
        }
        if (cb1hour.isChecked()) {
            kTimes.add(cb1hour.getText().toString().toLowerCase());
        }
        if (cb2hour.isChecked()) {
            kTimes.add(cb2hour.getText().toString().toLowerCase());
        }
        if (cb4hour.isChecked()) {
            kTimes.add(cb4hour.getText().toString().toLowerCase());
        }
        if (cb6hour.isChecked()) {
            kTimes.add(cb6hour.getText().toString().toLowerCase());
        }
        if (kTimes.size() < 2) {
            ToastUtis.showToast("请选择2组K线");
            return;
        }
        intent.putStringArrayListExtra("KTIMES", kTimes);
        SpUtils.putString(Const.SELECTED_KTIME, JSON.toJSONString(kTimes));

        int checkedRadioButtonId = rgPeriod.getCheckedRadioButtonId();
        String period = ((RadioButton) findViewById(checkedRadioButtonId)).getText().toString().toLowerCase();
        intent.putExtra("PERIOD", period);
        SpUtils.putString(Const.SELECTED_PERIOD, period);

        SpUtils.putBoolean(Const.AUTO_TRANSACTION, checkbox.isChecked());
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


    @OnClick({R.id.btn_start, R.id.btn_stop, R.id.btn_buy, R.id.btn_sell, R.id.btn_isrunning})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                startLooper();
                break;
            case R.id.btn_stop:
                stopLooper();
                break;
            case R.id.btn_buy:
//                TradeManager.purchase(SpUtils.getString(Const.SELECTED_SYMBOL, OkCoin.USDT.OF), TradeManager.WAREHOUSE.FULL);
                break;
            case R.id.btn_sell:
//                TradeManager.sellCoins(SpUtils.getString(Const.SELECTED_SYMBOL, OkCoin.USDT.OF), TradeManager.WAREHOUSE.FULL);
                break;
            case R.id.btn_isrunning:
                ToastUtis.showToast(CommonUtil.isServiceWork(getContext(), LooperService.class.getName()) ? "正在运行" : "已停止");
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
    }

}
