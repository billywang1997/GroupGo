package com.example.myapplication.my_groups_adapter;

public class MyGroupsItem {
    private String groupName;

    public String getGroupName() {
        return groupName;
    }

    public int getGroupId() {
        return groupId;
    }

    private int groupId;

    public MyGroupsItem(String groupName, int groupId) {
        this.groupName = groupName;
        this.groupId = groupId;
    }
}
