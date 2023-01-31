package com.example.myapplication.group_management;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.LocalDatabase.MyDatabase;
import com.example.myapplication.R;
import com.example.myapplication.account_management.AccountDataHelper;
import com.example.myapplication.account_management.RemoteAccountHelper;
import com.example.myapplication.group_member_adapter.GroupMemberAdapter;
import com.example.myapplication.group_member_adapter.GroupMemberItem;
import com.example.myapplication.my_groups_adapter.MyGroupsAdapter;
import com.example.myapplication.my_groups_adapter.MyGroupsItem;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class GroupMembersActivity extends AppCompatActivity {

    private List<GroupMemberItem> groupMemberList = new ArrayList<>();

    GroupDataHelper localGroupHelper;
    GroupManageHelper localGroupManageHelper;

    AccountDataHelper localAccountHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_members_page);

        setTitle("Group Members");

        localGroupHelper = new RemoteGroupHelper();
        localGroupManageHelper = new RemoteGroupManageHelper();

        localAccountHelper = new RemoteAccountHelper();

        initMyGroupsItems();

    }

    private void initMyGroupsItems() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.member_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getParent());
        recyclerView.setLayoutManager(layoutManager);

        localGroupManageHelper.findGroupMembers(ManageGroupActivity.curGroupId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<String> strings) {
                        for (String string : strings) {
                            localAccountHelper.getNickname(string)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new SingleObserver<String>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onSuccess(String s) {
                                            GroupMemberItem curItem = new GroupMemberItem(s, string);
                                            groupMemberList.add(curItem);

                                            GroupMemberAdapter adapter = new GroupMemberAdapter(groupMemberList);
                                            adapter.setOnKickOutClickListener(GroupMembersActivity.this::finish);
                                            recyclerView.setAdapter(adapter);
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            GroupMemberItem curItem = new GroupMemberItem("", string);
                                            groupMemberList.add(curItem);

                                            GroupMemberAdapter adapter = new GroupMemberAdapter(groupMemberList);
                                            adapter.setOnKickOutClickListener(GroupMembersActivity.this::finish);
                                            recyclerView.setAdapter(adapter);
                                        }
                                    });
                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(GroupMembersActivity.this, "No group member here", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Start page
    public static void rewNewList(Context context) {
        Intent intent = new Intent(context, GroupMembersActivity.class);
        context.startActivity(intent);
    }
}
