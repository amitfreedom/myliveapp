package com.stream.prettylive.services.repository;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.stream.prettylive.services.ApiService;
import com.stream.prettylive.services.RetrofitClient;
import com.stream.prettylive.ui.auth.models.GoogleLoginRequest;
import com.stream.prettylive.ui.auth.models.LoginRequest;
import com.stream.prettylive.ui.auth.models.SignUpRequest;
import com.stream.prettylive.ui.auth.models.UserMainResponse;
import com.stream.prettylive.ui.auth.models.UserResponseModel;
import com.stream.prettylive.ui.home.ui.profile.models.MasterModel;
import com.stream.prettylive.ui.home.ui.profile.models.UpdateUserRequest;
import com.stream.prettylive.ui.home.ui.profile.models.UserUpdateResponseModel;

import java.io.File;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private final ApiService apiService;

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
            public void onResponse(@NonNull Call<UserResponseModel> call, @NonNull Response<UserResponseModel> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponseModel> call, @NonNull Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<UserResponseModel> googleLogin(String authMethod, String email, String deviceId, String deviceToken,String user_profile_pic) {
        final MutableLiveData<UserResponseModel> data = new MutableLiveData<>();
        GoogleLoginRequest googleLoginRequest = new GoogleLoginRequest(authMethod,email,deviceId,deviceToken,user_profile_pic);
        apiService.googleLogin(googleLoginRequest).enqueue(new Callback<UserResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<UserResponseModel> call, @NonNull Response<UserResponseModel> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponseModel> call, @NonNull Throwable t) {
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
            public void onResponse(@NonNull Call<UserResponseModel> call, @NonNull Response<UserResponseModel> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponseModel> call, @NonNull Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<UserMainResponse> getUser(int id) {
        final MutableLiveData<UserMainResponse> data = new MutableLiveData<>();
        apiService.getUser(id).enqueue(new Callback<UserMainResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserMainResponse> call, @NonNull Response<UserMainResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserMainResponse> call, @NonNull Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<UserMainResponse> updateProfileImage(int id, Uri filePath, Context context) {
        final MutableLiveData<UserMainResponse> data = new MutableLiveData<>();
        File file = new File(filePath.getPath());

        RequestBody requestFile = RequestBody.create(
                MediaType.parse(Objects.requireNonNull(context.getContentResolver().getType(filePath))),
                file
        );
//        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        apiService.updateProfileImage(id, body).enqueue(new Callback<UserMainResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserMainResponse> call, @NonNull Response<UserMainResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserMainResponse> call, @NonNull Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<UserUpdateResponseModel> updateUser(String userId, UpdateUserRequest updateUserRequest) {
        final MutableLiveData<UserUpdateResponseModel> data = new MutableLiveData<>();
        apiService.updateUser(userId, updateUserRequest).enqueue(new Callback<UserUpdateResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<UserUpdateResponseModel> call, @NonNull Response<UserUpdateResponseModel> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserUpdateResponseModel> call, @NonNull Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }


    public LiveData<MasterModel> getMaster(int id) {
        final MutableLiveData<MasterModel> data = new MutableLiveData<>();
        apiService.getMaster(id).enqueue(new Callback<MasterModel>() {
            @Override
            public void onResponse(@NonNull Call<MasterModel> call, @NonNull Response<MasterModel> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MasterModel> call, @NonNull Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }





}

