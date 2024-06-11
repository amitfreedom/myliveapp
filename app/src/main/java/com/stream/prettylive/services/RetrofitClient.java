package com.stream.prettylive.services;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://apimylive.collegespike.com/socket-server/api/v2/";
    private static RetrofitClient instance;
    private Retrofit retrofit;
    private String jwtToken;

    private RetrofitClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();
                        Request.Builder builder = originalRequest.newBuilder();
                        builder.addHeader("Content-Type", "application/json");
//                        if (jwtToken != null && !jwtToken.isEmpty()) {
//                            builder.addHeader("Authorization", "Bearer " + jwtToken);
//                        }
                        Request requestWithHeaders = builder.build();
                        return chain.proceed(requestWithHeaders);
                    }
                })
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public void setJwtToken(String token) {
        this.jwtToken = token;
    }

    public ApiService getApi() {
        return retrofit.create(ApiService.class);
    }
}