package com.example.myapplication.LocalDatabase;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Relation;

import java.util.List;

@Entity(tableName = "peopleVote", primaryKeys = {"groupNumber", "locationName", "locationAddress", "voteStartTime", "username"})
public class PeopleVote {

    @NonNull
    public int groupNumber;

    @NonNull
    public String username;

    @NonNull
    public String locationAddress;

    @NonNull
    public String locationName;

    public String ifVote;

    @NonNull
    public String voteStartTime;




}
