package com.example.myapplication.account_management;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.LocalDatabase.MyDatabase;
import com.example.myapplication.LocalDatabase.UserEntity;
import com.example.myapplication.R;
import com.example.myapplication.helper.EncodeHelper;

import java.util.regex.Pattern;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Scheduler;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameView, nicknameView, passwordView;
    private String username, nickname, password;

    private boolean ifNotExist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        setTitle("Sign Up");

        final AccountDataHelper localAccountHelper = new RemoteAccountHelper();

        usernameView = findViewById(R.id.name);
        nicknameView = findViewById(R.id.nickname);
        passwordView = findViewById(R.id.password);

        TextView signUp = findViewById(R.id.tv_sign_up);
        LinearLayout logIn = findViewById(R.id.tv_hint2);

        // When clicking the confirm button
        signUp.setOnClickListener(v -> {

            username = usernameView.getText().toString().trim();
            password = passwordView.getText().toString().trim();
            nickname = nicknameView.getText().toString().trim();

            boolean ifAnythingEmpty = TextUtils.isEmpty(username) || TextUtils.isEmpty(password);

            localAccountHelper.getOneByName(username)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<UserEntity>() {
                        @Override
                        public void onSubscribe(Disposable d) {}

                        @Override
                        public void onSuccess(UserEntity userEntity) {
                            ifNotExist = false;
                            Toast.makeText(SignUpActivity.this, "The username already exists", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {


                            ifNotExist = true;
                            if (ifAnythingEmpty) {
                                Toast.makeText(SignUpActivity.this, "Please fill in username and password", Toast.LENGTH_SHORT).show();
                            } else if (password.length() < 8 ) {
                                Toast.makeText(SignUpActivity.this, "Password length must be eight or more characters.", Toast.LENGTH_SHORT).show();
                            } else if (!Pattern.compile("[0-9]" ).matcher(password).find()
                                    || !Pattern.compile("[a-zA-Z]").matcher(password).find()) {
                                Toast.makeText(SignUpActivity.this, "Password must combine digits and letters.", Toast.LENGTH_SHORT).show();
                            } else {

                                String encryptedPassword = password;

                                if (TextUtils.isEmpty(nickname)) {
                                    localAccountHelper.createAccount(username, encryptedPassword).subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new CompletableObserver() {
                                                @Override
                                                public void onSubscribe(Disposable d) {

                                                }

                                                @Override
                                                public void onComplete() {
                                                    Toast.makeText(SignUpActivity.this, "Register successfully", Toast.LENGTH_SHORT).show();
                                                    // Come back to the user login page with the result
                                                    startActivity(new Intent(SignUpActivity.this, LogInActivity.class).putExtra("username", username));
                                                    SignUpActivity.this.finish();
                                                }

                                                @Override
                                                public void onError(Throwable e) {
                                                    Toast.makeText(SignUpActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                    ;;
                                } else {
                                    localAccountHelper.createAccount(username, nickname, encryptedPassword)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new CompletableObserver() {
                                                @Override
                                                public void onSubscribe(Disposable d) {

                                                }

                                                @Override
                                                public void onComplete() {
                                                    Toast.makeText(SignUpActivity.this, "Register successfully", Toast.LENGTH_SHORT).show();
                                                    // Come back to the user login page with the result
                                                    startActivity(new Intent(SignUpActivity.this, LogInActivity.class).putExtra("username", username));
                                                    SignUpActivity.this.finish();
                                                }

                                                @Override
                                                public void onError(Throwable e) {
                                                    Toast.makeText(SignUpActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                    ;
                                }

                            }
                        }
                    });


        });

        logIn.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LogInActivity.class).putExtra("username", ""));
            SignUpActivity.this.finish();
        });


    }
}
