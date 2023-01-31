package com.example.myapplication.my_groups_adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.account_management.LogInActivity;
import com.example.myapplication.group_management.ManageGroupActivity;
import com.example.myapplication.group_management.ManagerGroupActivity;
import com.example.myapplication.group_member_adapter.GroupMemberAdapter;
import com.example.myapplication.group_member_adapter.GroupMemberItem;

import java.util.List;

public class ManageGroupsAdapter extends RecyclerView.Adapter<ManageGroupsAdapter.ViewHolder> {

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

    public ManageGroupsAdapter (List<MyGroupsItem> groupsList) {
        myGroupsList = groupsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_group_list, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ManageGroupsAdapter.ViewHolder holder, int position) {
        MyGroupsItem item = myGroupsList.get(position);
        holder.groupNameView.setText(item.getGroupName());
        holder.groupIdView.setText(String.valueOf(item.getGroupId()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAbsoluteAdapterPosition();
                MyGroupsItem curGroup = myGroupsList.get(position);

                ManageGroupActivity.clickStart(holder.itemView.getContext(), curGroup.getGroupId(), curGroup.getGroupName(),
                        ManagerGroupActivity.allGroups.get(position).groupPin);

                System.out.println("---------------------pinpin" + ManagerGroupActivity.allGroups.get(position).groupPin);

                // PlaceDetailActivity.clickStart(holder.itemView.getContext(), position + 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myGroupsList.size();
    }

}
