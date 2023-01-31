package com.example.myapplication.LocalDatabase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface PeopleVoteDao {
    @Query("SELECT * FROM peopleVote where groupNumber= :groupNumber and locationName = :locationName and locationAddress = :locationAddress and voteStartTime = :voteStartTime and username= :username limit 0,1")
    public Single<PeopleVote> findVotePeople(int groupNumber, String locationName, String locationAddress, String voteStartTime, String username);

    @Query("INSERT INTO peopleVote (groupNumber, username, locationName, locationAddress, ifVote, voteStartTime) values( :groupNumber, :username, :locationName, :locationAddress, :ifVote, :voteStartTime)")
    public Completable insertOne(int groupNumber, String username, String locationName, String locationAddress, String ifVote, String voteStartTime);
}
