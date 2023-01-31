package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.*;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.account_management.AccountSettingActivity;
import com.example.myapplication.account_management.HomePageFragment;
import com.example.myapplication.account_management.LogInActivity;
import com.example.myapplication.view_place.FullListViewFragment;
import com.example.myapplication.view_place.MapViewFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        setTitle("GroupGo");

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);

        Intent intent = getIntent();
        int fragmentId = intent.getIntExtra("fragment_id", 0);

        if (fragmentId == 1) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, firstFragment).commit();
        } else if (fragmentId == 2) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, secondFragment).commit();
        } else if (fragmentId == 3) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, thirdFragment).commit();
        }



    }
    MapViewFragment firstFragment = new MapViewFragment();
    HomePageFragment secondFragment = new HomePageFragment();
    FullListViewFragment thirdFragment = new FullListViewFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.person:

                if (HomePageFragment.curGroupNumber == 0) {
                    Toast.makeText(MainActivity.this, "You should choose a group first", Toast.LENGTH_SHORT).show();
                    bottomNavigationView.setSelectedItemId(R.id.home);
                    getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, secondFragment).commit();
                } else if (HomePageFragment.curLocationList == null || HomePageFragment.curLocationList.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Be the first one to add location in your group", Toast.LENGTH_SHORT).show();
                    bottomNavigationView.setSelectedItemId(R.id.home);
                    getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, secondFragment).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, firstFragment).commit();
                }

                return true;

            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, secondFragment).commit();
                return true;

            case R.id.settings:

                if (HomePageFragment.curGroupNumber == 0) {
                    Toast.makeText(MainActivity.this, "You should choose a group first", Toast.LENGTH_SHORT).show();
                    bottomNavigationView.setSelectedItemId(R.id.home);
                    getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, secondFragment).commit();
                } else if (HomePageFragment.curLocationList == null || HomePageFragment.curLocationList.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Be the first one to add location in your group", Toast.LENGTH_SHORT).show();
                    bottomNavigationView.setSelectedItemId(R.id.home);
                    getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, secondFragment).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, thirdFragment).commit();
                }

                return true;

        }
        return false;
    }

    public void onBackPressed() {
        Toast.makeText(MainActivity.this, "You can log out via Home -> Account Management", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK && bottomNavigationView.getSelectedItemId() == R.id.person) {
                    firstFragment.onActivityResult(requestCode, resultCode, data);
                }
                break;
            default:
        }

    }


    public void reloadMap(){
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.remove(firstFragment).commitNow();

        final FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();

        ft1.replace(R.id.flFragment, firstFragment).commitNow();
    }
}
