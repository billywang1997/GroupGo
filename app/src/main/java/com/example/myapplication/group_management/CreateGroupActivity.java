package com.example.myapplication.group_management;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.LocalDatabase.GroupEntity;
import com.example.myapplication.LocalDatabase.MyDatabase;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.account_management.HomePageFragment;
import com.example.myapplication.account_management.LogInActivity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CreateGroupActivity extends AppCompatActivity {


    EditText nameView, pinView;
    String name;
    int pin;
    TextView createButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group);

        setTitle("Create Group");

        final GroupDataHelper localGroupHelper = new RemoteGroupHelper();

        nameView = findViewById(R.id.name);
        pinView = findViewById(R.id.pin);
        pinView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

        final GroupManageHelper localGroupManageHelper = new RemoteGroupManageHelper();

        createButton = findViewById(R.id.tv_create);

        createButton.setOnClickListener(v -> {

            name = nameView.getText().toString().trim();
            pin = Integer.parseInt(pinView.getText().toString().trim());

            boolean ifAnythingEmpty = TextUtils.isEmpty(name) || TextUtils.isEmpty(pinView.getText().toString());

            if (ifAnythingEmpty) {
                Toast.makeText(CreateGroupActivity.this, "Please fill in all blanks", Toast.LENGTH_SHORT).show();
            } else if (pinView.getText().toString().trim().length() != 4) {
                Toast.makeText(CreateGroupActivity.this, "Please use 4 digits as the group pin", Toast.LENGTH_SHORT).show();
            } else {

                    localGroupHelper.createGroup(name, pin)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new CompletableObserver() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onComplete() {
                                    Toast.makeText(CreateGroupActivity.this, "You have successfully created a new group", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(CreateGroupActivity.this, MyGroupsActivity.class);

                                    startActivity(intent);
//                                    localGroupHelper.findLatestGroupsByManager(HomePageFragment.curUser)
//                                            .subscribeOn(Schedulers.io())
//                                            .observeOn(AndroidSchedulers.mainThread())
//                                            .subscribe(new SingleObserver<List<GroupEntity>>() {
//                                                @Override
//                                                public void onSubscribe(Disposable d) {
//
//                                                }
//
//                                                @Override
//                                                public void onSuccess(List<GroupEntity> groupEntities) {
//                                                    int groupId = groupEntities.get(0).groupNumber;
//
//                                                    localGroupManageHelper.insertMemberToGroup(groupId, HomePageFragment.curUser)
//                                                            .subscribeOn(Schedulers.io())
//                                                            .observeOn(AndroidSchedulers.mainThread())
//                                                            .subscribe(new CompletableObserver() {
//                                                                @Override
//                                                                public void onSubscribe(Disposable d) {
//
//                                                                }
//
//                                                                @Override
//                                                                public void onComplete() {
//
//                                                                    Intent intent = new Intent(CreateGroupActivity.this, MyGroupsActivity.class);
//
//                                                                    startActivity(intent);
//                                                                }
//
//                                                                @Override
//                                                                public void onError(Throwable e) {
//                                                                }
//                                                            });
//
//
//                                                }
//
//                                                @Override
//                                                public void onError(Throwable e) {
//
//                                                }
//                                            });

                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });




            }
        });


    }
}
