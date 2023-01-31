package com.example.myapplication.LocalDatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface ManageGroupDao {


    @Query("insert into manageGroup values( :groupNumber, :groupMember)")
    Completable insertOne(int groupNumber, String groupMember);

    @Query("DELETE FROM manageGroup where groupNumber= :groupNumber and username = :groupMember")
    Completable deleteOneMember(int groupNumber,String groupMember);

    @Query("SELECT username FROM manageGroup where groupNumber= :groupNumber")
    Single<List<String>> findAllMembersName(int groupNumber);

    @Query("SELECT groupNumber FROM manageGroup where username= :groupMember")
    Single<List<Integer>> findAllGroupNumber(String groupMember);


}
