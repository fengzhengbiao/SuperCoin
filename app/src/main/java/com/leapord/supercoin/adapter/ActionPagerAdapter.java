package com.leapord.supercoin.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.leapord.supercoin.ui.ActionFragment;
import com.leapord.supercoin.ui.FutureFragment;

import java.util.ArrayList;
import java.util.List;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/4/25
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/
public class ActionPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    public ActionPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
        fragments.add(new ActionFragment());
        fragments.add(new FutureFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return position == 0 ? "币币" : "合约";
    }
}
