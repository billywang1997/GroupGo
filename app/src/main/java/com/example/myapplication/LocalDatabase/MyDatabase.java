package com.example.myapplication.LocalDatabase;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

@Database(entities = {UserEntity.class, GroupEntity.class, GroupMemberCrossRef.class, LocationEntity.class, VoteEntity.class, PeopleVote.class}, version = 5)
public abstract class MyDatabase extends RoomDatabase {
    abstract public  UserDao userDao();
    abstract public AllGroupDao allGroupDao();
    abstract public ManageGroupDao manageGroupDao();
    abstract public LocationDao locationDao();
    abstract public VoteDao voteDao();
    abstract public PeopleVoteDao peopleVoteDao();

    private static final String DATABASE_NAME = "my_db";

    private static MyDatabase databaseInstance;

    public static synchronized MyDatabase getInstance(Context context)
    {
        if(databaseInstance == null)
        {
            databaseInstance = Room
                    .databaseBuilder(context, MyDatabase.class, DATABASE_NAME)
                    .build();
        }
        return databaseInstance;
    }

}
