package com.example.myapplication.helper;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.add_location_management.AddLocationActivity;
import com.example.myapplication.helper.Model.Listclass;

import java.util.List;

public class OneLineAdapter extends RecyclerView.Adapter<OneLineAdapter.ViewHolder> {
    private List<Listclass> oneLineList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView nameView;


        public ViewHolder(View view) {
            super(view);
            itemView = view;
            nameView = (TextView) view.findViewById(R.id.name);
        }
    }

    public OneLineAdapter(List<Listclass> nameList) {
        oneLineList = nameList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_line_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAbsoluteAdapterPosition();
                Listclass item = oneLineList.get(position);

                /*
                AddLocationActivity.clickStart(holder.itemView.getContext(), item.getFormatted_address(), item.getName()
                        , item.getLatitude(), item.getLongitude());

                 */
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(OneLineAdapter.ViewHolder holder, int position) {
        Listclass item = oneLineList.get(position);
        holder.nameView.setText(item.getDescription());

    }

    @Override
    public int getItemCount() {
        return oneLineList.size();
    }
}
