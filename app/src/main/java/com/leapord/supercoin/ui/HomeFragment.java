package com.leapord.supercoin.ui;

import android.view.View;

import com.leapord.supercoin.R;
import com.leapord.supercoin.util.ToastUtis;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/1/30
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/

public class HomeFragment extends BaseFragment {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void init(View rootView) {
        super.init(rootView);

    }

    public void onViewClick(View view){
        ToastUtis.showToast("未实现");
    }



}
