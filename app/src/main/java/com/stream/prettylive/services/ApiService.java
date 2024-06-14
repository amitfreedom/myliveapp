package com.stream.prettylive.services;

import com.stream.prettylive.streaming.activity.model.LiveStreamingRequest;
import com.stream.prettylive.streaming.activity.model.LiveStreamingResponse;
import com.stream.prettylive.ui.auth.models.GoogleLoginRequest;
import com.stream.prettylive.ui.auth.models.LoginRequest;
import com.stream.prettylive.ui.auth.models.SignUpRequest;
import com.stream.prettylive.ui.auth.models.UserMainResponse;
import com.stream.prettylive.ui.auth.models.UserResponseModel;
import com.stream.prettylive.ui.home.ui.profile.models.FetchImageResponseModel;
import com.stream.prettylive.ui.home.ui.profile.models.MasterModel;
import com.stream.prettylive.ui.home.ui.profile.models.UpdateUserRequest;
import com.stream.prettylive.ui.home.ui.profile.models.UserUpdateResponseModel;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {
//    @GET("/users")
//    Call<List<User>> getUsers();

    @POST("users/login-with-gmail")
    Call<UserResponseModel> login(@Body LoginRequest loginRequest);

    @POST("users/singup-with-google")
    Call<UserResponseModel> googleLogin(@Body GoogleLoginRequest googleLoginRequest);
    @POST("users/singup-with-gmail")
    Call<UserResponseModel> signUp(@Body SignUpRequest signUpRequest);

    @GET("users/get-user/{id}")
    Call<UserMainResponse> getUser(@Path("id") int id);

    @Multipart
    @POST("users/upload-profile-pic/{id}")
    Call<FetchImageResponseModel> updateProfileImage(@Path("id") String id, @Part MultipartBody.Part file);

    @PUT("users/update-user/{id}")
    Call<UserUpdateResponseModel> updateUser(@Path("id") String id, @Body UpdateUserRequest updateUserRequest);

    @GET("master/{id}")
    Call<MasterModel> getMaster(@Path("id") int id);

    @POST("livestreaming/create-live-streaming") Call<LiveStreamingResponse> createLiveStreaming(@Body LiveStreamingRequest liveStreamingRequest);


}

