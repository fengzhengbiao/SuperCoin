package com.leapord.supercoin.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leapord.supercoin.R;
import com.leapord.supercoin.entity.http.KlineAnalyzeInfo;

import java.util.List;

/**
 * @author Biao
 * @date 2018/1/20
 * @description
 * @email fengzb0216@sina.com
 */

public class CoinAdapter extends RecyclerView.Adapter<CoinAdapter.CoinHolder> {

    private List<KlineAnalyzeInfo> klineAnalyzeInfos;

    @Override
    public CoinHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_okcoin_selected, parent);
        return new CoinHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CoinHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return klineAnalyzeInfos == null ? 0 : klineAnalyzeInfos.size();
    }

    public class CoinHolder extends RecyclerView.ViewHolder {

        TextView tvCoinName, tvCurrentBuy, tvCurrentSell, tvSuggestionAction, tvIncrease, tvDecline, tvIncreaseTime2, tvDecline2;

        public CoinHolder(View itemView) {
            super(itemView);


        }
    }
}
