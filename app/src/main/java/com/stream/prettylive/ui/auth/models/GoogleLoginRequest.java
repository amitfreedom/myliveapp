package com.stream.prettylive.ui.auth.models;

public class GoogleLoginRequest {
    private String authMethod="";
    private String email="";
    private String deviceId="";
    private String deviceToken="";
    private String user_profile_pic="";

    public GoogleLoginRequest(String authMethod, String email, String deviceId, String deviceToken, String user_profile_pic) {
        this.authMethod = authMethod;
        this.email = email;
        this.deviceId = deviceId;
        this.deviceToken = deviceToken;
        this.user_profile_pic = user_profile_pic;
    }

    public String getAuthMethod() {
        return authMethod;
    }

    public void setAuthMethod(String authMethod) {
        this.authMethod = authMethod;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getUser_profile_pic() {
        return user_profile_pic;
    }

    public void setUser_profile_pic(String user_profile_pic) {
        this.user_profile_pic = user_profile_pic;
    }
}
