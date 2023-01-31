package com.example.myapplication.LocalDatabase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface VoteDao {
    @Query("select * from vote where groupNumber = :groupNumber and :currentTime > voteOverTime order by voteStartTime desc,numberOfVotes desc limit 0,1; ")
    public Single<VoteEntity> findPreviousVote(String currentTime, int groupNumber);

    @Query("select * from vote  where :currentTime >= voteStartTime and :currentTime <= voteOverTime and groupNumber = :groupNumber limit 0,1")
    public Single<VoteEntity> findHasCurrentVote(String currentTime,int groupNumber);

    @Query("select * from vote where groupNumber=:groupNumber and voteStartTime = :voteStartTime order by numberOfVotes desc,locationName asc limit 1;")
    public Single<VoteEntity> lastWinningLocation(int groupNumber,String voteStartTime);

    @Query("SELECT * from vote where groupNumber= :groupNumber and " +
            "voteStartTime= :voteStartTime")
    public Single<List<VoteEntity>> findAllVotes(int groupNumber, String voteStartTime);

    @Query("SELECT * from vote where groupNumber= :groupNumber and " +
            "voteStartTime= :voteStartTime and locationTag = :locationTag")
    public Single<List<VoteEntity>> typeFilter(int groupNumber, String voteStartTime, String locationTag);

    @Query("SELECT * from vote where groupNumber= :groupNumber and " +
            "voteStartTime= :voteStartTime and numberOfVotes >= :voteLimit")
    public Single<List<VoteEntity>> filter(int groupNumber, String voteStartTime, int voteLimit);

    @Query("SELECT * from vote where groupNumber= :groupNumber and " +
            "locationName= :locationName and " +
            "locationAddress= :locationAddress and " +
            "voteStartTime= :voteStartTime")
    public Single<VoteEntity> checkIfCanInsert(int groupNumber, String locationName, String locationAddress, String voteStartTime);

    @Query("INSERT INTO vote (groupNumber, activityName, locationName, locationAddress, locationTag, comments, photos, longitude, latitude, voteStartTime, voteOverTime, numberOfVotes) values( :groupNumber," +
            ":activityName," +
            ":locationName," +
            ":locationAddress," +
            ":locationTag," +
            ":comments," +
            ":photos," +
            ":longitude," +
            ":latitude," +
            ":voteStartTime," +
            ":voteOverTime," +
            ":numberOfVotes)")
    public Completable insertOne(int groupNumber, String activityName, String locationName, String locationAddress, String locationTag, String comments, String photos, String longitude, String latitude, String voteStartTime, String voteOverTime, int numberOfVotes);

    @Query("UPDATE vote set numberOfVotes = numberOfVotes + 1 where groupNumber = :groupNumber and " +
            "locationName = :locationName and " +
            "locationAddress = :locationAddress and " +
            "voteStartTime = :voteStartTime")
    public Completable updateVotes(int groupNumber, String locationName, String locationAddress, String voteStartTime);

    @Query("SELECT numberOfVotes from vote where groupNumber = :groupNumber and voteStartTime = :voteStartTime and locationName = :locationName and locationAddress = :locationAddress")
    public Single<Integer> findNumberOfNotesOneGroupOneTime(int groupNumber,String voteStartTime,String locationName, String locationAddress);

    @Query("SELECT * from vote where groupNumber = :groupNumber and voteStartTime <= :currentTime and :currentTime <= voteOverTime limit 0,1 ")
    public Single<VoteEntity> getActivityNameAndTime(int groupNumber,String currentTime);
}
