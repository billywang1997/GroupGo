package com.example.myapplication.vote_place_adapter;

import android.graphics.Bitmap;

import com.example.myapplication.PlaceType;

public class VotePlaceItem {
    private String placeName;
    private String placeAddress;
    private String recentReason;
    private PlaceType placeType;
    private Bitmap imageId1;
    private Bitmap imageId2;
    private Bitmap imageId3;

    public VotePlaceItem(String placeName, String placeAddress, String recentReason, PlaceType placeType
            , Bitmap imageId1, Bitmap imageId2, Bitmap imageId3) {
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.recentReason = recentReason;
        this.placeType = placeType;
        this.imageId1 = imageId1;
        this.imageId2 = imageId2;
        this.imageId3 = imageId3;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public Bitmap getImageId1() {
        return imageId1;
    }

    public Bitmap getImageId2() {
        return imageId2;
    }

    public Bitmap getImageId3() {
        return imageId3;
    }

    public String getRecentReason() {
        return recentReason;
    }

    public PlaceType getPlaceType() {
        return placeType;
    }
}
