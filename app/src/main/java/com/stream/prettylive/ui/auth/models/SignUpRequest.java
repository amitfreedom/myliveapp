package com.stream.prettylive.ui.auth.models;

public class SignUpRequest {
    private String email;
    private String password;
    private String deviceId;
    private String deviceToken;

    // Constructor
    public SignUpRequest(String email, String password, String deviceId, String deviceToken) {
        this.email = email;
        this.password = password;
        this.deviceId = deviceId;
        this.deviceToken = deviceToken;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}

