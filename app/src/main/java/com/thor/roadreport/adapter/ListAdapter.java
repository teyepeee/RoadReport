package com.thor.roadreport.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.thor.roadreport.R;
import com.thor.roadreport.model.ListItem;
import com.thor.roadreport.util.ListViewHolder;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListViewHolder> {

    private List<ListItem> feedItemList;
    private Context mContext;

    public ListAdapter(Context context, List<ListItem> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView textView;

        public CustomViewHolder(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.nama);
        }
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rec_item, null);
        ListViewHolder mh = new ListViewHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(ListViewHolder feedListRowHolder, int i) {
        ListItem item = feedItemList.get(i);

        feedListRowHolder.tvName.setText(item.getNama());

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomViewHolder holder = (CustomViewHolder) view.getTag();
                int position = holder.getPosition();

                ListItem listItem = feedItemList.get(position);
                Toast.makeText(mContext, listItem.getNama(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }
}