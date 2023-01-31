package com.example.myapplication.account_management;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.LocalDatabase.MyDatabase;
import com.example.myapplication.LocalDatabase.UserEntity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.helper.EncodeHelper;

import org.w3c.dom.Text;

import java.io.Serializable;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LogInActivity extends AppCompatActivity {

    private EditText usernameView, passwordView;
    private String inputUsername, inputPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

        setTitle("Log In");

        final AccountDataHelper localAccountHelper = new RemoteAccountHelper();

        usernameView = findViewById(R.id.name);
        passwordView = findViewById(R.id.password);

        TextView logIn = findViewById(R.id.tv_log_in);
        LinearLayout signUp = findViewById(R.id.tv_hint);

        logIn.setOnClickListener(v -> {

            if (HomePageFragment.curLocationList != null) {
                HomePageFragment.curLocationList.clear();
            }
            if (HomePageFragment.curVoteList != null) {
                HomePageFragment.curVoteList.clear();
            }

            inputUsername = usernameView.getText().toString().trim();
            inputPassword = passwordView.getText().toString().trim();

            boolean ifAnythingEmpty = TextUtils.isEmpty(inputPassword) || TextUtils.isEmpty(inputUsername);

            if (ifAnythingEmpty) {
                Toast.makeText(LogInActivity.this, "Please fill in username and password", Toast.LENGTH_SHORT).show();
            } else {

                // Check the password
                String encodedPassword = inputPassword;

                localAccountHelper.checkLogIn(inputUsername, encodedPassword)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<UserEntity>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(UserEntity userEntity) {
                                Toast.makeText(LogInActivity.this, "welcome you! " + inputUsername, Toast.LENGTH_SHORT).show();

                                setResult(RESULT_OK, new Intent().putExtra("ifLoggedIn", true));

                                LogInActivity.this.finish();

                                Intent intent = new Intent(LogInActivity.this, MainActivity.class)
                                        .putExtra("curUser", inputUsername)
                                        .putExtra("nickname", userEntity.nickname)
                                        .putExtra("user", userEntity);
                                startActivity(intent);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(LogInActivity.this, "The username or password is wrong, please check", Toast.LENGTH_SHORT).show();
                                // Toast.makeText(LogInActivity.this, "The username or password is wrong, please check", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


        signUp.setOnClickListener(v -> {
            startActivity(new Intent(LogInActivity.this, SignUpActivity.class));
        });


    }

    public void onBackPressed() {
        Intent intent = new Intent(LogInActivity.this, StartPageActivity.class);
        startActivity(intent);
    }
}
