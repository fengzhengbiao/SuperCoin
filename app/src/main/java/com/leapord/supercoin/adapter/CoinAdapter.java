package com.leapord.supercoin.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leapord.supercoin.R;
import com.leapord.supercoin.entity.http.current.KlineAnalyzeInfo;
import com.leapord.supercoin.util.CommonUtil;
import com.leapord.supercoin.util.TimeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Biao
 * @date 2018/1/20
 * @description
 * @email fengzb0216@sina.com
 */

public class CoinAdapter extends RecyclerView.Adapter<CoinAdapter.CoinHolder> {

    private List<KlineAnalyzeInfo> klineAnalyzeInfos;

    public void setKlineAnalyzeInfos(List<KlineAnalyzeInfo> klineAnalyzeInfos) {
        this.klineAnalyzeInfos = klineAnalyzeInfos;
        notifyDataSetChanged();
    }

    @Override
    public CoinHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_okcoin_selected, parent, false);
        return new CoinHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CoinHolder holder, int position) {
        KlineAnalyzeInfo klineAnalyzeInfo = klineAnalyzeInfos.get(position);
        holder.tvCoinName.setText(klineAnalyzeInfo.getCoinName());
        holder.tvBuy.setText(String.format("%.5f", klineAnalyzeInfo.getBuyPrice()));
        holder.tvSell.setText(String.format("%.5f", klineAnalyzeInfo.getSellPrice()));
        holder.tvDepth.setText(CommonUtil.getTendency(klineAnalyzeInfo.getTendency()));
        holder.tvDepth.setTextColor(Color.parseColor(klineAnalyzeInfo.getTendency() >= 0 ? "#45b10f" : "#d72d21"));
        holder.tvTime.setText("交易时间：" + TimeUtils.formatDate(klineAnalyzeInfo.getTime()));
    }

    @Override
    public int getItemCount() {
        return klineAnalyzeInfos == null ? 0 : klineAnalyzeInfos.size();
    }

    public class CoinHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_coin_name)
        TextView tvCoinName;
        @BindView(R.id.tv_current_buy)
        TextView tvBuy;
        @BindView(R.id.tv_current_sell)
        TextView tvSell;
        @BindView(R.id.tv_depth)
        TextView tvDepth;
        @BindView(R.id.tv_time)
        TextView tvTime;


        public CoinHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
