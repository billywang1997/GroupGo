package com.example.myapplication.account_management;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.LocalDatabase.MyDatabase;
import com.example.myapplication.LocalDatabase.UserEntity;
import com.example.myapplication.R;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ModifyNicknameActivity extends AppCompatActivity {
    EditText enterNickname;
    TextView modify;

    String newNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_nickname);

        setTitle("Modify Nickname");

        final AccountDataHelper localAccountHelper = new RemoteAccountHelper();

        Intent intent = getIntent();
        String oldNickname = intent.getStringExtra("nickname");

        enterNickname = findViewById(R.id.new_nickname);
        modify = findViewById(R.id.tv_modify);

        modify.setOnClickListener(v -> {
            newNickname = enterNickname.getText().toString().trim();

            if (TextUtils.isEmpty(newNickname)) {
                Toast.makeText(ModifyNicknameActivity.this, "Please fill in new nickname", Toast.LENGTH_SHORT).show();
            } else if (newNickname.equals(oldNickname)) {
                Toast.makeText(ModifyNicknameActivity.this, "Please use different nickname", Toast.LENGTH_SHORT).show();
            } else {
                localAccountHelper.updateNickname(HomePageFragment.curUser, newNickname, HomePageFragment.thisUser.password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onComplete() {
                                HomePageFragment.thisUser.nickname = newNickname;

                                Toast.makeText(ModifyNicknameActivity.this, "Modify nickname successfully ", Toast.LENGTH_SHORT).show();

                                Intent newIntent = new Intent(ModifyNicknameActivity.this, AccountSettingActivity.class);
                                startActivity(newIntent);

                                ModifyNicknameActivity.this.finish();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(ModifyNicknameActivity.this, "error", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
