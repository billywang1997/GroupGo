package com.example.myapplication.LocalDatabase;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"groupNumber", "username"}, tableName = "manageGroup")
public class GroupMemberCrossRef {
    @NonNull
    public int groupNumber;

    @NonNull
    public String username;

    public GroupMemberCrossRef(@NonNull int groupNumber, @NonNull String username)
    {
        this.groupNumber = groupNumber;
        this.username = username;
    }
}
