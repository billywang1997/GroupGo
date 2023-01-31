package com.example.myapplication.account_management;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.myapplication.ClickEvent;
import com.example.myapplication.CountDown;
import com.example.myapplication.LocalDatabase.GroupEntity;
import com.example.myapplication.LocalDatabase.LocationEntity;
import com.example.myapplication.LocalDatabase.MyDatabase;
import com.example.myapplication.LocalDatabase.UserEntity;
import com.example.myapplication.LocalDatabase.VoteEntity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.TimeOutEvent;
import com.example.myapplication.add_location_management.AddLocationActivity;
import com.example.myapplication.add_location_management.LocationDataHelper;
import com.example.myapplication.add_location_management.MapsActivityCurrentPlace;
import com.example.myapplication.add_location_management.RemoteLocationHelper;
import com.example.myapplication.group_management.GroupDataHelper;
import com.example.myapplication.group_management.GroupManageHelper;
import com.example.myapplication.group_management.GroupMembersActivity;
import com.example.myapplication.group_management.ManageGroupActivity;
import com.example.myapplication.group_management.ManagerGroupActivity;
import com.example.myapplication.group_management.MyGroupsActivity;
import com.example.myapplication.group_management.RemoteGroupHelper;
import com.example.myapplication.group_management.RemoteGroupManageHelper;
import com.example.myapplication.vote_management.RemoteVoteDataHelper;
import com.example.myapplication.vote_management.VoteActivity;
import com.example.myapplication.vote_management.VoteDataHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Logger;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HomePageFragment extends Fragment {

    public static String curUser;
    public static UserEntity thisUser;

    public static int curGroupNumber;
    public static String groupName;

    public static VoteEntity currentVote;

    GroupDataHelper localGroupHelper;
    GroupManageHelper localGroupManageHelper;

    TextView createVoteView;

    ConstraintLayout lastVotingLayout;
    ConstraintLayout currentVotingLayout;

    TextView lastVotingResultView, currentActivityView;

    public static List<LocationEntity> curLocationList;
    public static List<VoteEntity> curVoteList;

    public static boolean ifHaveCurrentVote;
    public static String currentVoteStartTime;

    CountDown countDown;
    TextView countdownHint;

    boolean ifManagerNow = false;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_page, container, false);

        Intent intent = getActivity().getIntent();

        SharedPreferences.Editor ed = getActivity().getSharedPreferences("groupRecord", Context.MODE_PRIVATE).edit();
        SharedPreferences sh = getActivity().getSharedPreferences("groupRecord", Context.MODE_PRIVATE);

        String curUsername = intent.getStringExtra("curUser");
        System.out.println("=========================curUser" + curUsername);
        String nickname = intent.getStringExtra("nickname");
        thisUser = (UserEntity)intent.getSerializableExtra("user");

        createVoteView = view.findViewById(R.id.create);

        TextView groupNameView = view.findViewById(R.id.group_name);

        lastVotingLayout = view.findViewById(R.id.layout2);
        currentVotingLayout = view.findViewById(R.id.layout3);

        lastVotingResultView = view.findViewById(R.id.result_name);
        currentActivityView = view.findViewById(R.id.activity_name);
        currentActivityView.setSelected(true);
        countDown = view.findViewById(R.id.time_countdown);
        countdownHint = view.findViewById(R.id.countdown);



        final LocationDataHelper localLocationHelper = new RemoteLocationHelper();
        final VoteDataHelper localVoteHelper = new RemoteVoteDataHelper();

        EventBus.getDefault().register(this);



        if (groupName == null || groupName.isEmpty()) {

            String lastOpenedGroup = sh.getString(curUsername + "group_name","");

            if (lastOpenedGroup == null || lastOpenedGroup.isEmpty() || lastOpenedGroup.equals("") || lastOpenedGroup == "") {
                groupNameView.setText("Please select a current group first");
                groupNameView.setTextColor(Color.parseColor("#D9D9D9"));

                lastVotingLayout.setVisibility(View.GONE);

                currentActivityView.setText("None");
                currentActivityView.setTextColor(Color.parseColor("#D9D9D9"));

                ifHaveCurrentVote = false;

            } else {
                groupNameView.setText(lastOpenedGroup);
                curGroupNumber = sh.getInt(curUsername + "group_id",-1);
                groupName = lastOpenedGroup;

                localVoteHelper.getPreviousVote(addCreateTime(), curGroupNumber)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<VoteEntity>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(VoteEntity voteEntity) {
                                lastVotingResultView.setText(voteEntity.locationName);
                            }

                            @Override
                            public void onError(Throwable e) {
                                lastVotingLayout.setVisibility(View.GONE);
                            }
                        });

                Log.i("HomePageFragment", "getAllLocation start");
                localLocationHelper.getAllLocation(HomePageFragment.curGroupNumber)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<List<LocationEntity>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(List<LocationEntity> locationEntities) {
                                curLocationList = locationEntities;
                                Log.i("HomePageFragment", "getAllLocation size: " + curLocationList.size());
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("HomePageFragment", "onError: " + e.toString());
                            }
                        });


                localVoteHelper.getHasCurrentVote(addCreateTime(), curGroupNumber)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<VoteEntity>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(VoteEntity voteEntity) {
                                currentVote = voteEntity;
                                Log.i("HomePageFragment", "getHasCurrentVote, setText");
                                currentActivityView.setText(voteEntity.activityName);
                                currentActivityView.setSelected(true);
                                currentActivityView.setTextColor(Color.parseColor("#333232"));

                                ifHaveCurrentVote = true;

                                createVoteView.setVisibility(View.INVISIBLE);

                                countDown.setVisibility(View.VISIBLE);
                                countdownHint.setVisibility(View.VISIBLE);
                                countDown.setData(currentVote.voteOverTime, addCreateTime());



                                localVoteHelper.getAllVotes(curGroupNumber, voteEntity.voteStartTime)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new SingleObserver<List<VoteEntity>>() {
                                            @Override
                                            public void onSubscribe(Disposable d) {

                                            }

                                            @Override
                                            public void onSuccess(List<VoteEntity> voteEntities) {
                                                curVoteList = voteEntities;
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                Toast.makeText(getActivity().getApplicationContext(), e.toString(), Toast.LENGTH_SHORT);
                                            }
                                        });



                            }

                            @Override
                            public void onError(Throwable e) {
                                currentActivityView.setText("None");
                                currentActivityView.setTextColor(Color.parseColor("#D9D9D9"));

                                ifHaveCurrentVote = false;

                                EventBus.getDefault().post(new TimeOutEvent());

                            }
                        });


            }


        } else {
            groupNameView.setText(groupName);

            ed.putInt(curUsername + "group_id", curGroupNumber);
            ed.putString(curUsername + "group_name", groupName);
            ed.apply();

            localVoteHelper.getPreviousVote(addCreateTime(), curGroupNumber)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<VoteEntity>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(VoteEntity voteEntity) {
                            lastVotingResultView.setText(voteEntity.locationName);


                        }

                        @Override
                        public void onError(Throwable e) {
                            lastVotingLayout.setVisibility(View.GONE);
                        }
                    });

            Log.i("HomePageFragment", "getAllLocation2 start groupNumber: " + HomePageFragment.curGroupNumber);
            localLocationHelper.getAllLocation(HomePageFragment.curGroupNumber)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<List<LocationEntity>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(List<LocationEntity> locationEntities) {
                            curLocationList = locationEntities;
                            Log.i("HomePageFragment", "getAllLocation2 size: " + curLocationList.size() + ", groupNumber: " + HomePageFragment.curGroupNumber);
                        }

                        @Override
                        public void onError(Throwable e) {
                            curLocationList = new ArrayList<>();
                            Log.e("getAllLocation2 onError", "============heyheyheyheyheyheyheyWrong : " + e.toString());
                        }
                    });

            localVoteHelper.getHasCurrentVote(addCreateTime(), curGroupNumber)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<VoteEntity>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(VoteEntity voteEntity) {
                            currentVote = voteEntity;
                            Log.i("HomePageFragment", "getHasCurrentVote, setText");
                            currentActivityView.setText(voteEntity.activityName);
                            currentActivityView.setSelected(true);
                            currentActivityView.setTextColor(Color.parseColor("#333232"));

                            ifHaveCurrentVote = true;

                            createVoteView.setVisibility(View.INVISIBLE);

                            countDown.setVisibility(View.VISIBLE);
                            countdownHint.setVisibility(View.VISIBLE);
                            countDown.setData(currentVote.voteOverTime, addCreateTime());

                            localVoteHelper.getAllVotes(curGroupNumber, voteEntity.voteStartTime)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new SingleObserver<List<VoteEntity>>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onSuccess(List<VoteEntity> voteEntities) {
                                            curVoteList = voteEntities;
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            if (getActivity().getApplicationContext() != null) {
                                                Toast.makeText(getActivity().getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }

                        @Override
                        public void onError(Throwable e) {
                            currentActivityView.setText("None");
                            currentActivityView.setTextColor(Color.parseColor("#D9D9D9"));

                            ifHaveCurrentVote = false;

                            EventBus.getDefault().post(new TimeOutEvent());

                        }
                    });

        }

        curUser = curUsername;

        localGroupHelper = new RemoteGroupHelper();
        localGroupManageHelper = new RemoteGroupManageHelper();

        TextView addLocationView = view.findViewById(R.id.tv_add_location);
        TextView manageGroupView = view.findViewById(R.id.tv_manage_group);


        LinearLayout linearLayout = view.findViewById(R.id.linear);

        localGroupHelper.findLatestGroupsByManager(curUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<GroupEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<GroupEntity> groupEntities) {
                        if (groupEntities.isEmpty()) {
                            manageGroupView.setVisibility(View.GONE);

                            createVoteView.setVisibility(View.INVISIBLE);
                        } else {

                            for (GroupEntity groupEntity : groupEntities) {
                                if (groupEntity.groupNumber == curGroupNumber && groupEntity.groupManager.equals(curUser)) {
                                    ifManagerNow = true;
                                    break;
                                }
                            }

                            if (ifManagerNow) {
                                createVoteView.setOnClickListener(v -> {
                                    Intent newIntent = new Intent(getActivity(), VoteActivity.class);
                                    startActivity(newIntent);
                                });
                            } else {
                                manageGroupView.setVisibility(View.GONE);

                                createVoteView.setVisibility(View.INVISIBLE);
                            }

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        manageGroupView.setVisibility(View.GONE);

                        createVoteView.setVisibility(View.INVISIBLE);
                    }
                });



        manageGroupView.setOnClickListener(v -> {
            Intent newIntent = new Intent(getActivity(), ManagerGroupActivity.class);
            startActivity(newIntent);
        });


        ImageView settingView = view.findViewById(R.id.setting);

        settingView.setOnClickListener(v -> {

            Intent newIntent = new Intent(getActivity(), AccountSettingActivity.class)
                    .putExtra("nickname", nickname);
            startActivity(newIntent);
        });


        TextView allGroupsView = view.findViewById(R.id.all);

        allGroupsView.setOnClickListener(v -> {

            Intent newIntent = new Intent(getActivity(), MyGroupsActivity.class);
            startActivity(newIntent);
        });

        addLocationView.setOnClickListener(v -> {

            if (curGroupNumber == 0 || groupName == null || groupName.isEmpty()) {
                Toast.makeText(getActivity(), "Please choose a current group first", Toast.LENGTH_SHORT).show();
            } else {
                Intent newIntent = new Intent(getActivity(), MapsActivityCurrentPlace.class);
                startActivity(newIntent);
            }

        });




        return view;


    }

    // Start page
    public static void clickStartHome(Context context, int GroupId, String groupName) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("group_id", GroupId);
        intent.putExtra("group_name", groupName);
        intent.putExtra("curUser", HomePageFragment.curUser);
        intent.putExtra("user", HomePageFragment.thisUser);
        intent.putExtra("nickname", HomePageFragment.thisUser.nickname);
        context.startActivity(intent);
    }

    public static String addCreateTime() {

        TimeZone tz = TimeZone.getTimeZone("UTC");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(tz);
        Date dateformatnew = new Date();
        String createTime = dateFormat.format(dateformatnew);

        return createTime;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        EventBus.getDefault().unregister(this);


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventHome(TimeOutEvent event) {
        ifHaveCurrentVote = false;

        countDown.setVisibility(View.GONE);
        countdownHint.setVisibility(View.GONE);

        if (ifManagerNow) {
            createVoteView.setVisibility(View.VISIBLE);
        }

    }

    @Subscribe
    public void onLogOutEvent(LogOutEvent logOutEvent) {
        curUser = "";
        thisUser = null;
        curGroupNumber = -1;
        groupName = "";
        currentVote = null;
        if (curLocationList != null) {
            curLocationList.clear();
        }
        if (curVoteList != null) {
            curVoteList.clear();
        }
        ifHaveCurrentVote = false;
        currentVoteStartTime = "";
    }


}
