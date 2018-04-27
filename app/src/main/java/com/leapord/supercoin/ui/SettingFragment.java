package com.leapord.supercoin.ui;


import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.leapord.supercoin.R;
import com.leapord.supercoin.adapter.ActionPagerAdapter;

import butterknife.BindView;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/4/25
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/
public class SettingFragment extends BaseFragment {

    @BindView(R.id.tablayout)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void init(View rootView) {
        viewPager.setAdapter(new ActionPagerAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
    }
}
