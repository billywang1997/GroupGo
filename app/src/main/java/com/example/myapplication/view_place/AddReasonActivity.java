package com.example.myapplication.view_place;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.LocalDatabase.MyDatabase;
import com.example.myapplication.R;
import com.example.myapplication.account_management.HomePageFragment;
import com.example.myapplication.account_management.SignUpActivity;
import com.example.myapplication.add_location_management.LocationDataHelper;
import com.example.myapplication.add_location_management.RemoteLocationHelper;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddReasonActivity extends AppCompatActivity {
    EditText editText;
    TextView nameView, addressView, typeView, post;

    String newComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_reason);

        setTitle("Add Recommending Reason");

        editText = findViewById(R.id.reason);

        nameView = findViewById(R.id.place_name);
        addressView = findViewById(R.id.address);
        typeView = findViewById(R.id.type);
        post = findViewById(R.id.post);


        Intent intent = this.getIntent();
        String placeName = intent.getStringExtra("place_name");
        String placeAddress = intent.getStringExtra("place_address");
        String placeType = intent.getStringExtra("place_type");
        String oldComments = intent.getStringExtra("comments");

        nameView.setText(placeName);
        addressView.setText(placeAddress);

        if (placeType == null || placeType.equals("")) {
            typeView.setVisibility(View.INVISIBLE);
        } else {
            typeView.setText("· " + placeType.substring(0, 1).toUpperCase() +
                    placeType.substring(1).toLowerCase());
        }


        final LocationDataHelper localLocationHelper = new RemoteLocationHelper();

        post.setOnClickListener(v -> {
            newComment = editText.getText().toString().trim();

            if (TextUtils.isEmpty(newComment)) {
                Toast.makeText(AddReasonActivity.this, "Please enter the recommended reason", Toast.LENGTH_SHORT).show();
            } else {
                localLocationHelper.addNewComments(HomePageFragment.curGroupNumber, placeName, placeAddress, oldComments + newComment + ";newOneComment;")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onComplete() {
                                Toast.makeText(AddReasonActivity.this, "Your new comment has been added", Toast.LENGTH_SHORT).show();

                                Intent newIntent = new Intent(AddReasonActivity.this, PlaceDetailActivity.class);
                                newIntent.putExtra("place_name", placeName);
                                newIntent.putExtra("place_address", placeAddress);
                                newIntent.putExtra("place_type", placeType);
                                startActivity(newIntent);

                                AddReasonActivity.this.finish();
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });
            }
        });
    }
}
