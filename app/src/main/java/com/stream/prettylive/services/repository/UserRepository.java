package com.stream.prettylive.services.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.stream.prettylive.services.ApiService;
import com.stream.prettylive.services.RetrofitClient;
import com.stream.prettylive.ui.auth.models.LoginRequest;
import com.stream.prettylive.ui.auth.models.SignUpRequest;
import com.stream.prettylive.ui.auth.models.UserResponseModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private ApiService apiService;

    public UserRepository() {
        apiService = RetrofitClient.getInstance().getApi();
    }

//    public LiveData<List<User>> getUsers() {
//        final MutableLiveData<List<User>> data = new MutableLiveData<>();
//        apiService.getUsers().enqueue(new Callback<List<User>>() {
//            @Override
//            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
//                data.setValue(response.body());
//            }
//
//            @Override
//            public void onFailure(Call<List<User>> call, Throwable t) {
//                data.setValue(null);
//            }
//        });
//        return data;
//    }

    public LiveData<UserResponseModel> login(String email, String password) {
        final MutableLiveData<UserResponseModel> data = new MutableLiveData<>();
        LoginRequest loginRequest = new LoginRequest(email, password);
        apiService.login(loginRequest).enqueue(new Callback<UserResponseModel>() {
            @Override
            public void onResponse(Call<UserResponseModel> call, Response<UserResponseModel> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<UserResponseModel> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<UserResponseModel> signUp(String email, String password, String deviceId, String deviceToken) {
        final MutableLiveData<UserResponseModel> data = new MutableLiveData<>();
        SignUpRequest signUpRequest = new SignUpRequest(email, password, deviceId, deviceToken);
        apiService.signUp(signUpRequest).enqueue(new Callback<UserResponseModel>() {
            @Override
            public void onResponse(Call<UserResponseModel> call, Response<UserResponseModel> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<UserResponseModel> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }


}

