package com.example.myapplication.group_management;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface GroupManageHelper {

    Completable insertMemberToGroup(int groupNumber, String groupMember);

    Completable deleteMemberFromGroup(int groupNumber, String groupMember);

    Single<List<String>> findGroupMembers(int groupNumber);

    Single<List<Integer>> findMemberGroups(String groupMember);
}
