package com.example.myapplication.group_management;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.LocalDatabase.GroupEntity;
import com.example.myapplication.LocalDatabase.MyDatabase;
import com.example.myapplication.R;
import com.example.myapplication.account_management.HomePageFragment;
import com.example.myapplication.account_management.LogInActivity;
import com.example.myapplication.account_management.SignUpActivity;

import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddGroupActivity extends AppCompatActivity {
    TextView createGroup, addGroup;
    EditText numberView, pinView;
    int number, pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_group);

        setTitle("Add Group");

        createGroup = findViewById(R.id.tv_hint1);
        numberView = findViewById(R.id.number);
        pinView = findViewById(R.id.pin);
        addGroup = findViewById(R.id.tv_add);

        final GroupDataHelper localGroupHelper = new RemoteGroupHelper();
        final GroupManageHelper localGroupManageHelper = new RemoteGroupManageHelper();

        createGroup.setOnClickListener(v -> {

            startActivity(new Intent(AddGroupActivity.this, CreateGroupActivity.class));

        });

        addGroup.setOnClickListener(v -> {

            boolean ifAnythingEmpty = TextUtils.isEmpty(numberView.getText().toString()) || TextUtils.isEmpty(pinView.getText().toString());
            if (ifAnythingEmpty) {
                Toast.makeText(getApplicationContext(), "Please fill in all the blanks.", Toast.LENGTH_SHORT).show();
                return;
            }

            number = Integer.parseInt(numberView.getText().toString().trim());
            pin = Integer.parseInt(pinView.getText().toString().trim());

            localGroupHelper.checkOneGroup(number, pin)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<GroupEntity>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(GroupEntity groupEntity) {
                            localGroupManageHelper.insertMemberToGroup(number, HomePageFragment.curUser)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new CompletableObserver() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onComplete() {
                                            Toast.makeText(AddGroupActivity.this, "You have successfully entered the group", Toast.LENGTH_SHORT).show();

                                            startActivity(new Intent(AddGroupActivity.this, MyGroupsActivity.class));
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Toast.makeText(AddGroupActivity.this, "You have entered this group before", Toast.LENGTH_SHORT).show();

                                            startActivity(new Intent(AddGroupActivity.this, MyGroupsActivity.class));
                                        }
                                    });

                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(AddGroupActivity.this, "Please recheck your group number or pin", Toast.LENGTH_SHORT).show();
                        }
                    });


        });
    }


}
