package com.example.myapplication.place_detail_adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.listViewAdapter.ListViewAdapter;
import com.example.myapplication.listViewAdapter.ListViewItem;

import java.util.List;


public class ReasonAdapter extends RecyclerView.Adapter<ReasonAdapter.ViewHolder> {

    private List<ReasonItem> reasonsList;


    static class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView reasonView;

        public ViewHolder(View view) {
            super(view);
            itemView = view;
            reasonView = view.findViewById(R.id.reason_item);
        }

    }

    public ReasonAdapter (List<ReasonItem> reasonList) {
        reasonsList = reasonList;
    }

    @Override
    public ReasonAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommend_reason_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAbsoluteAdapterPosition();
                ReasonItem item = reasonsList.get(position);
                // PlaceDetailActivity.clickStart(holder.itemView.getContext(), position + 1);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ReasonItem item = reasonsList.get(position);
        holder.reasonView.setText(item.getReason());
    }

    @Override
    public int getItemCount() {
        return reasonsList.size();
    }
}
