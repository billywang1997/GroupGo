package com.example.myapplication.add_location_management;

import static com.example.myapplication.BuildConfig.MAPS_API_KEY;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.helper.ApiInterface;
import com.example.myapplication.helper.Model.Listclass;
import com.example.myapplication.helper.Model.MainPojo;
import com.example.myapplication.helper.OneLineAdapter;
import com.example.myapplication.place_address_adapter.PlaceAddressAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchPlaceActivity extends AppCompatActivity {

    EditText searchBar;
    RecyclerView recyclerView;
    RelativeLayout relativeLayout;

    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_place_search);

        searchBar = findViewById(R.id.search_bar);
        recyclerView = findViewById(R.id.recycler_view);
        relativeLayout = findViewById(R.id.no_data_found);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                .baseUrl("https://maps.googleapis.com/maps/api/")
                                        .build();

        apiInterface = retrofit.create(ApiInterface.class);

        searchBar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                getData(editable.toString());
            }
        });
    }


    private void getData(String text) {
        apiInterface.getPlace(text, MAPS_API_KEY).enqueue(new Callback<MainPojo>() {
            @Override
            public void onResponse(Call<MainPojo> call, Response<MainPojo> response) {
                if (response.isSuccessful()) {
                    relativeLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    ArrayList<Listclass> listItems = response.body().getPredictions();

                    RecyclerView recyclerView = findViewById(R.id.recycler_view);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(layoutManager);


                    OneLineAdapter adapter = new OneLineAdapter(listItems);
                    recyclerView.setAdapter(adapter);


                } else {
                    relativeLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<MainPojo> call, Throwable t) {
                relativeLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);

                Toast.makeText(SearchPlaceActivity.this, "Error Occurred.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
