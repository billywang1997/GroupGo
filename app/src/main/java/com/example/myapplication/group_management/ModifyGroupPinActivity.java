package com.example.myapplication.group_management;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.LocalDatabase.GroupEntity;
import com.example.myapplication.LocalDatabase.MyDatabase;
import com.example.myapplication.R;

import org.w3c.dom.Text;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ModifyGroupPinActivity extends AppCompatActivity {

    TextView currentPinView;
    TextView modifyButton;
    EditText newPinView;

    int newPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_group_pin);

        setTitle("Modify Group Pin");

        final GroupDataHelper localGroupHelper = new RemoteGroupHelper();
        final GroupManageHelper localGroupManageHelper = new RemoteGroupManageHelper();

        currentPinView = findViewById(R.id.current_pin_frame);
        newPinView = findViewById(R.id.new_pin);

        modifyButton = findViewById(R.id.tv_modify);

        currentPinView.setText(String.valueOf(ManageGroupActivity.curGroupPin));

        modifyButton.setOnClickListener(v -> {
            newPin = Integer.parseInt(newPinView.getText().toString().trim());

            if (TextUtils.isEmpty(newPinView.getText().toString())) {
                Toast.makeText(ModifyGroupPinActivity.this, "Please fill in the blank", Toast.LENGTH_SHORT).show();
            } else if (newPinView.getText().toString().trim().length() != 4) {
                Toast.makeText(ModifyGroupPinActivity.this, "Please use 4 digits as the group pin", Toast.LENGTH_SHORT).show();
            } else {
                localGroupHelper.modifyGroupPin(ManageGroupActivity.curGroupId, newPin)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onComplete() {
                                Toast.makeText(ModifyGroupPinActivity.this, "Modify your group pin successfully", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(ModifyGroupPinActivity.this, ManageGroupActivity.class)
                                        .putExtra("group_id", ManageGroupActivity.curGroupId)
                                        .putExtra("group_name", ManageGroupActivity.curGroupName)
                                        .putExtra("group_pin", newPin));

                                ModifyGroupPinActivity.this.finish();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(ModifyGroupPinActivity.this, "Please choose different name", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });



    }
}
