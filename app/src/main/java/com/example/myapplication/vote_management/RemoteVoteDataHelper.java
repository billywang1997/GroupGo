package com.example.myapplication.vote_management;

import com.example.myapplication.GsonUtils;
import com.example.myapplication.LocalDatabase.VoteEntity;
import com.example.myapplication.network.GroupGoApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;

public class RemoteVoteDataHelper implements VoteDataHelper {

    @Override
    public Single<VoteEntity> getPreviousVote(String currentTime, int groupNumber) {
        return GroupGoApi.getInstance().retrofitService.findPreviousVote(currentTime, groupNumber).map(responseResult -> GsonUtils.getInstance().gson.fromJson(responseResult.getData(), VoteEntity.class));
    }

    @Override
    public Single<VoteEntity> getHasCurrentVote(String currentTime, int groupNumber) {
        return GroupGoApi.getInstance().retrofitService.findHasCurrentVote(currentTime, groupNumber).map(responseResult -> GsonUtils.getInstance().gson.fromJson(responseResult.getData(), VoteEntity.class));
    }

    @Override
    public Single<VoteEntity> getLastWinningLocation(int groupNumber, String voteStartTime) {
        return null;
    }

    @Override
    public Single<List<VoteEntity>> getAllVotes(int groupNumber, String voteStartTime) {
        return GroupGoApi.getInstance().retrofitService.findAllVote(groupNumber, voteStartTime).map(responseResult -> {
            VoteEntity[] votes = GsonUtils.getInstance().gson.fromJson(responseResult.getData().get("votes"), VoteEntity[].class);
            return new ArrayList<>(Arrays.asList(votes));
        });
    }

    @Override
    public Single<List<VoteEntity>> useTypeFilter(int groupNumber, String voteStartTime, String locationTag) {
        return null;
    }

    @Override
    public Single<List<VoteEntity>> useVoteFilter(int groupNumber, String voteStartTime, int voteLimit) {
        return null;
    }

    @Override
    public Single<VoteEntity> checkIfCanInsertIn(int groupNumber, String locationName, String locationAddress, String voteStartTime) {
        return null;
    }

    @Override
    public Completable insertOneVote(
            int groupNumber,
            String activityName,
            String locationName,
            String locationAddress,
            String locationTag,
            String comments,
            String photos,
            String longitude,
            String latitude,
            String voteStartTime,
            String voteOverTime,
            int numberOfVotes
    ) {
        Map<String, String> map = new HashMap<>();
        map.put("groupNumber", groupNumber + "");
        map.put("activityName", activityName);
        map.put("locationName", locationName);
        map.put("locationAddress", locationAddress);
        map.put("locationTag", locationTag);
        map.put("comments", comments);
        map.put("photos", photos);
        map.put("longitude", longitude);
        map.put("latitude", latitude);
        map.put("voteStartTime", voteStartTime);
        map.put("voteOverTime", voteOverTime);
        map.put("numberOfVotes", numberOfVotes + "");
        return GroupGoApi.getInstance().retrofitService.insertOneVote(map);
    }

    @Override
    public Completable updateTheVotes(int groupNumber, String locationName, String locationAddress, String voteStartTime) {
        Map<String, String> map = new HashMap<>();
        map.put("groupNumber", groupNumber + "");
        map.put("locationName", locationName);
        map.put("locationAddress", locationAddress);
        map.put("voteStartTime", voteStartTime);
        return GroupGoApi.getInstance().retrofitService.updateVotes(map);
    }

    @Override
    public Single<Integer> getNumberOfNotesOneGroupOneTime(int groupNumber, String voteStartTime, String locationName, String locationAddress) {
        Map<String, String> map = new HashMap<>();
        map.put("groupNumber", groupNumber + "");
        map.put("voteStartTime", voteStartTime);
        map.put("locationName", locationName);
        map.put("locationAddress", locationAddress);
        return GroupGoApi.getInstance().retrofitService.findNumberOfNotesOneGroupOneTime(map).map(responseResult -> Integer.parseInt(responseResult.getData().get("number").toString()));
    }

    @Override
    public Single<VoteEntity> findActivityNameAndTime(int groupNumber, String currentTime) {
        return null;
    }

}
