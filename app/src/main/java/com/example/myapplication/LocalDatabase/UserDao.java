package com.example.myapplication.LocalDatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertUser(UserEntity user);

    @Delete
    Completable deleteUser(UserEntity user);

    @Update
    Completable updateUser(UserEntity user);

    @Query("SELECT * FROM user")
    Single<List<UserEntity>> getUserList();

    @Query("select * from user where username= :username and password= :password")
    Single<UserEntity> findOne(String username,String password);

    @Query("select * from user where username= :username")
    Single<UserEntity> findOneByName(String username);

    @Query("SELECT nickname from user where username= :username")
    Single<String> findNickName(String username);

}
