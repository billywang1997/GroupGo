package com.example.myapplication.account_management;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class PrivatePolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_policy);
        WebView webView = findViewById(R.id.webview);
        webView.loadUrl("https://doc-hosting.flycricket.io/groupgo-privacy-policy/d1f440fb-8f19-4aac-8562-76bbaf2b10ea/privacy");
    }

}
