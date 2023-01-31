package com.example.myapplication.account_management;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

import org.greenrobot.eventbus.EventBus;

public class AccountSettingActivity extends AppCompatActivity {
    TextView nicknameView, passwordView, logOutView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_setting);

        setTitle("Account Management");

        Intent intent = getIntent();
        String nickname = intent.getStringExtra("nickname");

        ImageView pen = findViewById(R.id.pen);
        nicknameView = findViewById(R.id.username);
        passwordView = findViewById(R.id.modify_password);
        logOutView = findViewById(R.id.tv_log_out);
        if (HomePageFragment.thisUser.nickname != null) {
            nicknameView.setText(HomePageFragment.thisUser.nickname);
        }

        pen.setOnClickListener(v -> {
            Intent newIntent = new Intent(AccountSettingActivity.this, ModifyNicknameActivity.class)
                    .putExtra("nickname", nickname);
            startActivity(newIntent);
        });

        passwordView.setOnClickListener(v -> {
            Intent newIntent = new Intent(AccountSettingActivity.this, ModifyPasswordActivity.class);
            startActivity(newIntent);
        });

        logOutView.setOnClickListener(v -> {
            HomePageFragment.curUser = null;
            HomePageFragment.thisUser = null;
            HomePageFragment.curGroupNumber = -1;
            HomePageFragment.groupName = null;
            EventBus.getDefault().post(new LogOutEvent());
            startActivity(new Intent(AccountSettingActivity.this, LogInActivity.class));
            AccountSettingActivity.this.finish();
        });

    }


    public void onBackPressed() {
        Intent intent = new Intent(AccountSettingActivity.this, MainActivity.class);
        intent.putExtra("curUser", HomePageFragment.curUser);
        intent.putExtra("user", HomePageFragment.thisUser);
        intent.putExtra("nickname", HomePageFragment.thisUser.nickname);
        startActivity(intent);
    }


}
