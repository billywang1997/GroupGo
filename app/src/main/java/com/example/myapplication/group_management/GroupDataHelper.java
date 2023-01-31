package com.example.myapplication.group_management;

import com.example.myapplication.LocalDatabase.GroupEntity;
import com.example.myapplication.LocalDatabase.UserEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface GroupDataHelper {
    public abstract Completable createGroup(String groupName, int groupPin);

    public abstract Completable modifyGroupPin(int groupNumber, int groupPin);

    public abstract Completable modifyGroupName(int groupNumber,String groupName);

    public abstract Single<String> fetchManager(int groupNumber);

    public abstract Single<GroupEntity> checkOneGroup(int groupNumber,int groupPin);

    public abstract  Single<String> getGroupName(int groupNumber);

    public abstract Single<List<GroupEntity>>  findLatestGroupsByManager(String groupManager);

    public abstract Single<List<Integer>> getLatestAddedGroupNumber();


}
