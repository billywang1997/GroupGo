package com.example.myapplication.LocalDatabase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "allgroup")
public class GroupEntity implements Serializable {


    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "groupNumber", typeAffinity = ColumnInfo.INTEGER)
    public int groupNumber;

    @NonNull
    @ColumnInfo(name = "groupName", typeAffinity = ColumnInfo.TEXT)
    public String groupName;

    @NonNull
    @ColumnInfo(name = "groupPin", typeAffinity = ColumnInfo.INTEGER)
    public int groupPin;

    @NonNull
    @ColumnInfo(name = "groupManager", typeAffinity = ColumnInfo.TEXT)
    public String groupManager;

    @NonNull
    @ColumnInfo(name = "createTime", typeAffinity = ColumnInfo.TEXT)
    public String createTime;

    public GroupEntity(String groupName, int groupPin, String groupManager, String createTime) {
        this.groupName = groupName;
        this.groupPin = groupPin;
        this.groupManager = groupManager;
        this.createTime = createTime;
    }


}
