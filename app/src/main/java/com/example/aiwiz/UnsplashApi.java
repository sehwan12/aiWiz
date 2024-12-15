package com.example.aiwiz;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface UnsplashApi {

    @Headers("Accept-Version: v1")
    @GET("search/photos")
    Call<SearchResponse> searchPhotos(
            @Query("query") String query,
            @Query("page") int page,
            @Query("per_page") int perPage,
            @Query("client_id") String clientId
    );

    // 랜덤 이미지 가져오기 메서드
    @GET("photos/random")
    Call<List<Photo>> getRandomPhotos(
            @Query("count") int count,
            @Query("client_id") String clientId
    );
}
