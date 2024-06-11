package com.stream.prettylive.ui.home.ui.profile.models;

public class UpdateUserRequest {
    private String user_nick_name;
    private String email;
    private String UID;
    private boolean status = true;
    private String device_token;
    private String deviceId;
    private String username;

    public UpdateUserRequest(String user_nick_name, String email, String UID, boolean status, String device_token, String deviceId, String username) {
        this.user_nick_name = user_nick_name;
        this.email = email;
        this.UID = UID;
        this.status = status;
        this.device_token = device_token;
        this.deviceId = deviceId;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

