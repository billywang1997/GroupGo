package com.example.myapplication;

import com.google.gson.Gson;

public class GsonUtils {

    private GsonUtils() {}

    private static class GsonUtilsHolder {
        private static GsonUtils INSTANCE = new GsonUtils();
    }

    public static GsonUtils getInstance() {
        return GsonUtilsHolder.INSTANCE;
    }

    public Gson gson = new Gson();

}
