package com.example.myapplication.network;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class GroupGoApi {

    private GroupGoApi() {}

    private static class GroupGoApiHolder {
        private static GroupGoApi INSTANCE = new GroupGoApi();
    }

    public static GroupGoApi getInstance() {
        return GroupGoApiHolder.INSTANCE;
    }

    public String BASE_URL = "http://ec2-35-89-3-165.us-west-2.compute.amazonaws.com:8080/";

    public Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

    public GroupGoApiService retrofitService = retrofit.create(GroupGoApiService.class);

}
