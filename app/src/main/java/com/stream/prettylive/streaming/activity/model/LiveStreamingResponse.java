package com.stream.prettylive.streaming.activity.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LiveStreamingResponse {
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

        @SerializedName("UID")
        @Expose
        private String uid;
        @SerializedName("live_streaming_channel_id")
        @Expose
        private String liveStreamingChannelId;
        @SerializedName("live_streaming_token")
        @Expose
        private String liveStreamingToken;
        @SerializedName("star")
        @Expose
        private Integer star;
        @SerializedName("backGroundImage")
        @Expose
        private Integer backGroundImage;
        @SerializedName("live_streaming_type")
        @Expose
        private String liveStreamingType;
        @SerializedName("voiceMute")
        @Expose
        private Boolean voiceMute;
        @SerializedName("videoMute")
        @Expose
        private Boolean videoMute;
        @SerializedName("role")
        @Expose
        private String role;
        @SerializedName("live_streaming_start_time")
        @Expose
        private String liveStreamingStartTime;
        @SerializedName("live_streaming_current_status")
        @Expose
        private String liveStreamingCurrentStatus;
        @SerializedName("coins")
        @Expose
        private Integer coins;
        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("userJoined")
        @Expose
        private List<Object> userJoined;
        @SerializedName("userJoinedOnSeat")
        @Expose
        private List<UserJoinedOnSeat> userJoinedOnSeat;
        @SerializedName("last_update")
        @Expose
        private String lastUpdate;
        @SerializedName("createdAt")
        @Expose
        private String createdAt;
        @SerializedName("__v")
        @Expose
        private Integer v;
        @SerializedName("level")
        @Expose
        private String level;
        @SerializedName("host")
        @Expose
        private String host;
        @SerializedName("user_nick_name")
        @Expose
        private String userNickName;
        @SerializedName("user_profile_pic")
        @Expose
        private String userProfilePic;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getLiveStreamingChannelId() {
            return liveStreamingChannelId;
        }

        public void setLiveStreamingChannelId(String liveStreamingChannelId) {
            this.liveStreamingChannelId = liveStreamingChannelId;
        }

        public String getLiveStreamingToken() {
            return liveStreamingToken;
        }

        public void setLiveStreamingToken(String liveStreamingToken) {
            this.liveStreamingToken = liveStreamingToken;
        }

        public Integer getStar() {
            return star;
        }

        public void setStar(Integer star) {
            this.star = star;
        }

        public Integer getBackGroundImage() {
            return backGroundImage;
        }

        public void setBackGroundImage(Integer backGroundImage) {
            this.backGroundImage = backGroundImage;
        }

        public String getLiveStreamingType() {
            return liveStreamingType;
        }

        public void setLiveStreamingType(String liveStreamingType) {
            this.liveStreamingType = liveStreamingType;
        }

        public Boolean getVoiceMute() {
            return voiceMute;
        }

        public void setVoiceMute(Boolean voiceMute) {
            this.voiceMute = voiceMute;
        }

        public Boolean getVideoMute() {
            return videoMute;
        }

        public void setVideoMute(Boolean videoMute) {
            this.videoMute = videoMute;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getLiveStreamingStartTime() {
            return liveStreamingStartTime;
        }

        public void setLiveStreamingStartTime(String liveStreamingStartTime) {
            this.liveStreamingStartTime = liveStreamingStartTime;
        }

        public String getLiveStreamingCurrentStatus() {
            return liveStreamingCurrentStatus;
        }

        public void setLiveStreamingCurrentStatus(String liveStreamingCurrentStatus) {
            this.liveStreamingCurrentStatus = liveStreamingCurrentStatus;
        }

        public Integer getCoins() {
            return coins;
        }

        public void setCoins(Integer coins) {
            this.coins = coins;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<Object> getUserJoined() {
            return userJoined;
        }

        public void setUserJoined(List<Object> userJoined) {
            this.userJoined = userJoined;
        }

        public List<UserJoinedOnSeat> getUserJoinedOnSeat() {
            return userJoinedOnSeat;
        }

        public void setUserJoinedOnSeat(List<UserJoinedOnSeat> userJoinedOnSeat) {
            this.userJoinedOnSeat = userJoinedOnSeat;
        }

        public String getLastUpdate() {
            return lastUpdate;
        }

        public void setLastUpdate(String lastUpdate) {
            this.lastUpdate = lastUpdate;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public Integer getV() {
            return v;
        }

        public void setV(Integer v) {
            this.v = v;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getUserNickName() {
            return userNickName;
        }

        public void setUserNickName(String userNickName) {
            this.userNickName = userNickName;
        }

        public String getUserProfilePic() {
            return userProfilePic;
        }

        public void setUserProfilePic(String userProfilePic) {
            this.userProfilePic = userProfilePic;
        }

        public class UserJoinedOnSeat {

            @SerializedName("request_accept_status")
            @Expose
            private Boolean requestAcceptStatus;
            @SerializedName("seat_no")
            @Expose
            private Integer seatNo;

            public Boolean getRequestAcceptStatus() {
                return requestAcceptStatus;
            }

            public void setRequestAcceptStatus(Boolean requestAcceptStatus) {
                this.requestAcceptStatus = requestAcceptStatus;
            }

            public Integer getSeatNo() {
                return seatNo;
            }

            public void setSeatNo(Integer seatNo) {
                this.seatNo = seatNo;
            }

        }



    }



}
