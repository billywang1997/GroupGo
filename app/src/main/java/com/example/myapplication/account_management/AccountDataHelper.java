package com.example.myapplication.account_management;

import com.example.myapplication.LocalDatabase.UserEntity;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface AccountDataHelper {
    public abstract Single<UserEntity> checkLogIn(String username, String password);

    public abstract Completable createAccount(String username, String nickname, String password);

    public abstract Completable updateNickname(String username, String nickname, String password);

    public abstract Single<String> getNickname(String username);

    public abstract Completable createAccount(String username, String password);

    public abstract Completable resetPassword(String username, String newPassword);

    public abstract Completable resetPassword(String username, String nickname, String newPassword);

    public abstract Single<UserEntity> getOneByName(String username);
}
