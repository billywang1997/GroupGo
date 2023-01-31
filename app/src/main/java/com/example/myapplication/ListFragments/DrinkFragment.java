package com.example.myapplication.ListFragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.LocalDatabase.LocationEntity;
import com.example.myapplication.LocalDatabase.VoteEntity;
import com.example.myapplication.PlaceType;
import com.example.myapplication.R;
import com.example.myapplication.TimeOutEvent;
import com.example.myapplication.VotedEvent;
import com.example.myapplication.account_management.HomePageFragment;
import com.example.myapplication.listViewAdapter.ListViewAdapter;
import com.example.myapplication.listViewAdapter.ListViewItem;
import com.example.myapplication.vote_management.RemoteVoteDataHelper;
import com.example.myapplication.vote_management.VoteDataHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DrinkFragment extends Fragment {

    private List<ListViewItem> itemList = new ArrayList<>();
    public List<ListViewItem> itemAllList = new ArrayList<>();

    View view;

    public ListViewAdapter getAdapter() {
        return adapter;
    }

    public ListViewAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.location_list_page, container, false);
        itemList = new ArrayList<>();
        itemAllList = new ArrayList<>();
        final VoteDataHelper localVoteHelper = new RemoteVoteDataHelper();
        EventBus.getDefault().register(this);

        localVoteHelper.getHasCurrentVote(HomePageFragment.addCreateTime(), HomePageFragment.curGroupNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<VoteEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(VoteEntity voteEntity) {
                        HomePageFragment.ifHaveCurrentVote = true;

                        initItems();

                        if (itemList.isEmpty()) {
                            view = inflater.inflate(R.layout.nothing, container, false);
                        } else {
                            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.location_list);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                            recyclerView.setLayoutManager(layoutManager);
                            adapter = new ListViewAdapter(itemList);
                            recyclerView.setAdapter(adapter);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        HomePageFragment.ifHaveCurrentVote = false;

                        initItems();

                        if (itemList.isEmpty()) {
                            view = inflater.inflate(R.layout.nothing, container, false);
                        } else {
                            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.location_list);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                            recyclerView.setLayoutManager(layoutManager);
                            adapter = new ListViewAdapter(itemList);
                            recyclerView.setAdapter(adapter);
                        }

                    }
                });

        return view;
    }

    private void initItems() {

        Bitmap img1, img2, img3;

        Bitmap defaultMap = drawableToBitamp(AppCompatResources.getDrawable(getContext(), R.drawable.blank_img));

        if (HomePageFragment.ifHaveCurrentVote) {

            Set<String> voteLocations = new HashSet<>();

            for (VoteEntity location : HomePageFragment.curVoteList) {

                if (location.locationTag != null && location.locationTag.equalsIgnoreCase("drink")) {
                    if (location.photos == null) {
                        img1 = defaultMap;
                        img2 = defaultMap;
                        img3 = defaultMap;
                    } else {
                        String[] split = location.photos.split(";newOne;");


                        if (split ==  null || split.length == 0) {

                            img1 = defaultMap;
                            img2 = defaultMap;
                            img3 = defaultMap;

                        } else if (split.length == 1) {

                            img1 = (base64ToBitmap(split[0]));
                            img2 = defaultMap;
                            img3 = defaultMap;
                        } else if (split.length == 2) {

                            img1 = base64ToBitmap(split[0]);
                            img2 = base64ToBitmap(split[1]);
                            img3 = defaultMap;
                        } else {
                            img1 = base64ToBitmap(split[0]);
                            img2 = base64ToBitmap(split[1]);
                            img3 = base64ToBitmap(split[2]);
                        }
                    }

                    PlaceType placeType;

                    if (location.locationTag != null && !location.locationTag.isEmpty()) {
                        placeType = PlaceType.valueOf(location.locationTag.toUpperCase());
                    } else {
                        placeType = null;
                    }

                    String firstComment = location.comments.split(";newOneComment;")[0];


                    ListViewItem item = new ListViewItem(location.locationName, location.locationAddress,
                            firstComment, placeType, img1, img2, img3, HomePageFragment.ifHaveCurrentVote);

                    itemList.add(item);
                    itemAllList.add(item);

                    voteLocations.add(location.locationName + location.locationAddress);
                }
            }

            for (LocationEntity location : HomePageFragment.curLocationList) {

                if (!voteLocations.contains(location.locationName + location.locationAddress)) {
                    if (location.locationTag != null && location.locationTag.equalsIgnoreCase("drink")) {
                        if (location.photos == null) {
                            img1 = defaultMap;
                            img2 = defaultMap;
                            img3 = defaultMap;
                        } else {
                            String[] split = location.photos.split(";newOne;");


                            if (split ==  null || split.length == 0) {

                                img1 = defaultMap;
                                img2 = defaultMap;
                                img3 = defaultMap;

                            } else if (split.length == 1) {

                                img1 = (base64ToBitmap(split[0]));
                                img2 = defaultMap;
                                img3 = defaultMap;
                            } else if (split.length == 2) {

                                img1 = base64ToBitmap(split[0]);
                                img2 = base64ToBitmap(split[1]);
                                img3 = defaultMap;
                            } else {
                                img1 = base64ToBitmap(split[0]);
                                img2 = base64ToBitmap(split[1]);
                                img3 = base64ToBitmap(split[2]);
                            }
                        }

                        PlaceType placeType;

                        if (location.locationTag != null && !location.locationTag.isEmpty()) {
                            placeType = PlaceType.valueOf(location.locationTag.toUpperCase());
                        } else {
                            placeType = null;
                        }

                        String firstComment = location.comments.split(";newOneComment;")[0];


                        ListViewItem item = new ListViewItem(location.locationName, location.locationAddress,
                                firstComment, placeType, img1, img2, img3, false);

                        itemList.add(item);
                        itemAllList.add(item);
                    }

                }
            }

            voteLocations.clear();

        } else {
            for (LocationEntity location : HomePageFragment.curLocationList) {

                if (location.locationTag != null && location.locationTag.equalsIgnoreCase("drink")) {
                    if (location.photos == null) {
                        img1 = defaultMap;
                        img2 = defaultMap;
                        img3 = defaultMap;
                    } else {
                        String[] split = location.photos.split(";newOne;");


                        if (split ==  null || split.length == 0) {

                            img1 = defaultMap;
                            img2 = defaultMap;
                            img3 = defaultMap;

                        } else if (split.length == 1) {

                            img1 = (base64ToBitmap(split[0]));
                            img2 = defaultMap;
                            img3 = defaultMap;
                        } else if (split.length == 2) {

                            img1 = base64ToBitmap(split[0]);
                            img2 = base64ToBitmap(split[1]);
                            img3 = defaultMap;
                        } else {
                            img1 = base64ToBitmap(split[0]);
                            img2 = base64ToBitmap(split[1]);
                            img3 = base64ToBitmap(split[2]);
                        }
                    }

                    PlaceType placeType;

                    if (location.locationTag != null && !location.locationTag.isEmpty()) {
                        placeType = PlaceType.valueOf(location.locationTag.toUpperCase());
                    } else {
                        placeType = null;
                    }

                    String firstComment = location.comments.split(";newOneComment;")[0];


                    ListViewItem item = new ListViewItem(location.locationName, location.locationAddress,
                            firstComment, placeType, img1, img2, img3, HomePageFragment.ifHaveCurrentVote);

                    itemList.add(item);
                    itemAllList.add(item);
                }
            }
        }


    }


    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    private Bitmap drawableToBitamp(Drawable drawable)
    {

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        EventBus.getDefault().unregister(this);


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventFragment(TimeOutEvent event) {
        if ( ! itemList.isEmpty()) {
            itemList.clear();
            itemAllList.clear();
            initItems();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(VotedEvent event) {

        if ( ! itemList.isEmpty()) {
            itemList.clear();
            itemAllList.clear();
            initItems();
        }

    }



}