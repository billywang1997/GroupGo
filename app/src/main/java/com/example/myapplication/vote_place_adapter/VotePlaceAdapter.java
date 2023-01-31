package com.example.myapplication.vote_place_adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ClickEvent;
import com.example.myapplication.R;
import com.example.myapplication.account_management.LogInActivity;
import com.example.myapplication.listViewAdapter.ListViewAdapter;
import com.example.myapplication.listViewAdapter.ListViewItem;
import com.example.myapplication.view_place.PlaceDetailActivity;
import com.example.myapplication.vote_management.ChooseVoteLocationActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class VotePlaceAdapter extends RecyclerView.Adapter<VotePlaceAdapter.ViewHolder> {
    private List<VotePlaceItem> votePlaceList;




    static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView placeNameView;
        TextView placeAddressView;
        TextView placeTypeView;
        TextView oneReasonView;
        ImageView img1View;
        ImageView img2View;
        ImageView img3View;
        CheckBox selectBox;


        public ViewHolder(View view) {
            super(view);
            itemView = view;
            placeNameView = (TextView) view.findViewById(R.id.place_name);
            placeAddressView = (TextView) view.findViewById(R.id.address);
            placeTypeView = (TextView) view.findViewById(R.id.place_type);
            oneReasonView = (TextView) view.findViewById(R.id.one_reason);
            img1View = (ImageView) view.findViewById(R.id.img1);
            img2View = (ImageView) view.findViewById(R.id.img2);
            img3View = (ImageView) view.findViewById(R.id.img3);
            selectBox = view.findViewById(R.id.select);



        }
    }

    public VotePlaceAdapter(List<VotePlaceItem> votesList) {
        votePlaceList = votesList;

        EventBus.getDefault().register(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.choose_location_card, parent, false);


        ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAbsoluteAdapterPosition();
                VotePlaceItem item = votePlaceList.get(position);
                // PlaceDetailActivity.clickStart(holder.itemView.getContext(), position + 1);

                PlaceDetailActivity.clickStart(holder.itemView.getContext(), item.getPlaceName(), item.getPlaceAddress(), item.getPlaceType() == null ? null : item.getPlaceType().toString(),false);
            }
        });




        return holder;
    }

    @Override
    public void onBindViewHolder(VotePlaceAdapter.ViewHolder holder, int position) {
        VotePlaceItem item = votePlaceList.get(position);
        holder.placeNameView.setText(item.getPlaceName());
        holder.placeAddressView.setText(item.getPlaceAddress());
        String placeType;

        holder.selectBox.setChecked(ChooseVoteLocationActivity.clickedSet.contains(item.getPlaceName() + ";nameThenAddress;" + item.getPlaceAddress()));

        if (item.getPlaceType() != null) {
            placeType = item.getPlaceType().toString();
            holder.placeTypeView.setText("· " + placeType.substring(0, 1) + placeType.substring(1).toLowerCase());
        } else {
            holder.placeTypeView.setText("");
        }

        holder.oneReasonView.setText(item.getRecentReason());
        holder.img1View.setImageBitmap(item.getImageId1());
        holder.img2View.setImageBitmap(item.getImageId2());
        holder.img3View.setImageBitmap(item.getImageId3());

        holder.selectBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()  {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                int position = holder.getAbsoluteAdapterPosition();
                VotePlaceItem item = votePlaceList.get(position);

                if (isChecked) {
                    ChooseVoteLocationActivity.clickedSet.add(item.getPlaceName() + ";nameThenAddress;" + item.getPlaceAddress());

                } else {
                    ChooseVoteLocationActivity.clickedSet.remove(item.getPlaceName() + ";nameThenAddress;" + item.getPlaceAddress());

                }

                EventBus.getDefault().post(new ClickEvent());
            }
        });

    }

    @Override
    public int getItemCount() {
        return votePlaceList.size();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ClickEvent event) {
        notifyDataSetChanged();
    }
}
