package com.example.myapplication.account_management;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.LocalDatabase.MyDatabase;
import com.example.myapplication.R;
import com.example.myapplication.helper.EncodeHelper;

import java.util.regex.Pattern;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ModifyPasswordActivity extends AppCompatActivity {
    EditText curPasswordView, newPasswordView;
    String curPassword, newPassword;
    TextView modify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_password);

        setTitle("Modify Password");

        final AccountDataHelper localAccountHelper = new RemoteAccountHelper();

        curPasswordView = findViewById(R.id.cur_password);
        newPasswordView = findViewById(R.id.new_password);
        modify = findViewById(R.id.tv_modify);

        modify.setOnClickListener(v -> {
            curPassword = curPasswordView.getText().toString().trim();
            newPassword = newPasswordView.getText().toString().trim();

            String encodedCurPassword = curPassword;
            String encodedNewPassword = newPassword;

            if (TextUtils.isEmpty(curPassword) || TextUtils.isEmpty(newPassword)) {
                Toast.makeText(ModifyPasswordActivity.this, "Please fill in all blanks", Toast.LENGTH_SHORT).show();
            } else if (!encodedCurPassword.equals(HomePageFragment.thisUser.password)) {
                Toast.makeText(ModifyPasswordActivity.this, "Please enter the correct original password", Toast.LENGTH_SHORT).show();
            } else if (curPassword.equals(newPassword)) {
                Toast.makeText(ModifyPasswordActivity.this, "Please use a different password", Toast.LENGTH_SHORT).show();
            } else if (newPassword.length() < 8) {
                Toast.makeText(this, "Password length must be eight or more characters.", Toast.LENGTH_SHORT).show();
            } else if (!Pattern.compile("[0-9]" ).matcher(newPassword).find()
                    || !Pattern.compile("[a-zA-Z]").matcher(newPassword).find()) {
                Toast.makeText(this, "Password must combine digits and letters.", Toast.LENGTH_SHORT).show();
            } else {
                localAccountHelper.resetPassword(HomePageFragment.curUser, HomePageFragment.thisUser.password, encodedNewPassword)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onComplete() {

                                HomePageFragment.thisUser.password = encodedNewPassword;

                                Toast.makeText(ModifyPasswordActivity.this, "Modify password successfully ", Toast.LENGTH_SHORT).show();

                                Intent newIntent = new Intent(ModifyPasswordActivity.this, AccountSettingActivity.class);
                                startActivity(newIntent);

                                ModifyPasswordActivity.this.finish();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("ModifyPasswordActivity", "onError: " + e.toString());
                            }
                        });
            }

        });
    }
}
