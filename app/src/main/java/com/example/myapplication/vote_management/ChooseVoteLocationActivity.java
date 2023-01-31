package com.example.myapplication.vote_management;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.ClickEvent;
import com.example.myapplication.ListFragments.AllChooseFragment;
import com.example.myapplication.ListFragments.AllFragment;
import com.example.myapplication.ListFragments.DrinkChooseFragment;
import com.example.myapplication.ListFragments.DrinkFragment;
import com.example.myapplication.ListFragments.FoodChooseFragment;
import com.example.myapplication.ListFragments.FoodFragment;
import com.example.myapplication.ListFragments.IndoorChooseFragment;
import com.example.myapplication.ListFragments.IndoorFragment;
import com.example.myapplication.ListFragments.OutdoorChooseFragment;
import com.example.myapplication.ListFragments.OutdoorFragment;
import com.example.myapplication.LocalDatabase.LocationEntity;
import com.example.myapplication.PlaceType;
import com.example.myapplication.R;
import com.example.myapplication.account_management.HomePageFragment;
import com.example.myapplication.account_management.LogInActivity;
import com.example.myapplication.account_management.SignUpActivity;
import com.example.myapplication.listViewAdapter.ListViewAdapter;
import com.example.myapplication.listViewAdapter.ListViewItem;
import com.example.myapplication.vote_place_adapter.VotePlaceAdapter;
import com.example.myapplication.vote_place_adapter.VotePlaceItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChooseVoteLocationActivity extends AppCompatActivity {

    private List<Fragment> fragmentList;
    private ViewPager2 vp;
    private TabLayout tl;

    TextView selectAllView, finishButton;

    public static Set<String> clickedSet = new HashSet<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_location_page);

        setTitle("Choose Locations For Voting");

        Intent intent = this.getIntent();
        String activityName = intent.getStringExtra("activity_name");
        String hourNum = intent.getStringExtra("hour_num");
        String minuteNum = intent.getStringExtra("minute_num");
        String secondNum = intent.getStringExtra("second_num");

        vp = (ViewPager2) findViewById(R.id.pager);
        tl = (TabLayout) findViewById(R.id.tab_layout);

        selectAllView = findViewById(R.id.tv_select_all);
        finishButton = findViewById(R.id.tv_finish_selection);

        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new AllChooseFragment());
        fragmentList.add(new FoodChooseFragment());
        fragmentList.add(new DrinkChooseFragment());
        fragmentList.add(new IndoorChooseFragment());
        fragmentList.add(new OutdoorChooseFragment());

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
        };

        vp.setAdapter(adapter);

        ArrayList<String> tabList = new ArrayList<String>();
        tabList.add("All");
        tabList.add("Food");
        tabList.add("Drink");
        tabList.add("Indoor Activity");
        tabList.add("Outdoor Activity");


        new TabLayoutMediator(tl, vp, true, true, (tab, position) -> tab.setText(tabList.get(position))).attach();

        selectAllView.setOnClickListener(v -> {
            int curItem = vp.getCurrentItem();

            if (curItem == 0) {
                for (LocationEntity curLocation : HomePageFragment.curLocationList) {
                    clickedSet.add(curLocation.locationName + ";nameThenAddress;" + curLocation.locationAddress);
                }
            } else if (curItem == 1) {
                for (LocationEntity curLocation : HomePageFragment.curLocationList) {
                    if (curLocation.locationTag != null && curLocation.locationTag.equals("food")) {
                        clickedSet.add(curLocation.locationName + ";nameThenAddress;" + curLocation.locationAddress);
                    }
                }

            } else if (curItem == 2) {
                for (LocationEntity curLocation : HomePageFragment.curLocationList) {
                    if (curLocation.locationTag != null && curLocation.locationTag.equals("drink")) {
                        clickedSet.add(curLocation.locationName + ";nameThenAddress;" + curLocation.locationAddress);
                    }
                }

            } else if (curItem == 3) {

                for (LocationEntity curLocation : HomePageFragment.curLocationList) {
                    if (curLocation.locationTag != null && curLocation.locationTag.equals("indoor_activity")) {
                        clickedSet.add(curLocation.locationName + ";nameThenAddress;" + curLocation.locationAddress);
                    }
                }

            } else if (curItem == 4) {

                for (LocationEntity curLocation : HomePageFragment.curLocationList) {
                    if (curLocation.locationTag != null && curLocation.locationTag.equals("outdoor_activity")) {
                        clickedSet.add(curLocation.locationName + ";nameThenAddress;" + curLocation.locationAddress);
                    }
                }

            }

            EventBus.getDefault().post(new ClickEvent());


        });

        finishButton.setOnClickListener(v -> {
            int numberOfPlaces = clickedSet.size();
            Intent newIntent = new Intent( ChooseVoteLocationActivity.this, VoteActivity.class)
                    .putExtra("number_of_places", String.valueOf(numberOfPlaces));

            if (activityName != null && !activityName.isEmpty()) {
                newIntent.putExtra("activity_name", activityName);
            }

            if (hourNum != null && !hourNum.isEmpty()) {
                newIntent.putExtra("hour_num", hourNum);
            }

            if (secondNum != null && !secondNum.isEmpty()) {
                newIntent.putExtra("second_num", secondNum);
            }

            if (minuteNum != null && !minuteNum.isEmpty()) {
                newIntent.putExtra("minute_num", minuteNum);
            }

            startActivity(newIntent);
        });


    }

    public void finishPage() {
        ChooseVoteLocationActivity.this.finish();
    }



}
