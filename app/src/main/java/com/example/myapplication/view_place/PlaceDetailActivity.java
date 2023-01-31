package com.example.myapplication.view_place;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.myapplication.LocalDatabase.LocationEntity;
import com.example.myapplication.LocalDatabase.MyDatabase;
import com.example.myapplication.LocalDatabase.PeopleVote;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.VotedEvent;
import com.example.myapplication.account_management.AccountSettingActivity;
import com.example.myapplication.account_management.HomePageFragment;
import com.example.myapplication.account_management.LogInActivity;
import com.example.myapplication.account_management.SignUpActivity;
import com.example.myapplication.add_location_management.LocationDataHelper;
import com.example.myapplication.add_location_management.RemoteLocationHelper;
import com.example.myapplication.group_management.ManagerGroupActivity;
import com.example.myapplication.listViewAdapter.ListViewAdapter;
import com.example.myapplication.listViewAdapter.ListViewItem;
import com.example.myapplication.place_detail_adapter.PhotoAdapter;
import com.example.myapplication.place_detail_adapter.PhotoItem;
import com.example.myapplication.place_detail_adapter.ReasonAdapter;
import com.example.myapplication.place_detail_adapter.ReasonItem;
import com.example.myapplication.vote_management.PeopleVoteHelper;
import com.example.myapplication.vote_management.RemotePeopleVoteHelper;
import com.example.myapplication.vote_management.RemoteVoteDataHelper;
import com.example.myapplication.vote_management.VoteDataHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Logger;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PlaceDetailActivity extends AppCompatActivity {

    private List<ReasonItem> reasonItemList = new ArrayList<>();
    private List<PhotoItem> photoItemList = new ArrayList<>();

    TextView reasonAdd, photoAdd;

    boolean ifToMore = true;

    TextView nameView, addressView, typeView, voteView;

    String comments, photos;

    String[] split;
    String[] splitPhoto;

    PeopleVoteHelper localPeopleVoteHelper;

    VoteDataHelper localVoteHelper;

    int voteNum = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_detail);

        setTitle("Place Detail");

        final LocationDataHelper localLocationHelper = new RemoteLocationHelper();

        localPeopleVoteHelper = new RemotePeopleVoteHelper();

        localVoteHelper = new RemoteVoteDataHelper();

        nameView = findViewById(R.id.place_name);
        addressView = findViewById(R.id.address);
        typeView = findViewById(R.id.type);
        voteView = findViewById(R.id.vote);

        Intent intent = this.getIntent();
        String placeName = intent.getStringExtra("place_name");
        String placeAddress = intent.getStringExtra("place_address");
        String placeType = intent.getStringExtra("place_type");
        boolean ifVote = intent.getBooleanExtra("if_vote", false);


        addressView.setText(placeAddress);

        nameView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        nameView.setText(placeName);

        EventBus.getDefault().register(this);

        if (placeType == null || placeType.equals("")) {
            typeView.setVisibility(View.INVISIBLE);
        } else {
            typeView.setText("· " + placeType.substring(0, 1).toUpperCase() +
                    placeType.substring(1).toLowerCase());
        }


        localLocationHelper.getLocation(HomePageFragment.curGroupNumber, placeName, placeAddress)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<LocationEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(LocationEntity locationEntity) {
                        comments = locationEntity.comments;
                        photos = locationEntity.photos;

                        split = comments.split(";newOneComment;");
                        splitPhoto = photos.split(";newOne;");

                        initReasonItems();
                        initPhotoItems();

                        setReasonView();

                        RecyclerView recyclerPhotoView = (RecyclerView) findViewById(R.id.photos);
                        StaggeredGridLayoutManager layoutManager2 = new  StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                        recyclerPhotoView.setLayoutManager(layoutManager2);
                        PhotoAdapter adapter2 = new PhotoAdapter(photoItemList);
                        recyclerPhotoView.setAdapter(adapter2);

                        TextView moreOrLess = findViewById(R.id.see_all_less);

                        reasonAdd = findViewById(R.id.reason_add);
                        photoAdd = findViewById(R.id.photo_add);

                        reasonAdd.setOnClickListener(v -> {
                            Intent newIntent = new Intent(PlaceDetailActivity.this, AddReasonActivity.class);

                            newIntent.putExtra("place_name", placeName);
                            newIntent.putExtra("place_address", placeAddress);
                            newIntent.putExtra("place_type", placeType);
                            newIntent.putExtra("comments", comments);

                            startActivity(newIntent);
                        });

                        photoAdd.setOnClickListener(v -> {
                            Intent newIntent = new Intent(PlaceDetailActivity.this, AddPhotoActivity.class);

                            newIntent.putExtra("place_name", placeName);
                            newIntent.putExtra("place_address", placeAddress);
                            newIntent.putExtra("place_type", placeType);

                            startActivity(newIntent);
                        });

                        moreOrLess.setOnClickListener(v -> {
                            if (ifToMore) {
                                ifToMore = false;
                                moreReasonItems();
                                setReasonView();
                                moreOrLess.setText("↑ See less");
                            } else {
                                ifToMore = true;
                                initReasonItems();
                                setReasonView();
                                moreOrLess.setText("↓ See all");
                            }
                        });





                        if (!ifVote || !HomePageFragment.ifHaveCurrentVote) {
                            voteView.setVisibility(View.GONE);
                        } else {
                            voteView.setVisibility(View.VISIBLE);
                            localPeopleVoteHelper.getVotePeople(HomePageFragment.curGroupNumber, locationEntity.locationName,
                                            locationEntity.locationAddress, HomePageFragment.currentVote.voteStartTime, HomePageFragment.curUser)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new SingleObserver<PeopleVote>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onSuccess(PeopleVote peopleVote) {

                                            localVoteHelper.getNumberOfNotesOneGroupOneTime(HomePageFragment.curGroupNumber, HomePageFragment.currentVote.voteStartTime,
                                                            locationEntity.locationName,
                                                            locationEntity.locationAddress)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(new SingleObserver<Integer>() {
                                                        @Override
                                                        public void onSubscribe(Disposable d) {

                                                        }

                                                        @Override
                                                        public void onSuccess(Integer integer) {
                                                            voteView.setBackgroundResource(R.drawable.grey_background);

                                                            voteView.setText(integer + " voted");
                                                            voteView.setTextColor(Color.parseColor("#333232"));

                                                            voteView.setClickable(false);
                                                        }

                                                        @Override
                                                        public void onError(Throwable e) {
                                                            voteView.setBackgroundResource(R.drawable.grey_background);

                                                            voteView.setText(0 + " voted");
                                                            voteView.setTextColor(Color.parseColor("#333232"));

                                                            voteView.setClickable(false);
                                                        }
                                                    });

                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                            voteView.setOnClickListener(v -> {
                                                localPeopleVoteHelper.insertOnePeopleVote(HomePageFragment.curGroupNumber, HomePageFragment.curUser, locationEntity.locationName,
                                                                locationEntity.locationAddress, "true", HomePageFragment.currentVote.voteStartTime)
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(new CompletableObserver() {
                                                            @Override
                                                            public void onSubscribe(Disposable d) {

                                                            }

                                                            @Override
                                                            public void onComplete() {

                                                                localVoteHelper.updateTheVotes(HomePageFragment.curGroupNumber, locationEntity.locationName,
                                                                                locationEntity.locationAddress, HomePageFragment.currentVote.voteStartTime)
                                                                        .subscribeOn(Schedulers.io())
                                                                        .observeOn(AndroidSchedulers.mainThread())
                                                                        .subscribe(new CompletableObserver() {
                                                                            @Override
                                                                            public void onSubscribe(Disposable d) {

                                                                            }

                                                                            @Override
                                                                            public void onComplete() {
                                                                                Toast.makeText(PlaceDetailActivity.this, "Voted successfully", Toast.LENGTH_SHORT).show();

                                                                                localVoteHelper.getNumberOfNotesOneGroupOneTime(HomePageFragment.curGroupNumber, HomePageFragment.currentVote.voteStartTime,
                                                                                                locationEntity.locationName,
                                                                                                locationEntity.locationAddress)
                                                                                        .subscribeOn(Schedulers.io())
                                                                                        .observeOn(AndroidSchedulers.mainThread())
                                                                                        .subscribe(new SingleObserver<Integer>() {
                                                                                            @Override
                                                                                            public void onSubscribe(Disposable d) {

                                                                                            }

                                                                                            @Override
                                                                                            public void onSuccess(Integer integer) {
                                                                                                voteView.setBackgroundResource(R.drawable.grey_background);

                                                                                                voteView.setText(integer + " voted");
                                                                                                voteView.setTextColor(Color.parseColor("#333232"));
                                                                                                voteNum = integer;

                                                                                                voteView.setClickable(false);

                                                                                                EventBus.getDefault().post(new VotedEvent());
                                                                                            }

                                                                                            @Override
                                                                                            public void onError(Throwable e) {
                                                                                                voteView.setBackgroundResource(R.drawable.grey_background);

                                                                                                voteView.setText(0 + " voted");
                                                                                                voteView.setTextColor(Color.parseColor("#333232"));

                                                                                                voteView.setClickable(false);
                                                                                            }
                                                                                        });
                                                                            }

                                                                            @Override
                                                                            public void onError(Throwable e) {

                                                                            }
                                                                        });
                                                            }

                                                            @Override
                                                            public void onError(Throwable e) {
                                                                Toast.makeText(PlaceDetailActivity.this, "You have voted for this place previously", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            });

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });



    }

    private void initReasonItems() {
        reasonItemList.clear();

        if (split.length == 1) {
            ReasonItem item = new ReasonItem(split[0]);
            reasonItemList.add(item);
        } else {
            for (int i = 0; i < 2; i++) {
                ReasonItem item = new ReasonItem(split[i]);
                reasonItemList.add(item);
            }
        }


    }

    private void moreReasonItems() {
        reasonItemList.clear();


        for (int i = 0; i < split.length; i++) {
            ReasonItem item = new ReasonItem(split[i]);
            reasonItemList.add(item);
        }


    }

    private void initPhotoItems() {

        for (int i = 0; i < splitPhoto.length; i++) {
            PhotoItem item = new PhotoItem(base64ToBitmap(splitPhoto[i]));
            photoItemList.add(item);
        }

    }

    private void setReasonView() {
        RecyclerView recyclerReasonView = (RecyclerView) findViewById(R.id.reasons_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerReasonView.setLayoutManager(layoutManager);
        ReasonAdapter adapter = new ReasonAdapter(reasonItemList);
        recyclerReasonView.setAdapter(adapter);
    }

    // Start page
    public static void clickStart(Context context, String placeName, String placeAddress, String placeType, boolean ifVote) {
        Intent intent = new Intent(context, PlaceDetailActivity.class);
        intent.putExtra("place_name", placeName);
        intent.putExtra("place_address", placeAddress);
        intent.putExtra("place_type", placeType);
        intent.putExtra("if_vote", ifVote);
        // context.startActivity(intent);
        ((Activity)context).startActivityForResult(intent, 1);
    }

    public static Bitmap base64ToBitmap(String base64Data) {
        Log.i("PlaceDetailActivity", "base64ToBitmap, base64Data: " + base64Data);
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private Bitmap drawableToBitamp(Drawable drawable) {

        Bitmap bitmap = null;

        int width = drawable.getIntrinsicWidth();

        int height = drawable.getIntrinsicHeight();

        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;

        bitmap = Bitmap.createBitmap(width,height,config);

        Canvas canvas = new Canvas(bitmap);

        drawable.setBounds(0, 0, width, height);

        drawable.draw(canvas);
        return bitmap;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(VotedEvent event) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);


    }

    @Override
    protected void onStop() {
        super.onStop();

        PlaceDetailActivity.this.finish();
    }



    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("data_return", voteNum);
        setResult(RESULT_OK, intent);

        finish();

    }


}
