package com.example.myapplication.vote_management;

import com.example.myapplication.LocalDatabase.VoteEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface VoteDataHelper {
    Single<VoteEntity> getPreviousVote(String currentTime, int groupNumber);

    Single<VoteEntity> getHasCurrentVote(String currentTime,int groupNumber);

    Single<VoteEntity> getLastWinningLocation(int groupNumber,String voteStartTime);

    Single<List<VoteEntity>> getAllVotes(int groupNumber, String voteStartTime);

    Single<List<VoteEntity>> useTypeFilter(int groupNumber, String voteStartTime, String locationTag);

    Single<List<VoteEntity>> useVoteFilter(int groupNumber, String voteStartTime,int voteLimit);

    Single<VoteEntity> checkIfCanInsertIn(int groupNumber, String locationName, String locationAddress, String voteStartTime);

    Completable insertOneVote(int groupNumber, String activityName, String locationName, String locationAddress,String locationTag,String comments,String photos,String longitude,String latitude, String voteStartTime, String voteOverTime, int numberOfVotes);

    Completable updateTheVotes(int groupNumber, String locationName, String locationAddress, String voteStartTime);

    Single<Integer> getNumberOfNotesOneGroupOneTime(int groupNumber,String voteStartTime,String locationName, String locationAddress);

    Single<VoteEntity> findActivityNameAndTime(int groupNumber,String currentTime);
}
