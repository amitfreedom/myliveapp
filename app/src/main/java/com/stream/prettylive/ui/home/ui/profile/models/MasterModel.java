package com.stream.prettylive.ui.home.ui.profile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MasterModel {
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
        @SerializedName("Ucoins")
        @Expose
        private Integer ucoins;
        @SerializedName("Udiamonds")
        @Expose
        private Integer udiamonds;
        @SerializedName("country")
        @Expose
        private Object country;
        @SerializedName("user_profile_pic")
        @Expose
        private String userProfilePic;
        @SerializedName("user_nick_name")
        @Expose
        private String userNickName;
        @SerializedName("status")
        @Expose
        private Boolean status;
        @SerializedName("level")
        @Expose
        private Integer level;
        @SerializedName("vip")
        @Expose
        private Integer vip;
        @SerializedName("host")
        @Expose
        private Integer host;
        @SerializedName("co_host")
        @Expose
        private Integer coHost;
        @SerializedName("followers")
        @Expose
        private Integer followers;
        @SerializedName("following")
        @Expose
        private Integer following;

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

        public Integer getUcoins() {
            return ucoins;
        }

        public void setUcoins(Integer ucoins) {
            this.ucoins = ucoins;
        }

        public Integer getUdiamonds() {
            return udiamonds;
        }

        public void setUdiamonds(Integer udiamonds) {
            this.udiamonds = udiamonds;
        }

        public Object getCountry() {
            return country;
        }

        public void setCountry(Object country) {
            this.country = country;
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

        public Boolean getStatus() {
            return status;
        }

        public void setStatus(Boolean status) {
            this.status = status;
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

        public Integer getHost() {
            return host;
        }

        public void setHost(Integer host) {
            this.host = host;
        }

        public Integer getCoHost() {
            return coHost;
        }

        public void setCoHost(Integer coHost) {
            this.coHost = coHost;
        }

        public Integer getFollowers() {
            return followers;
        }

        public void setFollowers(Integer followers) {
            this.followers = followers;
        }

        public Integer getFollowing() {
            return following;
        }

        public void setFollowing(Integer following) {
            this.following = following;
        }

    }
}
