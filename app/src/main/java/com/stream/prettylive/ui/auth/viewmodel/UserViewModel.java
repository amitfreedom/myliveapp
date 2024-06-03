package com.stream.prettylive.ui.auth.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.stream.prettylive.services.repository.UserRepository;
import com.stream.prettylive.ui.auth.models.UserResponseModel;

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
    public LiveData<UserResponseModel> signUp(String email, String password, String deviceId, String deviceToken) {
        return repository.signUp(email, password, deviceId, deviceToken);
    }
}

