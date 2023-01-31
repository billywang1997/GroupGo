package com.example.myapplication.place_address_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.add_location_management.AddLocationActivity;

import java.util.List;

public class PlaceAddressAdapter extends RecyclerView.Adapter<PlaceAddressAdapter.ViewHolder> {

    private List<PlaceAddressItem> placeAddressList;


    static class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView placeNameView;
        TextView placeAddressView;

        public ViewHolder(View view) {
            super(view);
            itemView = view;
            placeNameView = view.findViewById(R.id.name);
            placeAddressView = view.findViewById(R.id.address);
        }
    }

    public PlaceAddressAdapter (List<PlaceAddressItem> placeList) {
        placeAddressList = placeList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_address_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAbsoluteAdapterPosition();
                PlaceAddressItem item = placeAddressList.get(position);

                AddLocationActivity.clickStart(holder.itemView.getContext(), item.getFormatted_address(), item.getName()
                        , item.getLatitude(), item.getLongitude());
            }
        });

        return holder;


    }

    @Override
    public void onBindViewHolder(PlaceAddressAdapter.ViewHolder holder, int position) {
        PlaceAddressItem item = placeAddressList.get(position);
        holder.placeAddressView.setText(item.getFormatted_address());
        holder.placeNameView.setText(item.getName());

    }

    @Override
    public int getItemCount() {
        return placeAddressList.size();
    }
}
