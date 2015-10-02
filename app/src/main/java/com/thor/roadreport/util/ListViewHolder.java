package com.thor.roadreport.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView tvName;

    public ListViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }
}