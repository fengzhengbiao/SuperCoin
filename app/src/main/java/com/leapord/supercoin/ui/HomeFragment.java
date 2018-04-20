package com.leapord.supercoin.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.leapord.supercoin.R;
import com.leapord.supercoin.adapter.LogAdapter;
import com.leapord.supercoin.entity.event.LogEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/1/30
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/

public class HomeFragment extends BaseFragment {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    private LogAdapter adapter;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_home_v2;
    }

    @Override
    protected void init(View rootView) {
        super.init(rootView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LogAdapter();
        recyclerView.setAdapter(adapter);
        EventBus.getDefault().register(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LogEvent event) {
        adapter.updateEvents(event);
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
