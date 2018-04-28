package com.leapord.supercoin.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.alibaba.fastjson.JSON;
import com.leapord.supercoin.R;
import com.leapord.supercoin.app.Const;
import com.leapord.supercoin.app.OkCoin;
import com.leapord.supercoin.core.TradeManager;
import com.leapord.supercoin.service.FutureService;
import com.leapord.supercoin.util.SpUtils;
import com.leapord.supercoin.util.ToastUtis;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/4/27
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/
public class FutureFragment extends BaseFragment {
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
    @BindView(R.id.cb_btc)
    CheckBox cbBtc;
    @BindView(R.id.cb_ltc)
    CheckBox cbLtc;
    @BindView(R.id.cb_eth)
    CheckBox cbEth;
    @BindView(R.id.cb_etc)
    CheckBox cbEtc;
    @BindView(R.id.cb_bch)
    CheckBox cbBch;
    @BindView(R.id.cb_xpr)
    CheckBox cbXpr;
    @BindView(R.id.cb_eos)
    CheckBox cbEos;
    @BindView(R.id.cb_btg)
    CheckBox cbBtg;
    @BindView(R.id.rb_5)
    RadioButton rb5;
    @BindView(R.id.rb_10)
    RadioButton rb10;
    @BindView(R.id.rb_15)
    RadioButton rb15;
    @BindView(R.id.rb_30)
    RadioButton rb30;
    @BindView(R.id.rb_60)
    RadioButton rb60;
    @BindView(R.id.rg_period)
    RadioGroup rgPeriod;
    @BindView(R.id.cb_1min)
    CheckBox cb1min;
    @BindView(R.id.cb_3min)
    CheckBox cb3min;
    @BindView(R.id.cb_5min)
    CheckBox cb5min;
    @BindView(R.id.cb_15min)
    CheckBox cb15min;
    @BindView(R.id.et_buy_price)
    EditText etBuyPrice;
    @BindView(R.id.et_min_price)
    EditText etMinPrice;
    @BindView(R.id.et_max_price)
    EditText etMaxPrice;
    @BindView(R.id.cb_auto)
    CheckBox cbAuto;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_future;
    }


    @Override
    protected void init(View rootView) {
        restoreUI();
    }

    private void restoreUI() {
        String period = SpUtils.getString(Const.FUTURE_PERIOD, "");
        if (!TextUtils.isEmpty(period)) {
            switch (period) {
                case "5":
                    rb5.setChecked(true);
                    break;
                case "10":
                    rb10.setChecked(true);
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
        }
        String kline = SpUtils.getString(Const.FUTURE_KLINE, "");
        if (!TextUtils.isEmpty(kline)) {

            switch (kline) {
                case "15min":
                    cb15min.setChecked(true);
                    break;
                case "5min":
                    cb15min.setChecked(true);
                    break;
                case "3min":
                    cb15min.setChecked(true);
                    break;
                case "1min":
                    cb15min.setChecked(true);
                    break;
            }

        }
        String symbol = SpUtils.getString(Const.FUTURE_SYMBOLS, "");
        rbUsdt.setChecked(true);

    }

    @OnClick({R.id.btn_buy_increase, R.id.btn_buy_decrease, R.id.btn_close, R.id.btn_start, R.id.btn_stop})
    public void onViewClicked(View view) {
        int checkedRadioButtonId = rgTradeZone.getCheckedRadioButtonId();
        String zone = getCoinZone(checkedRadioButtonId);
        ArrayList<String> selectedSymbol = getSelectedSymbol(zone);
        if (TextUtils.isEmpty(zone)) {
            ToastUtis.showToast("请选择交易区");
        }
        if (selectedSymbol.size() == 0) {
            ToastUtis.showToast("请选择币种");
            return;
        }
        switch (view.getId()) {
            case R.id.btn_buy_increase:
                for (String s : selectedSymbol) {
                    TradeManager.openTrade(s, OkCoin.FUTURE_TYPE.OPEN_INCREASE);
                }

                break;
            case R.id.btn_buy_decrease:
                for (String s : selectedSymbol) {
                    TradeManager.openTrade(s , OkCoin.FUTURE_TYPE.OPEN_DECREASE);
                }
                break;
            case R.id.btn_close:
                for (String s : selectedSymbol) {
                    TradeManager.closeTrade(s,OkCoin.FUTURE_TYPE.CLOSE_INCREASE);
                }
                break;
            case R.id.btn_start:
                String period = "10";
                switch (rgPeriod.getCheckedRadioButtonId()) {
                    case R.id.rb_5:
                        period = "5";
                        break;
                    case R.id.rb_10:
                        period = "10";
                        break;
                    case R.id.rb_15:
                        period = "15";
                        break;
                    case R.id.rb_30:
                        period = "30";
                        break;
                    case R.id.rb_60:
                        period = "60";
                        break;
                }
                String kline = "1min";
                if (cb15min.isChecked()) {
                    kline = "15min";
                }
                if (cb5min.isChecked()) {
                    kline = "5min";
                }
                if (cb3min.isChecked()) {
                    kline = "3min";
                }
                if (cb1min.isChecked()) {
                    kline = "1min";
                }
                SpUtils.putString(Const.FUTURE_PERIOD, period);
                SpUtils.putString(Const.FUTURE_KLINE, kline);
                SpUtils.putString(Const.FUTURE_ZONE, zone);
                SpUtils.putString(Const.FUTURE_SYMBOLS, JSON.toJSONString(selectedSymbol));
                FutureService.start(getContext(), selectedSymbol, period, kline, zone);
                break;
            case R.id.btn_stop:
                Intent intent = new Intent(getContext(), FutureService.class);
                getContext().stopService(intent);
                break;
        }
    }

    @NonNull
    private String getCoinZone(int checkedRadioButtonId) {
        String zone = "usd";
        switch (checkedRadioButtonId) {
            case R.id.rb_btc:
                zone = rbBtc.getText().toString().toLowerCase().trim();
                break;
            case R.id.rb_bch:
                zone = rbBch.getText().toString().toLowerCase().trim();
                break;
            case R.id.rb_usdt:
                zone = rbUsdt.getText().toString().toLowerCase().trim();
                break;
            case R.id.rb_eth:
                zone = rbEth.getText().toString().toLowerCase().trim();
                break;
        }
        return "_" + zone;
    }

    private ArrayList<String> getSelectedSymbol(String zone) {
        ArrayList<String> SYMBOLS = new ArrayList<>();
        if (cbBtc.isChecked()) {
            SYMBOLS.add(cbBtc.getText().toString().toLowerCase() + zone);
        }
        if (cbXpr.isChecked()) {
            SYMBOLS.add(cbXpr.getText().toString().toLowerCase() + zone);
        }
        if (cbEos.isChecked()) {
            SYMBOLS.add(cbEos.getText().toString().toLowerCase() + zone);
        }
        if (cbEtc.isChecked()) {
            SYMBOLS.add(cbEtc.getText().toString().toLowerCase() + zone);
        }
        return SYMBOLS;
    }
}
