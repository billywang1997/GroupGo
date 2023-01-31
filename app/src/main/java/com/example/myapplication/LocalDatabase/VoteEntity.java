package com.example.myapplication.LocalDatabase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "vote", primaryKeys = {"groupNumber", "locationName", "locationAddress", "voteStartTime"})
public class VoteEntity {

    @NonNull
    @ColumnInfo(name = "groupNumber", typeAffinity = ColumnInfo.INTEGER)
    public int groupNumber;

    @NonNull
    @ColumnInfo(name = "activityName", typeAffinity = ColumnInfo.TEXT)
    public String activityName;

    @NonNull
    @ColumnInfo(name = "locationName", typeAffinity = ColumnInfo.TEXT)
    public String locationName;

    @NonNull
    @ColumnInfo(name = "locationAddress", typeAffinity = ColumnInfo.TEXT)
    public String locationAddress;

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

    @NonNull
    @ColumnInfo(name = "voteStartTime", typeAffinity = ColumnInfo.TEXT)
    public String voteStartTime;

    @ColumnInfo(name = "voteOverTime", typeAffinity = ColumnInfo.TEXT)
    public String voteOverTime;

    @ColumnInfo(name = "numberOfVotes", typeAffinity = ColumnInfo.INTEGER)
    public int numberOfVotes;

}
