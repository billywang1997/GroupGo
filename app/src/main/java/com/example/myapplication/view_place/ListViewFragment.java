package com.example.myapplication.view_place;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.listViewAdapter.ListViewAdapter;
import com.example.myapplication.listViewAdapter.ListViewItem;
import com.example.myapplication.PlaceType;

import java.util.ArrayList;
import java.util.List;

public class ListViewFragment extends Fragment {

    private List<ListViewItem> itemList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.location_list_page, container, false);
        initItems();
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.location_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        ListViewAdapter adapter = new ListViewAdapter(itemList);
        recyclerView.setAdapter(adapter);
        return view;
    }

    private void initItems() {
        /*
        for (int i = 0; i < 10; i++) {
            ListViewItem item = new ListViewItem("Melbourne Zoo", "Elliott Ave, Parkville VIC 3052",
                    "Love the animals.", PlaceType.OUTDOOR_ACTIVITY, R.drawable.blank_img
            , R.drawable.blank_img, R.drawable.blank_img);
        }
         */


    }


}
