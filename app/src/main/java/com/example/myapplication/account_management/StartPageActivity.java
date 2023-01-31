package com.example.myapplication.account_management;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class StartPageActivity extends AppCompatActivity {

    private TextView logIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page);

        setTitle("GroupGo");

        logIn = findViewById(R.id.tv_log_in);

        logIn.setOnClickListener(v -> {
            startActivity(new Intent(StartPageActivity.this, LogInActivity.class));
            StartPageActivity.this.finish();
        });

        TextView privatePolicyTv = findViewById(R.id.tv_private_policy);
        privatePolicyTv.setOnClickListener(view -> {
            startActivity(new Intent(this, PrivatePolicyActivity.class));
        });

    }

    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}
