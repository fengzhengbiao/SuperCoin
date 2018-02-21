package com.leapord.supercoin.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leapord.supercoin.R;
import com.leapord.supercoin.entity.dao.Trade;
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

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.CoinHolder> {

    private List<Trade> trades;

    public void setTrades(List<Trade> trades) {
        this.trades = trades;
        notifyDataSetChanged();
    }

    @Override
    public CoinHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new CoinHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CoinHolder holder, int position) {
        Trade trade = trades.get(position);
        holder.tvCoinName.setText(trade.getSymbol());
        holder.tvAmount.setText(trade.getAmount());
        holder.tvPrice.setText(trade.getPrice());
        holder.tvAmount.setBackgroundColor(Color.parseColor(trade.getStatus() ? "#45b10f" : "#d72d21"));
        holder.tvPrice.setBackgroundColor(Color.parseColor(trade.getSellType().contains("buy") ? "#45b10f" : "#d72d21"));
        holder.tvTime.setText(TimeUtils.formatDate(trade.getCreateTime()));
    }

    @Override
    public int getItemCount() {
        return trades == null ? 0 : trades.size();
    }

    public class CoinHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_name)
        TextView tvCoinName;
        @BindView(R.id.tv_amount)
        TextView tvAmount;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.tv_time)
        TextView tvTime;

        public CoinHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
