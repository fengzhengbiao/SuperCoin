package com.leapord.supercoin.ui;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.leapord.supercoin.R;
import com.leapord.supercoin.adapter.HistoryAdapter;
import com.leapord.supercoin.app.CoinApplication;
import com.leapord.supercoin.entity.dao.Trade;
import com.leapord.supercoin.entity.dao.TradeDao;
import com.leapord.supercoin.entity.event.TradeChangeEvent;
import com.leapord.supercoin.util.ToastUtis;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/1/30
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/

public class HistoryFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.srl)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    private HistoryAdapter historyAdapter;


    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_history;
    }


    @Override
    protected void init(View rootView) {
        super.init(rootView);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));
        historyAdapter = new HistoryAdapter();
        mRecyclerView.setAdapter(historyAdapter);
        onRefresh();
    }

    @Override
    public void onRefresh() {
        Log.i(TAG, "onRefresh: ");
        Observable.create((ObservableOnSubscribe<List<Trade>>) observableEmitter -> {
            TradeDao tradeDao = CoinApplication.INSTANCE.getDaoSession().getTradeDao();
            observableEmitter.onNext(tradeDao.queryBuilder().build().list());
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trades -> {
                    mSwipeRefreshLayout.setRefreshing(false);
                    if (trades.size() == 0) {
                        ToastUtis.showToast("还没有任何历史记录");
                    } else {
                        historyAdapter.setTrades(trades);
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TradeChangeEvent event) {
        onRefresh();
    }
}
