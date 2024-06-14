package com.stream.prettylive.services.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.stream.prettylive.services.ApiService;
import com.stream.prettylive.services.RetrofitClient;
import com.stream.prettylive.streaming.activity.model.LiveStreamingRequest;
import com.stream.prettylive.streaming.activity.model.LiveStreamingResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveRepository {
    private ApiService apiService;

    public LiveRepository() {
        apiService = RetrofitClient.getInstance().getApi();
    }

    public LiveData<LiveStreamingResponse> createLiveStreaming(String UID, String live_streaming_type) {
        final MutableLiveData<LiveStreamingResponse> data = new MutableLiveData<>();
        LiveStreamingRequest liveStreamingRequest = new LiveStreamingRequest(UID, live_streaming_type);
        apiService.createLiveStreaming(liveStreamingRequest).enqueue(new Callback<LiveStreamingResponse>() {
            @Override
            public void onResponse(@NonNull Call<LiveStreamingResponse> call, @NonNull Response<LiveStreamingResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<LiveStreamingResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

}
