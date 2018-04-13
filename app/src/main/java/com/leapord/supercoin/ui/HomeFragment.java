package com.leapord.supercoin.ui;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.leapord.supercoin.R;
import com.leapord.supercoin.util.ToastUtis;

import butterknife.BindView;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/1/30
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/

public class HomeFragment extends BaseFragment {
    @BindView(R.id.swiperefreshlayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;


    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_home_v2;
    }

    @Override
    protected void init(View rootView) {
        super.init(rootView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.setAdapter();

    }

    public void onViewClick(View view) {
        ToastUtis.showToast("未实现");
    }


}
