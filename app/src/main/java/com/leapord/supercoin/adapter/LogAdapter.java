package com.leapord.supercoin.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/4/13
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/
public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogHolder> {



    @Override
    public LogHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(LogHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class LogHolder extends RecyclerView.ViewHolder{

        public LogHolder(View itemView) {
            super(itemView);
        }
    }
}
