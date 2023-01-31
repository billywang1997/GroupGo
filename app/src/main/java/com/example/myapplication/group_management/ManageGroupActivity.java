package com.example.myapplication.group_management;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.LocalDatabase.GroupEntity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.account_management.AccountSettingActivity;
import com.example.myapplication.account_management.HomePageFragment;
import com.example.myapplication.account_management.LogInActivity;
import com.example.myapplication.account_management.SignUpActivity;
import com.example.myapplication.my_groups_adapter.MyGroupsItem;

public class ManageGroupActivity extends AppCompatActivity {

    TextView groupNameView, groupIdView;

    public static int curGroupId;
    static String curGroupName;

    TextView modifyPinView;
    ImageView modifyNameView;

    TextView groupMembersView;

    static int curGroupPin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_group);

        setTitle("Manage Group");

        groupNameView = findViewById(R.id.group_name);
        groupIdView = findViewById(R.id.group_id);

        Intent intent = getIntent();

        curGroupId = intent.getIntExtra("group_id", -1);
        curGroupName = intent.getStringExtra("group_name");

        curGroupPin = intent.getIntExtra("group_pin", 1);

        groupNameView.setText(curGroupName);
        groupIdView.setText(String.valueOf(curGroupId));

        modifyPinView = findViewById(R.id.modify_group_pin);
        modifyNameView = findViewById(R.id.pen);

        modifyPinView.setOnClickListener(v -> {
            startActivity(new Intent(ManageGroupActivity.this, ModifyGroupPinActivity.class));
        });

        modifyNameView.setOnClickListener(v -> {
            startActivity(new Intent(ManageGroupActivity.this, ModifyGroupNameActivity.class));
        });

        groupMembersView = findViewById(R.id.group_members);

        groupMembersView.setOnClickListener(v -> {
            startActivity(new Intent(ManageGroupActivity.this, GroupMembersActivity.class));
        });


    }

    // Start page
    public static void clickStart(Context context, int GroupId, String groupName, int groupPin) {
        Intent intent = new Intent(context, ManageGroupActivity.class);
        intent.putExtra("group_id", GroupId);
        intent.putExtra("group_name", groupName);
        intent.putExtra("group_pin", groupPin);
        context.startActivity(intent);

    }

    public void onBackPressed() {
        Intent intent = new Intent(ManageGroupActivity.this, ManagerGroupActivity.class);

        startActivity(intent);
    }
}
