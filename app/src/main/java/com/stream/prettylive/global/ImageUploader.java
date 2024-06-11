package com.stream.prettylive.global;

import androidx.annotation.NonNull;

import okhttp3.*;

import java.io.File;
import java.io.IOException;

public class ImageUploader {

    OkHttpClient client = new OkHttpClient();

    public void updateImage(File imageFile, String userId) {
        // Define the media type for the image file
        MediaType mediaType = MediaType.parse("application/image");

        // Create request body with multipart form data
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", imageFile.getName(), RequestBody.create(mediaType, imageFile))
                .build();

        // Create the PUT request with the URL and request body
        Request request = new Request.Builder()
                .url("https://apimylive.collegespike.com/socket-server/api/v2/users/upload-profile-pic/111113")
                .put(body)
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Handle the failure
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // Handle the response
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }

                // Handle the successful response
                assert response.body() != null;
                String responseBody = response.body().string();
                System.out.println(responseBody); // Print response body
            }
        });
    }
}

