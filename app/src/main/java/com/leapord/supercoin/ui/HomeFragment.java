package com.leapord.supercoin.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.leapord.supercoin.R;
import com.leapord.supercoin.adapter.CoinAdapter;
import com.leapord.supercoin.core.Analyzer;
import com.leapord.supercoin.entity.http.KlineAnalyzeInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/1/30
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/

public class HomeFragment extends BaseFragment {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    private CoinAdapter coinAdapter;
    List<KlineAnalyzeInfo> infoList;


    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void init(View rootView) {
        super.init(rootView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        coinAdapter = new CoinAdapter();
        mRecyclerView.setAdapter(coinAdapter);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(KlineAnalyzeInfo info) {
        if (infoList == null) {
            infoList = new ArrayList<>();
            infoList.add(info);
            coinAdapter.setKlineAnalyzeInfos(infoList);
        } else {
            for (int i = 0; i < infoList.size(); i++) {
                KlineAnalyzeInfo klineAnalyzeInfo = infoList.get(i);
                if (TextUtils.equals(klineAnalyzeInfo.getCoinName(), info.getCoinName())) {
                    boolean timeValid = Analyzer.isTimeValid(info.getTime());
                    klineAnalyzeInfo.setBuyPrice(info.getBuyPrice());
                    klineAnalyzeInfo.setSellPrice(info.getSellPrice());
                    klineAnalyzeInfo.setTendency(info.getTendency());
                    if (timeValid) {
                        klineAnalyzeInfo.setTime(info.getTime());
                    }
                    Log.i(TAG, " 数据更新：" + info.getCoinName());
                    return;
                }
            }
            infoList.add(info);
            coinAdapter.notifyDataSetChanged();
            Log.i(TAG, "新增  " + info.getCoinName());
        }
    }

    ;

}
