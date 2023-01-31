package com.example.myapplication.LocalDatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface LocationDao {
    @Query("select * from locationList where groupNumber= :groupNumber and locationName= :locationName and locationAddress= :locationAddress")
    Single<LocationEntity> findLocation(int groupNumber, String locationName, String locationAddress);

    @Query("insert into locationList values( :groupNumber,:locationName, :locationAddress, :username, :locationTag, :comments, :photos, :longitude, :latitude)")
    Completable insertLocation(int groupNumber, String locationName, String locationAddress, String username, String locationTag, String comments, String photos, String longitude, String latitude);

    @Query("update locationList set comments= :comments where groupNumber= :groupNumber and locationName= :locationName and locationAddress= :locationAddress")
    Completable updateComments(int groupNumber, String locationName, String locationAddress, String comments);

    @Query("select comments from locationList where groupNumber= :groupNumber and locationName= :locationName and locationAddress= :locationAddress")
    Single<String> findComments(int groupNumber, String locationName, String locationAddress);

    @Query("update locationList set photos= :photos where groupNumber= :groupNumber and locationName= :locationName and locationAddress= :locationAddress")
    Completable updatePhotos(int groupNumber, String locationName, String locationAddress, String photos);

    @Query("select photos from locationList where groupNumber= :groupNumber and locationName= :locationName and locationAddress= :locationAddress")
    Single<String> findPhotos(int groupNumber, String locationName, String locationAddress);

    @Query("select count(*) from locationList where groupNumber= :groupNumber")
    Single<Integer> findLocationNumber(int groupNumber);

    @Query("select * from locationList where groupNumber= :groupNumber limit :x, :y")
    Single<LocationEntity> findOneLocation(int groupNumber, int x, int y);

    @Query("select * from locationList where groupNumber= :groupNumber ")
    Single<List<LocationEntity>> findAllLocation(int groupNumber);

    @Query("select * from locationList where groupNumber= :groupNumber and locationName= :locationName and locationAddress= :locationAddress and username= :username")
    Single<LocationEntity> findUserLocation(int groupNumber, String locationName, String locationAddress, String username);

    @Query("delete from locationList where groupNumber= :groupNumber and locationName= :locationName and locationAddress= :locationAddress and username= :username")
    Completable deleteUserLocation(int groupNumber, String locationName, String locationAddress, String username);

}
