package com.example.myapplication.LocalDatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface AllGroupDao {


    @Query("SELECT groupNumber from allGroup order by createTime desc;")
    Single<List<Integer>> findLatestAddedGroupNumber();

    @Query("SELECT * FROM allGroup where groupManager= :groupManager order by createTime desc;")
    Single<List<GroupEntity>> findLatestCreatedGroup(String groupManager);

    @Query("SELECT groupName from allGroup where groupNumber= :groupNumber")
    Single<String> findGroupName(int groupNumber);

    @Query("select * from allGroup where groupNumber= :groupNumber and groupPin= :groupPin")
    Single<GroupEntity> findOneGroup(int groupNumber,int groupPin);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOne(GroupEntity groupEntity);

    @Update
    Completable update(GroupEntity groupEntity);

    @Query("update allGroup set groupName= :groupName where groupNumber= :groupNumber")
    Completable updateGroupName(int groupNumber,String groupName);

    @Query("update allGroup set groupPin= :groupPin where groupNumber= :groupNumber")
    Completable updateGroupPin(int groupNumber,int groupPin);

    @Query("SELECT groupManager from allGroup where groupNumber= :groupNumber")
    Single<String> getGroupManager(int groupNumber);

}
