package com.thor.roadreport.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thor.roadreport.R;
import com.thor.roadreport.model.ListItem;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    List<ListItem> feedItemList;
    Context context;
    OnClickItemOnList onClickItemOnList;

    public ListAdapter(List<ListItem> feedItemList, Context context) {
        this.feedItemList = feedItemList;
        this.context = context;
    }

    public void setOnClickItemOnList(OnClickItemOnList onClickItemOnList) {
        this.onClickItemOnList = onClickItemOnList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ListItem items = feedItemList.get(position);

        holder.judul.setText(items.getJudul());
        holder.isi_laporan.setText(items.getIsi());
        Picasso.with(context).load(items.getImage()).into(holder.gambar);
        holder.area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickItemOnList != null) {
                    onClickItemOnList.onClick(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return feedItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.gambar)
        com.thor.roadreport.util.CircleImageView gambar;
        @Bind(R.id.judul)
        TextView judul;
        @Bind(R.id.isi_laporan)
        TextView isi_laporan;
        @Bind(R.id.area)
        LinearLayout area;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnClickItemOnList {
        void onClick(int position);
    }
}