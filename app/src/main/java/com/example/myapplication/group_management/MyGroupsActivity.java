package com.example.myapplication.group_management;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.myapplication.LocalDatabase.MyDatabase;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.account_management.AccountSettingActivity;
import com.example.myapplication.account_management.HomePageFragment;
import com.example.myapplication.account_management.LogInActivity;
import com.example.myapplication.account_management.SignUpActivity;
import com.example.myapplication.listViewAdapter.ListViewAdapter;
import com.example.myapplication.my_groups_adapter.MyGroupsAdapter;
import com.example.myapplication.my_groups_adapter.MyGroupsItem;
import com.example.myapplication.place_detail_adapter.PhotoItem;
import com.example.myapplication.place_detail_adapter.ReasonItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MyGroupsActivity extends AppCompatActivity {

    private List<MyGroupsItem> myGroupsList = new ArrayList<>();
    GroupDataHelper localGroupHelper;
    GroupManageHelper localGroupManageHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_groups_page);

        setTitle("Groups You Entered");

        localGroupHelper = new RemoteGroupHelper();
        localGroupManageHelper = new RemoteGroupManageHelper();

        FloatingActionButton floatingButton = findViewById(R.id.floating);

        initMyGroupsItems();



        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyGroupsActivity.this, AddGroupActivity.class));
            }
        });

    }

    private void initMyGroupsItems() {
        localGroupManageHelper.findMemberGroups(HomePageFragment.curUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Integer> integers) {
                        for (Integer integer: integers) {

                            System.out.println("============Integer==========" + integer);


                            localGroupHelper.getGroupName(integer)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new SingleObserver<String>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onSuccess(String s) {
                                            s = s.replace("\"", "");
                                            System.out.println("===========Name==========" + s);

                                            myGroupsList.add(new MyGroupsItem(s, integer));


                                            System.out.println("===========Group==========" + myGroupsList);


                                            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.groups_list);
                                            LinearLayoutManager layoutManager = new LinearLayoutManager(getParent());
                                            recyclerView.setLayoutManager(layoutManager);
                                            MyGroupsAdapter adapter = new MyGroupsAdapter(myGroupsList);
                                            recyclerView.setAdapter(adapter);

                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Toast.makeText(MyGroupsActivity.this, "Error getGroupName", Toast.LENGTH_SHORT).show();
                                        }


                                    });
                        }



                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MyGroupsActivity.this, "Error findMemberGroups: " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void onBackPressed() {
        Intent intent = new Intent(MyGroupsActivity.this, MainActivity.class);
        intent.putExtra("curUser", HomePageFragment.curUser);
        intent.putExtra("user", HomePageFragment.thisUser);
        intent.putExtra("nickname", HomePageFragment.thisUser.nickname);
        startActivity(intent);
    }
}
