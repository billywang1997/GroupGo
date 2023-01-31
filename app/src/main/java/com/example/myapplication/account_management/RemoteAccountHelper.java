package com.example.myapplication.account_management;

import com.example.myapplication.GsonUtils;
import com.example.myapplication.LocalDatabase.UserEntity;
import com.example.myapplication.network.GroupGoApi;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;

public class RemoteAccountHelper implements AccountDataHelper {

    @Override
    public Single<UserEntity> checkLogIn(String username, String password) {
        return GroupGoApi.getInstance().retrofitService
                .login(new LoginData(username, password))
                .map(
                        responseResult -> GsonUtils.getInstance().gson
                                .fromJson(responseResult.getData(), UserEntity.class)

                );
    }

    @Override
    public Completable createAccount(String username, String nickname, String password) {
        return GroupGoApi.getInstance().retrofitService
                .register(new RegisterData(username, password, nickname));
    }

    @Override
    public Completable updateNickname(String username, String nickname, String password) {
        return GroupGoApi.getInstance().retrofitService
                .changeNickname(new ChangeNicknameData(username, nickname));
    }

    @Override
    public Single<String> getNickname(String username) {
        return GroupGoApi.getInstance().retrofitService.getNickname(username)
                .map(responseResult -> responseResult.getData().get("nickname").toString().replace("\"", ""));
    }

    @Override
    public Completable createAccount(String username, String password) {
        return GroupGoApi.getInstance().retrofitService
                .register(new RegisterData(username, password, ""));
    }

    @Override
    public Completable resetPassword(String username, String newPassword) {
        return null;
    }

    @Override
    public Completable resetPassword(String username, String currentPassword, String newPassword) {
        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("currentPassword", currentPassword);
        map.put("newPassword", newPassword);
        return GroupGoApi.getInstance().retrofitService.changePassword(map);
    }

    @Override
    public Single<UserEntity> getOneByName(String username) {
        return GroupGoApi.getInstance().retrofitService.getOneByName(username)
                .map(responseResult ->
                        GsonUtils.getInstance().gson.fromJson(
                                responseResult.getData(),
                                UserEntity.class
                        )
                );
    }

}
