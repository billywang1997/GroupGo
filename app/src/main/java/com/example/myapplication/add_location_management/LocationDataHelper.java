package com.example.myapplication.add_location_management;

import androidx.room.Query;

import com.example.myapplication.LocalDatabase.LocationEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface LocationDataHelper {

    Single<LocationEntity> getLocation(int groupNumber, String locationName, String locationAddress);

    Completable addLocation(int groupNumber, String locationName, String locationAddress, String username, String locationTag, String comments, String photos, String longitude, String latitude);

    Completable addNewComments(int groupNumber, String locationName, String locationAddress, String comments);

    Single<String> getComments(int groupNumber, String locationName, String locationAddress);

    Completable addNewPhotos(int groupNumber, String locationName, String locationAddress, String photos);

    Single<String> getPhotos(int groupNumber, String locationName, String locationAddress);

    Single<Integer> getLocationNumbers(int groupNumber);

    Single<LocationEntity> getOneLocation(int groupNumber, int x, int y);

    Single<List<LocationEntity>> getAllLocation(int groupNumber);

    Single<LocationEntity> getUserLocation(int groupNumber, String locationName, String locationAddress, String username);

    Completable deleteAUserLocation(int groupNumber, String locationName, String locationAddress, String username);
}
