package com.stream.prettylive.streaming.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.stream.prettylive.services.repository.LiveRepository;
import com.stream.prettylive.streaming.activity.model.LiveStreamingResponse;

import java.util.List;
public class LiveViewModel extends ViewModel{
    private LiveRepository repository;

    public LiveViewModel() {
        repository = new LiveRepository();
    }
    public LiveData<LiveStreamingResponse> createLiveStreaming(String UID, String live_streaming_type) {
        return repository.createLiveStreaming(UID, live_streaming_type);
    }
}
