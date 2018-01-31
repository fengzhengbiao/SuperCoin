package com.leapord.supercoin.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * * Created by JokerFish on 17/07/11
 *
 * @author JokerFish
 */

public abstract class
BaseFragment extends Fragment {

    protected final String TAG = this.getClass().getSimpleName();
    protected View mRootView;

    /**
     * 返回fragment的布局资源ID
     *
     * @return
     */
    protected abstract int getLayoutResourceId();

    /**
     * 初始化操作
     */
    protected  void init(View rootView){};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(getLayoutResourceId(), null);
            ButterKnife.bind(this, mRootView);
            init(mRootView);
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    protected View findViewById(int resId) {
        return mRootView.findViewById(resId);
    }





    /**
     * 当前碎片是否在有效生命周期内
     */
    public boolean isInLifeCycle() {
        return isAdded() && null != getActivity() && !getActivity().isFinishing();
    }
}
