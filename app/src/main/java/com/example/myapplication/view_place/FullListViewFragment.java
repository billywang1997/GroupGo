package com.example.myapplication.view_place;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.ListFragments.AllFragment;
import com.example.myapplication.ListFragments.DrinkFragment;
import com.example.myapplication.ListFragments.FoodFragment;
import com.example.myapplication.ListFragments.IndoorFragment;
import com.example.myapplication.ListFragments.OutdoorFragment;
import com.example.myapplication.LocalDatabase.LocationEntity;
import com.example.myapplication.LocalDatabase.VoteEntity;
import com.example.myapplication.R;
import com.example.myapplication.TimeOutEvent;
import com.example.myapplication.account_management.HomePageFragment;
import com.example.myapplication.add_location_management.LocationDataHelper;
import com.example.myapplication.add_location_management.RemoteLocationHelper;
import com.example.myapplication.vote_management.RemoteVoteDataHelper;
import com.example.myapplication.vote_management.VoteDataHelper;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FullListViewFragment extends Fragment {

    private List<Fragment> fragmentList;
    private ViewPager2 vp;
    private TabLayout tl;
    private SearchView searchView;

    VoteDataHelper localVoteHelper;
    LocationDataHelper localLocationHelper;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.list_complete_fragment, container, false);


        vp = (ViewPager2) view.findViewById(R.id.pager);
        vp.setSaveEnabled(false);
        tl = (TabLayout) view.findViewById(R.id.tab_layout);

        searchView = view.findViewById(R.id.search_bar);

        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new AllFragment());
        fragmentList.add(new FoodFragment());
        fragmentList.add(new DrinkFragment());
        fragmentList.add(new IndoorFragment());
        fragmentList.add(new OutdoorFragment());

        localVoteHelper = new RemoteVoteDataHelper();
        localLocationHelper = new RemoteLocationHelper();
        FragmentStateAdapter adapter = new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getItemCount() {
                return fragmentList.size();
            }

            @Override
            public long getItemId(int position) {
                return position;
            }


        };

        vp.setAdapter(adapter);

        ArrayList<String> tabList = new ArrayList<String>();
        tabList.add("All");
        tabList.add("Food");
        tabList.add("Drink");
        tabList.add("Indoor Activity");
        tabList.add("Outdoor Activity");


        new TabLayoutMediator(tl, vp, false, true, (tab, position) -> tab.setText(tabList.get(position))).attach();


        vp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                searchView.setIconifiedByDefault(false);
                searchView.setQuery("", false);
                searchView.clearFocus();


            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("FullListViewFragment", "getAllLocations start");
        localLocationHelper.getAllLocation(HomePageFragment.curGroupNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<LocationEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<LocationEntity> locationEntities) {
                        HomePageFragment.curLocationList = locationEntities;
                        Log.i("FullListViewFragment", "getAllLocation size: " + HomePageFragment.curLocationList.size());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("FullListViewFragment", "getAllLocation error: " + e.toString());

                    }
                });

        localVoteHelper.getHasCurrentVote(HomePageFragment.addCreateTime(), HomePageFragment.curGroupNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<VoteEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(VoteEntity voteEntity) {
                        HomePageFragment.currentVote = voteEntity;



                        HomePageFragment.ifHaveCurrentVote = true;


                        localVoteHelper.getAllVotes(HomePageFragment.curGroupNumber, voteEntity.voteStartTime)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new SingleObserver<List<VoteEntity>>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onSuccess(List<VoteEntity> voteEntities) {
                                        HomePageFragment.curVoteList = voteEntities;
                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }
                                });

                    }

                    @Override
                    public void onError(Throwable e) {

                        HomePageFragment.ifHaveCurrentVote = false;

                        EventBus.getDefault().post(new TimeOutEvent());

                    }
                });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;


            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {


                // mListView.setFilterText(newText);

                int tabNum = tl.getSelectedTabPosition();

                Fragment myFragment = getChildFragmentManager().findFragmentByTag("f" + tabNum);

                if (myFragment != null) {
                    if (tabNum == 0) {

                        if ( ((AllFragment)myFragment).getAdapter() != null ) {
                            ((AllFragment)myFragment).getAdapter().filter(newText);
                        }

                    } else if (tabNum == 1) {

                        if ( ((FoodFragment)myFragment).getAdapter() != null) {
                            ((FoodFragment)myFragment).getAdapter().filter(newText);
                        }


                    } else if (tabNum == 2) {

                        if ( ((DrinkFragment)myFragment).getAdapter() != null) {
                            ((DrinkFragment)myFragment).getAdapter().filter(newText);
                        }

                    } else if (tabNum == 3) {

                        if (((IndoorFragment)myFragment).getAdapter() != null) {
                            ((IndoorFragment)myFragment).getAdapter().filter(newText);
                        }

                    } else if (tabNum == 4) {

                        if (((OutdoorFragment)myFragment).getAdapter() != null) {
                            ((OutdoorFragment)myFragment).getAdapter().filter(newText);
                        }

                    }

                }


                return false;
            }
        });


    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
