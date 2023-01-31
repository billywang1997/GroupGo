package com.example.myapplication.add_location_management;

import com.example.myapplication.GsonUtils;
import com.example.myapplication.LocalDatabase.LocationEntity;
import com.example.myapplication.account_management.HomePageFragment;
import com.example.myapplication.network.GroupGoApi;
import com.example.myapplication.network.ResponseResult;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Function;

public class RemoteLocationHelper implements LocationDataHelper {

    @Override
    public Single<LocationEntity> getLocation(int groupNumber, String locationName, String locationAddress) {
        return GroupGoApi.getInstance().retrofitService
                .getLocation(groupNumber, locationName, locationAddress)
                .map(responseResult ->
                        GsonUtils.getInstance().gson.fromJson(responseResult.getData(), LocationEntity.class)
                );
    }

    @Override
    public Completable addLocation(int groupNumber, String locationName, String locationAddress, String username, String locationTag, String comments, String photos, String longitude, String latitude) {
        LocationEntity entity = new LocationEntity();
        entity.groupNumber = groupNumber;
        entity.locationName = locationName;
        entity.locationAddress = locationAddress;
        entity.username = username;
        entity.locationTag = locationTag;
        entity.comments = comments;
        entity.photos = photos;
        entity.longitude = longitude;
        entity.latitude = latitude;
        return GroupGoApi.getInstance().retrofitService.addNewLocation(entity);
    }

    @Override
    public Completable addNewComments(int groupNumber, String locationName, String locationAddress, String comments) {
        return GroupGoApi.getInstance().retrofitService.addNewComments(
                new Comments(groupNumber, locationName, locationAddress, comments)
        );
    }

    @Override
    public Single<String> getComments(int groupNumber, String locationName, String locationAddress) {
        return GroupGoApi.getInstance().retrofitService
                .getComments(groupNumber, locationName, locationAddress)
                .map(responseResult ->
                        responseResult.getData().get("comments").toString()
                );
    }

    @Override
    public Completable addNewPhotos(int groupNumber, String locationName, String locationAddress, String photos) {
        return GroupGoApi.getInstance().retrofitService.addNewPhotos(new Photos(
                groupNumber, locationName, locationAddress, photos)
        );
    }

    @Override
    public Single<String> getPhotos(int groupNumber, String locationName, String locationAddress) {
        return GroupGoApi.getInstance().retrofitService
                .getPhotos(groupNumber, locationName, locationAddress)
                .map(responseResult ->
                        responseResult.getData().get("photos").toString().replace("\"", "").replace("\\n", "\n")
                );
    }

    @Override
    public Single<Integer> getLocationNumbers(int groupNumber) {
        return GroupGoApi.getInstance().retrofitService.getLocationNumber(groupNumber)
                .map(responseResult ->
                        responseResult.getData().get("locationNumber").getAsInt()
                );
    }

    @Override
    public Single<LocationEntity> getOneLocation(int groupNumber, int x, int y) {
        return GroupGoApi.getInstance().retrofitService.getOneLocation(groupNumber, x, y).map(new Function<ResponseResult, LocationEntity>() {
            @Override
            public LocationEntity apply(ResponseResult responseResult) throws Exception {
                return GsonUtils.getInstance().gson.fromJson(
                        responseResult.getData(),
                        LocationEntity.class
                );
            }
        });
    }

    @Override
    public Single<List<LocationEntity>> getAllLocation(int groupNumber) {
        Map<String, Integer> map = new HashMap<>();
        map.put("groupNumber", groupNumber);
        return GroupGoApi.getInstance().retrofitService.getAllLocation(map).map(responseResult -> {
            LocationEntity[] locationList = GsonUtils.getInstance().gson.fromJson(responseResult.getData().get("locationList"), LocationEntity[].class);
            if (groupNumber != HomePageFragment.curGroupNumber) {
                return new ArrayList<>();
            }
            return new ArrayList<>(Arrays.asList(locationList));
        });
    }

    @Override
    public Single<LocationEntity> getUserLocation(int groupNumber, String locationName, String locationAddress, String username) {
        return GroupGoApi.getInstance().retrofitService
                .getUserLocation(groupNumber, locationName, locationAddress, username).map(responseResult ->
                        GsonUtils.getInstance().gson
                                .fromJson(responseResult.getData(), LocationEntity.class));
    }

    @Override
    public Completable deleteAUserLocation(int groupNumber, String locationName, String locationAddress, String username) {
        Map<String, String> map = new HashMap<>();
        map.put("groupNumber", groupNumber + "");
        map.put("locationName", locationName);
        map.put("locationAddress", locationAddress);
        map.put("username", username);
        return GroupGoApi.getInstance().retrofitService.deleteLocation(map);
    }

}
