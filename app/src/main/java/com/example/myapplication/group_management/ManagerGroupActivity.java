package com.example.myapplication.group_management;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.LocalDatabase.GroupEntity;
import com.example.myapplication.LocalDatabase.MyDatabase;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.account_management.AccountSettingActivity;
import com.example.myapplication.account_management.HomePageFragment;
import com.example.myapplication.my_groups_adapter.ManageGroupsAdapter;
import com.example.myapplication.my_groups_adapter.MyGroupsAdapter;
import com.example.myapplication.my_groups_adapter.MyGroupsItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ManagerGroupActivity extends AppCompatActivity {

    private List<MyGroupsItem> manageGroupsList = new ArrayList<>();
    public static List<GroupEntity> allGroups = new ArrayList<>();
    GroupDataHelper localGroupHelper;
    GroupManageHelper localGroupManageHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_group_page);

        setTitle("Groups You Manages");

        localGroupHelper = new RemoteGroupHelper();
        localGroupManageHelper = new RemoteGroupManageHelper();


        initMyGroupsItems();


    }

    private void initMyGroupsItems() {
        localGroupHelper.findLatestGroupsByManager(HomePageFragment.curUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<GroupEntity>>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<GroupEntity> groupEntities) {
                        for (GroupEntity groupEntity : groupEntities) {
                            manageGroupsList.add(new MyGroupsItem(groupEntity.groupName, groupEntity.groupNumber));
                            allGroups.add(groupEntity);
                        }

                        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.groups_list);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getParent());
                        recyclerView.setLayoutManager(layoutManager);
                        ManageGroupsAdapter adapter = new ManageGroupsAdapter(manageGroupsList);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    public void onBackPressed() {
        Intent intent = new Intent(ManagerGroupActivity.this, MainActivity.class);
        intent.putExtra("curUser", HomePageFragment.curUser);
        intent.putExtra("user", HomePageFragment.thisUser);
        intent.putExtra("nickname", HomePageFragment.thisUser.nickname);
        startActivity(intent);
    }
}
