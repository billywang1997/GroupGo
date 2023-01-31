package com.example.myapplication.group_management;

import com.example.myapplication.GsonUtils;
import com.example.myapplication.LocalDatabase.GroupEntity;
import com.example.myapplication.account_management.HomePageFragment;
import com.example.myapplication.network.GroupGoApi;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;

public class RemoteGroupHelper implements GroupDataHelper {

    @Override
    public Completable createGroup(String groupName, int groupPin) {
        Map<String, String> map = new HashMap<>();
        map.put("groupName", groupName);
        map.put("groupPin", groupPin + "");
        map.put("groupManager", HomePageFragment.curUser);
        return GroupGoApi.getInstance().retrofitService.createGroup(map);
    }

    @Override
    public Completable modifyGroupPin(int groupNumber, int groupPin) {
        Map<String, String> map = new HashMap<>();
        map.put("groupNumber", groupNumber + "");
        map.put("newGroupPin", groupPin + "");
        return GroupGoApi.getInstance().retrofitService.modifyGroupPin(map);
    }

    @Override
    public Completable modifyGroupName(int groupNumber, String groupName) {
        Map<String, String> map = new HashMap<>();
        map.put("groupName", groupName);
        map.put("groupNumber", groupNumber + "");
        return GroupGoApi.getInstance().retrofitService.changeGroupName(map);
    }

    @Override
    public Single<String> fetchManager(int groupNumber) {
        return GroupGoApi.getInstance().retrofitService.getGroupManager(groupNumber).map(responseResult -> responseResult.getData().get("groupManager").toString());
    }

    @Override
    public Single<GroupEntity> checkOneGroup(int groupNumber, int groupPin) {
        return GroupGoApi.getInstance().retrofitService.findOneGroup(groupNumber, groupPin).map(responseResult -> GsonUtils.getInstance().gson.fromJson(
                responseResult.getData(),
                GroupEntity.class
        ));
    }

    @Override
    public Single<String> getGroupName(int groupNumber) {
        return GroupGoApi.getInstance().retrofitService.findGroupName(groupNumber).map(responseResult -> responseResult.getData().get("groupName").toString().replace("\"", ""));
    }

    @Override
    public Single<List<GroupEntity>> findLatestGroupsByManager(String groupManager) {
        return GroupGoApi.getInstance().retrofitService.findLatestGroupsByManager(groupManager).map(responseResult -> {
            GroupEntity groupEntity = GsonUtils.getInstance().gson.fromJson(responseResult.getData(), GroupEntity.class);
            List<GroupEntity> list = new ArrayList<>();
            list.add(groupEntity);
            return list;
        });
    }

    @Override
    public Single<List<Integer>> getLatestAddedGroupNumber() {
        return GroupGoApi.getInstance().retrofitService.findLatestAddedGroupNumber().map(responseResult -> {
            TypeToken<ArrayList<Integer>> type = new TypeToken<ArrayList<Integer>>() {};
            return GsonUtils.getInstance().gson.fromJson(responseResult.getData(), type.getType());
        });
    }

}
