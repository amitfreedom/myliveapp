package com.stream.prettylive.ui.auth.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserMainResponse {
    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("show")
    @Expose
    private Boolean show;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Boolean getShow() {
        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }

    public class Data {

        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("UID")
        @Expose
        private String uid;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("coins")
        @Expose
        private Integer coins;
        @SerializedName("diamonds")
        @Expose
        private Integer diamonds;
        @SerializedName("account_create_method")
        @Expose
        private String accountCreateMethod;
        @SerializedName("account_status")
        @Expose
        private String accountStatus;
        @SerializedName("user_profile_pic")
        @Expose
        private String userProfilePic;
        @SerializedName("user_nick_name")
        @Expose
        private String userNickName;
        @SerializedName("device_token")
        @Expose
        private Object deviceToken;
        @SerializedName("device_id")
        @Expose
        private Object deviceId;
        @SerializedName("device_status")
        @Expose
        private String deviceStatus;
        @SerializedName("delete_status")
        @Expose
        private Boolean deleteStatus;
        @SerializedName("level")
        @Expose
        private Integer level;
        @SerializedName("vip")
        @Expose
        private Integer vip;
        @SerializedName("is_verified")
        @Expose
        private Boolean isVerified;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Integer getCoins() {
            return coins;
        }

        public void setCoins(Integer coins) {
            this.coins = coins;
        }

        public Integer getDiamonds() {
            return diamonds;
        }

        public void setDiamonds(Integer diamonds) {
            this.diamonds = diamonds;
        }

        public String getAccountCreateMethod() {
            return accountCreateMethod;
        }

        public void setAccountCreateMethod(String accountCreateMethod) {
            this.accountCreateMethod = accountCreateMethod;
        }

        public String getAccountStatus() {
            return accountStatus;
        }

        public void setAccountStatus(String accountStatus) {
            this.accountStatus = accountStatus;
        }

        public String getUserProfilePic() {
            return userProfilePic;
        }

        public void setUserProfilePic(String userProfilePic) {
            this.userProfilePic = userProfilePic;
        }

        public String getUserNickName() {
            return userNickName;
        }

        public void setUserNickName(String userNickName) {
            this.userNickName = userNickName;
        }

        public Object getDeviceToken() {
            return deviceToken;
        }

        public void setDeviceToken(Object deviceToken) {
            this.deviceToken = deviceToken;
        }

        public Object getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(Object deviceId) {
            this.deviceId = deviceId;
        }

        public String getDeviceStatus() {
            return deviceStatus;
        }

        public void setDeviceStatus(String deviceStatus) {
            this.deviceStatus = deviceStatus;
        }

        public Boolean getDeleteStatus() {
            return deleteStatus;
        }

        public void setDeleteStatus(Boolean deleteStatus) {
            this.deleteStatus = deleteStatus;
        }

        public Integer getLevel() {
            return level;
        }

        public void setLevel(Integer level) {
            this.level = level;
        }

        public Integer getVip() {
            return vip;
        }

        public void setVip(Integer vip) {
            this.vip = vip;
        }

        public Boolean getIsVerified() {
            return isVerified;
        }

        public void setIsVerified(Boolean isVerified) {
            this.isVerified = isVerified;
        }

    }
}
