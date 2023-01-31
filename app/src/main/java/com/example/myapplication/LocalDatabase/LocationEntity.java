package com.example.myapplication.LocalDatabase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "locationList", primaryKeys = {"groupNumber", "locationName", "locationAddress"})
public class LocationEntity {

    @NonNull
    @ColumnInfo(name = "groupNumber", typeAffinity = ColumnInfo.INTEGER)
    public int groupNumber;

    @NonNull
    @ColumnInfo(name = "locationName", typeAffinity = ColumnInfo.TEXT)
    public String locationName;

    @NonNull
    @ColumnInfo(name = "locationAddress", typeAffinity = ColumnInfo.TEXT)
    public String locationAddress;

    @NonNull
    @ColumnInfo(name = "username", typeAffinity = ColumnInfo.TEXT)
    public String username;

    @ColumnInfo(name = "locationTag", typeAffinity = ColumnInfo.TEXT)
    public String locationTag;

    @NonNull
    @ColumnInfo(name = "comments", typeAffinity = ColumnInfo.TEXT)
    public String comments;

    @ColumnInfo(name = "photos", typeAffinity = ColumnInfo.TEXT)
    public String photos;

    @NonNull
    @ColumnInfo(name = "longitude", typeAffinity = ColumnInfo.TEXT)
    public String longitude;

    @NonNull
    @ColumnInfo(name = "latitude", typeAffinity = ColumnInfo.TEXT)
    public String latitude;
}
