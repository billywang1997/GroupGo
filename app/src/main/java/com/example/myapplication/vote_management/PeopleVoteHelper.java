package com.example.myapplication.vote_management;

import com.example.myapplication.LocalDatabase.PeopleVote;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface PeopleVoteHelper {
    Single<PeopleVote> getVotePeople(int groupNumber, String locationName, String locationAddress, String voteStartTime, String username);

    Completable insertOnePeopleVote(int groupNumber, String username, String locationName, String locationAddress, String ifVote, String voteStartTime);
}
