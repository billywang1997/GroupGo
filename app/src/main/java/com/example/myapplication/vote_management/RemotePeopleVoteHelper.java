package com.example.myapplication.vote_management;

import com.example.myapplication.GsonUtils;
import com.example.myapplication.LocalDatabase.PeopleVote;
import com.example.myapplication.network.GroupGoApi;
import com.example.myapplication.network.ResponseResult;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Function;

public class RemotePeopleVoteHelper implements PeopleVoteHelper {

    @Override
    public Single<PeopleVote> getVotePeople(int groupNumber, String locationName, String locationAddress, String voteStartTime, String username) {
        return GroupGoApi.getInstance().retrofitService.findPeopleVote(groupNumber, locationName, voteStartTime, username).map(new Function<ResponseResult, PeopleVote>() {
            @Override
            public PeopleVote apply(ResponseResult responseResult) throws Exception {
                return GsonUtils.getInstance().gson.fromJson(responseResult.getData(), PeopleVote.class);
            }
        });
    }

    @Override
    public Completable insertOnePeopleVote(int groupNumber, String username, String locationName, String locationAddress, String ifVote, String voteStartTime) {
        Map<String, String> map = new HashMap<>();
        map.put("groupNumber", groupNumber + "");
        map.put("username", username);
        map.put("locationName", locationName);
        map.put("locationAddress", locationAddress);
        map.put("ifVote", ifVote);
        map.put("voteStartTime", voteStartTime);
        return GroupGoApi.getInstance().retrofitService.insertOnePeopleVote(map);
    }

}
