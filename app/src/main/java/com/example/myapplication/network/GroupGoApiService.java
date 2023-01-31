package com.example.myapplication.network;

import com.example.myapplication.LocalDatabase.LocationEntity;
import com.example.myapplication.account_management.ChangeNicknameData;
import com.example.myapplication.account_management.ChangePasswordData;
import com.example.myapplication.account_management.LoginData;
import com.example.myapplication.account_management.RegisterData;
import com.example.myapplication.add_location_management.Comments;
import com.example.myapplication.add_location_management.Photos;

import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GroupGoApiService {

    @POST("user/login")
    Single<ResponseResult> login(@Body LoginData loginData);

    @POST("user/register")
    Completable register(@Body RegisterData registerData);

    @POST("user/changeNickname")
    Completable changeNickname(@Body ChangeNicknameData changeNicknameData);

    @POST("user/changePassword")
    Completable changePassword(@Body Map<String, String> map);

    @GET("group/findNickName/{username}")
    Single<ResponseResult> getNickname(@Path("username") String username);

    @GET("user/findOneByName/{username}")
    Single<ResponseResult> getOneByName(@Path("username") String username);

    @GET("locationList/findLocation")
    Single<ResponseResult> getLocation(
            @Query("groupNumber") int groupNumber,
            @Query("locationName") String locationName,
            @Query("locationAddress") String locationAddress
    );

    @POST("locationList/addNewLocation")
    Completable addNewLocation(@Body LocationEntity locationEntity);

    @POST("locationList/addNewComments")
    Completable addNewComments(@Body Comments comments);

    @GET("locationList/findComments")
    Single<ResponseResult> getComments(
            @Query("groupNumber") int groupNumber,
            @Query("locationName") String locationName,
            @Query("locationAddress") String locationAddress
    );

    @POST("locationList/addNewPhotos")
    Completable addNewPhotos(@Body Photos photos);

    @GET("locationList/findPhotos")
    Single<ResponseResult> getPhotos(
            @Query("groupNumber") int groupNumber,
            @Query("locationName") String locationName,
            @Query("locationAddress") String locationAddress
    );

    @GET("locationList/findLocationNumber")
    Single<ResponseResult> getLocationNumber(@Query("groupNumber") int groupNumber);

    @GET("locationList/findOneLocation")
    Single<ResponseResult> getOneLocation(
            @Query("groupNumber") int groupNumber,
            @Query("x") int x,
            @Query("y") int y
    );

    @POST("locationList/showLocationList")
    Single<ResponseResult> getAllLocation(@Body Map<String, Integer> map);

    @GET("locationList/findUserLocation")
    Single<ResponseResult> getUserLocation(
            @Query("groupNumber") int groupNumber,
            @Query("locationName") String locationName,
            @Query("locationAddress") String locationAddress,
            @Query("username") String username
    );

    @POST("locationList/deleteLocation")
    Completable deleteLocation(@Body Map<String, String> map);

    @POST("group/createGroup")
    Completable createGroup(@Body Map<String, String> map);

    @POST("group/modifyGroupPin")
    Completable modifyGroupPin(@Body Map<String, String> map);

    @POST("group/changeGroupName")
    Completable changeGroupName(@Body Map<String, String> map);

    @GET("group/getGroupManager")
    Single<ResponseResult> getGroupManager(@Query("groupNumber") int groupNumber);

    @GET("group/findOneGroup")
    Single<ResponseResult> findOneGroup(
            @Query("groupNumber") int groupNumber,
            @Query("groupPin") int groupPin
    );

    @GET("group/findGroupName")
    Single<ResponseResult> findGroupName(@Query("groupNumber") int groupNumber);

    @GET("group/findLatestGroupsByManager")
    Single<ResponseResult> findLatestGroupsByManager(@Query("groupManager") String groupManager);

    @GET("group/findLatestAddedGroupNumber")
    Single<ResponseResult> findLatestAddedGroupNumber();

    @GET("people/findVotePeople")
    Single<ResponseResult> findPeopleVote(
            @Query("groupNumber") int groupNumber,
            @Query("locationName") String locationName,
            @Query("voteStartTime") String voteStartTime,
            @Query("username") String username
    );

    @POST("people/insertOnePeopleVote")
    Completable insertOnePeopleVote(@Body Map<String, String> map);

    @GET("vote/findPreviousVote")
    Single<ResponseResult> findPreviousVote(
            @Query("currentTime") String currentTime,
            @Query("groupNumber") int groupNumber
    );

    @GET("vote/findHasCurrentVote")
    Single<ResponseResult> findHasCurrentVote(
            @Query("currentTime") String currentTime,
            @Query("groupNumber") int groupNumber
    );

    @GET("vote/findAllVote")
    Single<ResponseResult> findAllVote(
            @Query("groupNumber") int groupNumber,
            @Query("voteStartTime") String voteStartTime
    );

    @POST("vote/insertOneVote")
    Completable insertOneVote(@Body Map<String, String> map);

    @POST("vote/updateVotes")
    Completable updateVotes(@Body Map<String, String> map);

    @POST("vote/findNumberOfNotesOneGroupOneTime")
    Single<ResponseResult> findNumberOfNotesOneGroupOneTime(@Body Map<String, String> map);

    @POST("group/insertOne")
    Completable insertMemberToGroup(@Body Map<String, String> map);

    @POST("group/kickOneMemberOut")
    Completable kickOneMemberOut(@Body Map<String, String> map);

    @POST("group/getAllMembersName")
    Single<ResponseResult> getAllMembersName(@Body Map<String, String> map);

    @GET("group/findAllGroupNumber")
    Single<ResponseResult> findAllGroupNumber(@Query("groupMember") String groupMember);

}
