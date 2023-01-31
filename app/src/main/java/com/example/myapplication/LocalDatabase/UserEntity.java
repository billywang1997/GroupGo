package com.example.myapplication.LocalDatabase;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "user")
public class UserEntity implements Serializable {

    // (autoGenerate = true)

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "username", typeAffinity = ColumnInfo.TEXT)
    public String username;

    @ColumnInfo(name = "nickname", typeAffinity = ColumnInfo.TEXT)
    public String nickname;

    @NonNull
    @ColumnInfo(name = "password", typeAffinity = ColumnInfo.TEXT)
    public String password;


    public UserEntity(@NonNull String username, String nickname, @NonNull String password)
    {
        this.username = username;
        this.nickname = nickname;
        this.password = password;
    }

    @Ignore
    public UserEntity(@NonNull String username, @NonNull String password)
    {
        this.username = username;
        this.nickname = null;
        this.password = password;
    }
}
