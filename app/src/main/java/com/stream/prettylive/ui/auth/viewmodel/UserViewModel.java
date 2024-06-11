package com.stream.prettylive.ui.auth.viewmodel;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.stream.prettylive.services.repository.UserRepository;
import com.stream.prettylive.ui.auth.models.UserMainResponse;
import com.stream.prettylive.ui.auth.models.UserResponseModel;
import com.stream.prettylive.ui.home.ui.profile.models.MasterModel;
import com.stream.prettylive.ui.home.ui.profile.models.UpdateUserRequest;
import com.stream.prettylive.ui.home.ui.profile.models.UserUpdateResponseModel;

import java.util.List;

public class UserViewModel extends ViewModel {
    private UserRepository repository;
    private LiveData<List<UserResponseModel>> users;

    public UserViewModel() {
        repository = new UserRepository();
//        users = repository.getUsers();
    }

//    public LiveData<List<User>> getUsers() {
//        return users;
//    }

    public LiveData<UserResponseModel> login(String email, String password) {
        return repository.login(email, password);
    }

    public LiveData<UserResponseModel> googleLogin(String authMethod, String email, String deviceId, String deviceToken,String user_profile_pic) {
        return repository.googleLogin(authMethod,email,deviceId,deviceToken,user_profile_pic);
    }
    public LiveData<UserResponseModel> signUp(String email, String password, String deviceId, String deviceToken) {
        return repository.signUp(email, password, deviceId, deviceToken);
    }

    public LiveData<UserMainResponse> getUser(int id) {
        return repository.getUser(id);
    }

    public LiveData<UserMainResponse> updateProfileImage(int id, Uri filePath, Context context) {
        return repository.updateProfileImage(id, filePath, context);
    }

    public LiveData<UserUpdateResponseModel> updateUser(String userId, UpdateUserRequest updateUserRequest) {
        return repository.updateUser(userId, updateUserRequest);
    }

    public LiveData<MasterModel> getMaster(int id) {
        return repository.getMaster(id);
    }
}

