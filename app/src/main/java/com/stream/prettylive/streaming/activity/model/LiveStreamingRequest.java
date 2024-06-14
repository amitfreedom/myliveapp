package com.stream.prettylive.streaming.activity.model;

public class LiveStreamingRequest {
    private String UID;
    private String live_streaming_type;

    // Constructor
    public LiveStreamingRequest(String UID, String live_streaming_type) {
        this.UID = UID;
        this.live_streaming_type = live_streaming_type;
    }

    // Getters and Setters
    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getLive_streaming_type() {
        return live_streaming_type;
    }

    public void setLive_streaming_type(String live_streaming_type) {
        this.live_streaming_type = live_streaming_type;
    }
}

