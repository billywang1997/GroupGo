package com.example.myapplication.my_groups_adapter;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.account_management.HomePageFragment;
import com.example.myapplication.group_member_adapter.GroupMemberAdapter;
import com.example.myapplication.group_member_adapter.GroupMemberItem;

import java.util.List;

public class MyGroupsAdapter extends RecyclerView.Adapter<MyGroupsAdapter.ViewHolder> {

    private List<MyGroupsItem> myGroupsList;


    static class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView groupNameView;
        TextView groupIdView;

        public ViewHolder(View view) {
            super(view);
            itemView = view;
            groupNameView = view.findViewById(R.id.group_name);
            groupIdView = view.findViewById(R.id.group_id);
        }
    }

    public MyGroupsAdapter (List<MyGroupsItem> groupsList) {
        myGroupsList = groupsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_group_list, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyGroupsAdapter.ViewHolder holder, int position) {
        MyGroupsItem item = myGroupsList.get(position);
        holder.groupNameView.setText(item.getGroupName());
        holder.groupIdView.setText(String.valueOf(item.getGroupId()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAbsoluteAdapterPosition();
                MyGroupsItem item = myGroupsList.get(position);

                HomePageFragment.curGroupNumber = item.getGroupId();
                HomePageFragment.groupName = item.getGroupName();

                if (HomePageFragment.curVoteList != null) {
                    HomePageFragment.curVoteList.clear();
                }
                if (HomePageFragment.curLocationList != null) {
                    HomePageFragment.curLocationList.clear();
                }
                HomePageFragment.currentVote = null;
                HomePageFragment.ifHaveCurrentVote = false;
                HomePageFragment.currentVoteStartTime = "";

                HomePageFragment.clickStartHome(holder.itemView.getContext(), item.getGroupId(), item.getGroupName());

            }
        });
    }

    @Override
    public int getItemCount() {
        return myGroupsList.size();
    }

}
