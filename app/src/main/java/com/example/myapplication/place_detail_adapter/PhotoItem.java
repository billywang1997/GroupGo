package com.example.myapplication.place_detail_adapter;

import android.graphics.Bitmap;

public class PhotoItem {

    public Bitmap getImg() {
        return img;
    }

    private Bitmap img;

    public PhotoItem (Bitmap img) {
        this.img = img;
    }
}


