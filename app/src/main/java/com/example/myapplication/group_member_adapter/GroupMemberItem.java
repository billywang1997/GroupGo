package com.example.myapplication.group_member_adapter;

public class GroupMemberItem {

    private String nickname;
    private String username;

    public GroupMemberItem(String nickname, String username) {
        this.nickname = nickname;
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUsername() {
        return username;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
