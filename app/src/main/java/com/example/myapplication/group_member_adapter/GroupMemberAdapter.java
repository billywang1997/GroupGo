package com.example.myapplication.group_member_adapter;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.LocalDatabase.MyDatabase;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.account_management.HomePageFragment;
import com.example.myapplication.group_management.AddGroupActivity;
import com.example.myapplication.group_management.GroupDataHelper;
import com.example.myapplication.group_management.GroupManageHelper;
import com.example.myapplication.group_management.GroupMembersActivity;
import com.example.myapplication.group_management.ManageGroupActivity;
import com.example.myapplication.group_management.RemoteGroupHelper;
import com.example.myapplication.group_management.RemoteGroupManageHelper;
import com.example.myapplication.place_detail_adapter.ReasonAdapter;
import com.example.myapplication.place_detail_adapter.ReasonItem;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.ViewHolder> {

    private List<GroupMemberItem> groupMemberList;
    ViewHolder holder;

    GroupDataHelper localGroupHelper;
    GroupManageHelper localGroupManageHelper;

    public OnKickOutClickListener onKickOutClickListener;


    static class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView nicknameView;
        TextView usernameView;

        public ViewHolder(View view) {
            super(view);
            itemView = view;
            nicknameView = view.findViewById(R.id.nickname);
            usernameView = view.findViewById(R.id.username);
        }
    }

    public GroupMemberAdapter (List<GroupMemberItem> memberList) {
        groupMemberList = memberList;
    }

    public void setOnKickOutClickListener(OnKickOutClickListener onKickOutClickListener) {
        this.onKickOutClickListener = onKickOutClickListener;
    }

    @Override
    public GroupMemberAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_member_list, parent, false);
        holder = new ViewHolder(view);

        localGroupHelper = new RemoteGroupHelper();
        localGroupManageHelper = new RemoteGroupManageHelper();



        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(GroupMemberAdapter.ViewHolder holder, int position) {
        GroupMemberItem item = groupMemberList.get(position);
        holder.nicknameView.setText(item.getNickname());
        holder.usernameView.setText("Username: " + item.getUsername());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAbsoluteAdapterPosition();
                GroupMemberItem item = groupMemberList.get(position);

                System.out.println("===================kickout position " + position);

                showBottomSheetDialog(item);


            }
        });

    }

    @Override
    public int getItemCount() {
        return groupMemberList.size();
    }

    private void showBottomSheetDialog(GroupMemberItem item) {
        final BottomSheetDialog dialog = new BottomSheetDialog(holder.itemView.getContext());
        View dialogView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.bottom_dialog,null);
        TextView tvSure = (TextView) dialogView.findViewById(R.id.tv_sure);

        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                new AlertDialog.Builder(holder.itemView.getContext()).setTitle("Notice")

                        .setMessage("Do you want to kick out this user？")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (item.getUsername().equals(HomePageFragment.curUser)) {
                                    Toast.makeText(holder.itemView.getContext(), "You cannot kick out yourself", Toast.LENGTH_SHORT).show();
                                } else {
                                    System.out.println("===================kickout " + item.getUsername());

                                    localGroupManageHelper.deleteMemberFromGroup(ManageGroupActivity.curGroupId, item.getUsername())
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new CompletableObserver() {
                                                @Override
                                                public void onSubscribe(Disposable d) {

                                                }

                                                @Override
                                                public void onComplete() {
                                                    Toast.makeText(holder.itemView.getContext(), "You have successfully kicked out this member", Toast.LENGTH_SHORT).show();

                                                    dialog.dismiss();
                                                    onKickOutClickListener.onKickOutClick();
                                                    GroupMembersActivity.rewNewList(holder.itemView.getContext());
                                                }

                                                @Override
                                                public void onError(Throwable e) {
                                                    Toast.makeText(holder.itemView.getContext(), "There's something wrong", Toast.LENGTH_SHORT).show();

                                                    dialog.dismiss();
                                                }
                                            });
                                }

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }

                        }).show();

            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(dialogView);
        dialog.show();
    }

    public interface OnKickOutClickListener {
        void onKickOutClick();
    }

}
