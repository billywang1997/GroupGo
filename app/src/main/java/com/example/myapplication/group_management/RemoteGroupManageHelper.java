package com.example.myapplication.group_management;

import com.example.myapplication.GsonUtils;
import com.example.myapplication.group_member_adapter.GroupMemberItem;
import com.example.myapplication.network.GroupGoApi;
import com.example.myapplication.network.ResponseResult;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Function;

public class RemoteGroupManageHelper implements GroupManageHelper {

    @Override
    public Completable insertMemberToGroup(int groupNumber, String groupMember) {
        Map<String, String> map = new HashMap<>();
        map.put("groupNumber", groupNumber + "");
        map.put("groupMember", groupMember);
        return GroupGoApi.getInstance().retrofitService.insertMemberToGroup(map);
    }

    @Override
    public Completable deleteMemberFromGroup(int groupNumber, String groupMember) {
        Map<String, String> map = new HashMap<>();
        map.put("groupNumber", groupNumber + "");
        map.put("groupMember", groupMember);
        return GroupGoApi.getInstance().retrofitService.kickOneMemberOut(map);
    }

    @Override
    public Single<List<String>> findGroupMembers(int groupNumber) {
        Map<String, String> map = new HashMap<>();
        map.put("groupNumber", groupNumber + "");
        return GroupGoApi.getInstance().retrofitService.getAllMembersName(map).map(responseResult -> {
            GroupMemberItem[] items =
                    GsonUtils.getInstance().gson
                            .fromJson(
                                    responseResult.getData().get("finalList"),
                                    GroupMemberItem[].class
                            );
            List<String> names = new ArrayList<>();
            for (GroupMemberItem item : items) {
                names.add(item.getUsername().replace("\"", ""));
            }
            return names;
        });
    }

    @Override
    public Single<List<Integer>> findMemberGroups(String groupMember) {
        return GroupGoApi.getInstance().retrofitService.findAllGroupNumber(groupMember).map(new Function<ResponseResult, List<Integer>>() {
            @Override
            public List<Integer> apply(ResponseResult responseResult) throws Exception {
                int[] groups = GsonUtils.getInstance().gson.fromJson(responseResult.getData().get("groupNumbers"), int[].class);
                List<Integer> groupList = new ArrayList<>();
                for (int groupNumber: groups) {
                    groupList.add(groupNumber);
                }
                return groupList;
            }
        });
    }

}
