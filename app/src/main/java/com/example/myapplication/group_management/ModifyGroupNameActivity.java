package com.example.myapplication.group_management;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.LocalDatabase.MyDatabase;
import com.example.myapplication.R;
import com.example.myapplication.account_management.HomePageFragment;
import com.example.myapplication.account_management.LogInActivity;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ModifyGroupNameActivity extends AppCompatActivity {

    EditText groupNameView;
    TextView modifyButton;

    String newGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_group_name);

        setTitle("Modify Group Name");

        final GroupDataHelper localGroupHelper = new RemoteGroupHelper();
        final GroupManageHelper localGroupManageHelper = new RemoteGroupManageHelper();

        groupNameView = findViewById(R.id.group_name);
        modifyButton = findViewById(R.id.tv_modify);

        modifyButton.setOnClickListener(v -> {
            newGroupName = groupNameView.getText().toString().trim();

            if (TextUtils.isEmpty(newGroupName)) {
                Toast.makeText(ModifyGroupNameActivity.this, "Please fill in the blank", Toast.LENGTH_SHORT).show();
            } else {
                localGroupHelper.modifyGroupName(ManageGroupActivity.curGroupId, newGroupName)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onComplete() {
                                Toast.makeText(ModifyGroupNameActivity.this, "Modify your group name successfully", Toast.LENGTH_SHORT).show();

                                HomePageFragment.groupName = newGroupName;

                                startActivity(new Intent(ModifyGroupNameActivity.this, ManageGroupActivity.class)
                                        .putExtra("group_id", ManageGroupActivity.curGroupId)
                                        .putExtra("group_name", newGroupName)
                                        .putExtra("group_pin", ManageGroupActivity.curGroupPin));

                                ModifyGroupNameActivity.this.finish();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(ModifyGroupNameActivity.this, "Please choose different name", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
