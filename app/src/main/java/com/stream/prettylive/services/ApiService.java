package com.stream.prettylive.services;

import com.stream.prettylive.ui.auth.models.LoginRequest;
import com.stream.prettylive.ui.auth.models.SignUpRequest;
import com.stream.prettylive.ui.auth.models.UserResponseModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
//    @GET("/users")
//    Call<List<User>> getUsers();

    @POST("users/login-with-gmail")
    Call<UserResponseModel> login(@Body LoginRequest loginRequest);

    @POST("users/singup-with-gmail")
    Call<UserResponseModel> signUp(@Body SignUpRequest signUpRequest);
}

