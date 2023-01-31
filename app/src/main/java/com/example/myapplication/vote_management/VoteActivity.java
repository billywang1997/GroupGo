package com.example.myapplication.vote_management;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.LocalDatabase.LocationEntity;
import com.example.myapplication.LocalDatabase.MyDatabase;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.account_management.AccountSettingActivity;
import com.example.myapplication.account_management.HomePageFragment;
import com.example.myapplication.account_management.LogInActivity;
import com.example.myapplication.account_management.SignUpActivity;
import com.example.myapplication.add_location_management.LocationDataHelper;
import com.example.myapplication.add_location_management.RemoteLocationHelper;
import com.example.myapplication.group_management.CreateGroupActivity;
import com.example.myapplication.group_management.MyGroupsActivity;
import com.example.myapplication.helper.InputFilterMinMax;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.logging.Logger;

import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class VoteActivity extends AppCompatActivity {

    EditText hourView, minuteView, secondView;

    EditText activityNameView;

    TextView locationListView, startButton;

    int curNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vote);

        final VoteDataHelper localVoteHelper = new RemoteVoteDataHelper();
        final LocationDataHelper localLocationHelper = new RemoteLocationHelper();

        hourView = findViewById(R.id.time_hour);
        minuteView = findViewById(R.id.time_minute);
        secondView = findViewById(R.id.time_second);

        activityNameView = findViewById(R.id.activity_name_enter);

        startButton = findViewById(R.id.tv_start);
        locationListView = findViewById(R.id.location_list);

        Intent intent = this.getIntent();
        String numberOfSelected = intent.getStringExtra("number_of_places");
        String activityNames = intent.getStringExtra("activity_name");
        String hourNumb = intent.getStringExtra("hour_num");
        String minuteNumb = intent.getStringExtra("minute_num");
        String secondNumb = intent.getStringExtra("second_num");

        if (activityNames != null && !activityNames.isEmpty()) {
            activityNameView.setText(activityNames);
        }


        if (hourNumb != null && !hourNumb.isEmpty()) {
            hourView.setText(hourNumb);
        }

        if (secondNumb != null && !secondNumb.isEmpty()) {
            secondView.setText(secondNumb);
        }

        if (minuteNumb != null && !minuteNumb.isEmpty()) {
            minuteView.setText(minuteNumb);
        }

        if (numberOfSelected != null && !numberOfSelected.isEmpty())  {
            locationListView.setText(numberOfSelected + " places selected");
        }

        hourView.setFilters(new InputFilter[] { new InputFilterMinMax(0, 999), new InputFilter.LengthFilter(3)});
        minuteView.setFilters(new InputFilter[] { new InputFilterMinMax(0, 59), new InputFilter.LengthFilter(2)});
        secondView.setFilters(new InputFilter[] { new InputFilterMinMax(0, 59), new InputFilter.LengthFilter(2)});

        locationListView.setOnClickListener(
                v -> {
                    if (HomePageFragment.curLocationList == null || HomePageFragment.curLocationList.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Be the first one to add location in your group.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intentNew = new Intent(VoteActivity.this, ChooseVoteLocationActivity.class);
                    if (!TextUtils.isEmpty(activityNameView.getText().toString().trim())) {
                        intentNew.putExtra("activity_name", activityNameView.getText().toString().trim());
                    }

                    if (!TextUtils.isEmpty(hourView.getText().toString().trim())) {
                        intentNew.putExtra("hour_num", hourView.getText().toString().trim());
                    }

                    if (!TextUtils.isEmpty(minuteView.getText().toString().trim())) {
                        intentNew.putExtra("minute_num", minuteView.getText().toString().trim());
                    }

                    if (!TextUtils.isEmpty(secondView.getText().toString().trim())) {
                        intentNew.putExtra("second_num", secondView.getText().toString().trim());
                    }

                    startActivity(intentNew);
                }
        );


        startButton.setOnClickListener(
                v -> {

                    String activityName = activityNameView.getText().toString().trim();
                    String hour = hourView.getText().toString().trim();
                    String minute = minuteView.getText().toString().trim();
                    String second = secondView.getText().toString().trim();




                    if (TextUtils.isEmpty(activityName)) {
                        Toast.makeText(VoteActivity.this, "Please fill in the activity name", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(hour) || TextUtils.isEmpty(minute) || TextUtils.isEmpty(second)) {
                        Toast.makeText(VoteActivity.this, "Please enter hour, minute and second", Toast.LENGTH_SHORT).show();
                    } else if (Integer.parseInt(hour) == 0 && Integer.parseInt(minute) == 0 && Integer.parseInt(second) < 30) {
                        Toast.makeText(VoteActivity.this, "Vote should last for more than 30 seconds", Toast.LENGTH_SHORT).show();
                    } else if (ChooseVoteLocationActivity.clickedSet == null || ChooseVoteLocationActivity.clickedSet.isEmpty()
                || ChooseVoteLocationActivity.clickedSet.size() == 1) {
                        Toast.makeText(VoteActivity.this, "You should choose at least two locations for voting", Toast.LENGTH_SHORT).show();
                    } else {
                        int hourNum = Integer.parseInt(hour);
                        int minuteNum = Integer.parseInt(minute);
                        int secondNum = Integer.parseInt(second);

                        for (String locationPair : ChooseVoteLocationActivity.clickedSet) {
                            String[] locationPairList = locationPair.split(";nameThenAddress;");

                            String locationName = locationPairList[0];
                            String locationAddress = locationPairList[1];

                            String startTime = addCreateTime();
                            String endTime = addEndTime(hourNum, minuteNum, secondNum);

                            localLocationHelper.getLocation(HomePageFragment.curGroupNumber, locationName, locationAddress)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new SingleObserver<LocationEntity>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onSuccess(LocationEntity locationEntity) {
                                            System.out.println("************************STARTTIME************** " + startTime + " ************************STARTTIME**************" );
                                            System.out.println("************************ENDTIME************** " + endTime + " ************************ENDTIME**************");
                                            Log.i("VoteActivity", "insertOneVote, locationTag: " + locationEntity.locationTag);
                                            localVoteHelper.insertOneVote(HomePageFragment.curGroupNumber, activityName, locationName, locationAddress,
                                                    locationEntity.locationTag, locationEntity.comments, locationEntity.photos,
                                                    locationEntity.longitude, locationEntity.latitude, startTime, endTime, 0)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(new CompletableObserver() {
                                                        @Override
                                                        public void onSubscribe(Disposable d) {

                                                        }

                                                        @Override
                                                        public void onComplete() {
                                                            curNum++;

                                                            if (curNum == ChooseVoteLocationActivity.clickedSet.size()) {

                                                                Toast.makeText(VoteActivity.this, "This vote is set successfully", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(VoteActivity.this, MainActivity.class)
                                                                        .putExtra("fragment_id", 2)
                                                                        .putExtra("curUser", HomePageFragment.curUser)
                                                                        .putExtra("nickname", HomePageFragment.thisUser.nickname)
                                                                        .putExtra("user", HomePageFragment.thisUser)
                                                                        .putExtra("vote_start", "Started");


                                                                startActivity(intent);
                                                                VoteActivity.this.finish();

                                                            }
                                                        }

                                                        @Override
                                                        public void onError(Throwable e) {
                                                            Log.e("VoteActitivity", "onError: " + e.toString());
                                                        }
                                                    });
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Log.e("VoteActitivity", "onError: " + e.toString());
                                        }
                                    });


                        }
                    }
                }
        );

    }

    private String addCreateTime() {

        TimeZone tz = TimeZone.getTimeZone("UTC");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(tz);
        Date dateformatnew = new Date();
        String createTime = dateFormat.format(dateformatnew);

        return createTime;
    }



    private String addEndTime (int hourNum, int minuteNum, int secondNum) {

        TimeZone tz = TimeZone.getTimeZone("UTC");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        dateFormat.setTimeZone(tz);
        Date dateformatnew = new Date();
        String createTime = dateFormat.format(dateformatnew);

        String[] dateParts = createTime.split("-");

        System.out.println("hourNumhourNumhourNumhourNumhourNumhourNumhourNumhourNumhourNum " + hourNum);

        int newSecond = secondNum + Integer.parseInt(dateParts[5]);
        int newMinute = minuteNum + Integer.parseInt(dateParts[4]);
        int newHour = hourNum + Integer.parseInt(dateParts[3]);

        System.out.println("dateformatnew.getHours()dateformatnew.getHours()dateformatnew.getHours()dateformatnew.getHours()dateformatnew.getHours()" + dateformatnew.getHours());
        System.out.println("newHour newHour newHour newHour newHour newHour " + newHour);
        int newDate = Integer.parseInt(dateParts[2]);
        int newMonth = Integer.parseInt(dateParts[1]);
        int newYear = Integer.parseInt(dateParts[0]);

        int daysNum;

        if (newSecond >= 60) {
            newSecond -= 60;
            newMinute++;
        }

        if (newMinute >= 60) {
            newMinute -= 60;
            newHour++;
        }

        if (newHour >= 24) {
            daysNum = newHour / 24;
            int leftHours = newHour % 24;

            newHour = leftHours;
            newDate += daysNum;
        }

        if (newMonth == 1 || newMonth == 3 || newMonth == 5 || newMonth == 7 || newMonth == 8 || newMonth == 10 || newMonth == 12) {
            if (newDate >= 32) {
                newDate -= 31;
                newMonth++;
            }
        }

        if (newMonth == 4 || newMonth == 6 || newMonth == 9 || newMonth == 11) {
            if (newDate >= 31) {
                newDate -= 30;
                newMonth++;
            }
        }

        if ((newYear % 4 != 0 || newYear == 2100) && newMonth == 2) {
            if (newDate >= 29) {
                newDate -= 28;
                newMonth++;
            }
        }

        if ((newYear % 4 == 0 && newYear != 2100) && newMonth == 2) {
            if (newDate >= 30) {
                newDate -= 29;
                newMonth++;
            }
        }

        if (newMonth >= 13) {
            newMonth -= 12;
            newYear++;
        }

        String newSecondString, newMinuteString, newHourString, newDateString, newMonthString, newYearString;

        if (String.valueOf(newSecond).length() == 1) {
            newSecondString = "0" + String.valueOf(newSecond);
        } else {
            newSecondString = String.valueOf(newSecond);
        }

        if (String.valueOf(newMinute).length() == 1) {
            newMinuteString = "0" + String.valueOf(newMinute);
        } else {
            newMinuteString = String.valueOf(newMinute);
        }

        if (String.valueOf(newHour).length() == 1) {
            newHourString = "0" + String.valueOf(newHour);
        } else {
            newHourString = String.valueOf(newHour);
        }

        if (String.valueOf(newDate).length() == 1) {
            newDateString = "0" + String.valueOf(newDate);
        } else {
            newDateString = String.valueOf(newDate);
        }

        if (String.valueOf(newMonth).length() == 1) {
            newMonthString = "0" + String.valueOf(newMonth);
        } else {
            newMonthString = String.valueOf(newMonth);
        }

        if (String.valueOf(newYear).length() == 1) {
            newYearString = "0" + String.valueOf(newYear);
        } else {
            newYearString = String.valueOf(newYear);
        }


        StringBuilder sb = new StringBuilder();

        sb.append(newYearString).append("-").append(newMonthString).append("-").append(newDateString).append(" ").append(newHourString)
                .append(":").append(newMinuteString).append(":").append(newSecondString);

        return sb.toString();
    }

    public void onBackPressed() {
        Intent intent = new Intent(VoteActivity.this, MainActivity.class);
        intent.putExtra("curUser", HomePageFragment.curUser);
        intent.putExtra("user", HomePageFragment.thisUser);
        intent.putExtra("nickname", HomePageFragment.thisUser.nickname);
        startActivity(intent);
    }
}
