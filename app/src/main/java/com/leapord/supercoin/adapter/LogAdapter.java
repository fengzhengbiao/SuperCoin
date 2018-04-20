package com.leapord.supercoin.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leapord.supercoin.R;
import com.leapord.supercoin.entity.event.LogEvent;
import com.leapord.supercoin.util.StringUtil;
import com.leapord.supercoin.util.TimeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/4/13
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/
public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogHolder> {


    private List<LogEvent> logEvents = new ArrayList<>();

    public void updateEvents(LogEvent logEvent) {
        this.logEvents.add(0,logEvent);
        if (this.logEvents.size() > 200) {
            Collections.reverse(logEvents);
            Iterator<LogEvent> iterator = this.logEvents.iterator();
            for (int i = 0; i < 100; i++) {
                if (iterator.hasNext()) {
                    iterator.remove();
                }
            }
            Collections.reverse(logEvents);
            notifyDataSetChanged();
        } else {
            notifyItemInserted(0);
        }
    }

    @Override
    public LogHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LogHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log, parent, false));
    }

    @Override
    public void onBindViewHolder(LogHolder holder, int position) {
        LogEvent logEvent = logEvents.get(position);
        holder.tvLog.setText(StringUtil.tintColor(logEvent));
        holder.tvTime.setText(TimeUtils.formatDate(logEvent.time));
    }

    @Override
    public int getItemCount() {
        return logEvents==null?0:logEvents.size();
    }

    class LogHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_log)
        TextView tvLog;


        public LogHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
