package com.example.aiwiz.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import com.example.aiwiz.BuildConfig;

public interface HuggingFaceApi {
    @Headers({
            "Content-Type: application/json",
            "Authorization: Bearer "+ BuildConfig.HUGGING_FACE_API_KEY
    })
    @POST("models/stabilityai/stable-diffusion-2")
    Call<ResponseBody> generateImage(@Body GenerateImageRequest request);

}
