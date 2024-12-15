package com.example.aiwiz;

// PhotoSearchActivity.java
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhotoSearchActivity extends AppCompatActivity implements PhotoAdapter.OnItemClickListener {

    private EditText searchEditText;
    private Button searchButton;
    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private List<Photo> photoList;
    private ProgressBar progressBar;

    private UnsplashApi unsplashApi;

    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private String currentQuery = null; // 현재 검색어
    private final int TOTAL_PAGES = 30; // 예시: 최대 페이지 수 설정 (Unsplash API 제한에 따라 조정)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_search); // Figma에서 제공하는 XML 파일로 교체

        searchEditText = findViewById(R.id.searchEditText); // XML의 EditText ID
        searchButton = findViewById(R.id.searchButton); // XML의 Button ID
        recyclerView = findViewById(R.id.recyclerView); // XML의 RecyclerView ID
        progressBar = findViewById(R.id.progressBar); // XML의 ProgressBar ID

        photoList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(this, photoList, this);
        GridLayoutManager gridLayoutManager= new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(photoAdapter);
        recyclerView.setHasFixedSize(true);

        // Retrofit 초기화
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.unsplash.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        unsplashApi = retrofit.create(UnsplashApi.class);

        loadRandomPhotos();
        // 검색 버튼 클릭 이벤트
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = searchEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(query)) {
                    searchPhotos(query);
                } else {
                    Toast.makeText(PhotoSearchActivity.this, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // EditText의 엔터 키(검색 액션) 리스너 설정
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                String query = searchEditText.getText().toString().trim();
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN)) {
                    searchPhotos(query);
                    return true; // 이벤트 소비
                }
                return false; // 이벤트 계속 전달
            }
        });


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) { // 스크롤이 아래로 진행될 때만 실행
                    int visibleItemCount = gridLayoutManager.getChildCount();
                    int totalItemCount = gridLayoutManager.getItemCount();
                    int firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && !isLastPage) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                && firstVisibleItemPosition >= 0
                                && totalItemCount >= 30) { // per_page 설정과 일치
                            loadMorePhotos();
                        }
                    }
                }
            }
        });
    }

    private void searchPhotos(String query) {
        progressBar.setVisibility(View.VISIBLE);
        Call<SearchResponse> call = unsplashApi.searchPhotos(query, 1, 30, BuildConfig.UNSPLASH_ACCESS_KEY);
        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    SearchResponse searchResponse = response.body();
                    if (searchResponse != null && searchResponse.getResults() != null) {
                        photoList = searchResponse.getResults();
                        photoAdapter.setPhotos(photoList);
                    } else {

                        Toast.makeText(PhotoSearchActivity.this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 오류 응답일 때 상세 정보 로그 출력
                    Log.e("PhotoSearch", "검색 실패: " + response.code() + " " + response.message());
                    Toast.makeText(PhotoSearchActivity.this, "검색 실패: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("PhotoSearch", "네트워크 오류: " + t.getMessage());
                Toast.makeText(PhotoSearchActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

//    @Override
//    public void onItemClick(int position) {
//        // 사진 클릭 시 상세 페이지로 이동하거나, 다른 동작을 수행할 수 있습니다.
//        Photo clickedPhoto = photoList.get(position);
//        Intent intent = new Intent(this, DetailActivity.class);
//        intent.putExtra("photo", clickedPhoto);
//        startActivity(intent);
//    }
    @Override
    public void onItemClick(Photo photo) {
        if (photo != null && photo.getUrls()!=null) {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("PHOTO_URL", photo.getUrls().getRegular());

            // 설명(description) 전달
            String description = photo.getDescription();
            if (description == null || description.isEmpty()) {
                description = photo.getAlt_description(); // 대체 설명 사용
            }
            intent.putExtra("PHOTO_DESCRIPTION", description);
            startActivity(intent);
        } else {
            Toast.makeText(this, "사진을 불러오는 중입니다.", Toast.LENGTH_SHORT).show();
        }
    }


    // 랜덤 이미지 로드 메서드
    private void loadRandomPhotos() {
        progressBar.setVisibility(View.VISIBLE);
        Call<List<Photo>> call = unsplashApi.getRandomPhotos(30, BuildConfig.UNSPLASH_ACCESS_KEY);
        call.enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(Call<List<Photo>> call, Response<List<Photo>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    List<Photo> photos = response.body();
                    if (photos != null && !photos.isEmpty()) {
                        Log.d("ActivityPhotoSearch", "랜덤 이미지 로드 성공: " + photos.size() + "개 로드");
                        photoAdapter.setPhotos(photos);
                    } else {
                        Toast.makeText(PhotoSearchActivity.this, "랜덤 이미지가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("ActivityPhotoSearch", "랜덤 이미지 로드 실패: " + response.code() + " " + response.message());
                    Toast.makeText(PhotoSearchActivity.this, "랜덤 이미지 로드 실패: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Photo>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("PhotoSearch", "랜덤 이미지 네트워크 오류: " + t.getMessage());
                Toast.makeText(PhotoSearchActivity.this, "랜덤 이미지 네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void loadMorePhotos() {
        if (isLoading) return;
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);

        if (currentQuery != null && !currentQuery.isEmpty()) {
            // 검색 모드인 경우
            Call<SearchResponse> call = unsplashApi.searchPhotos(currentQuery, currentPage + 1, 30, BuildConfig.UNSPLASH_ACCESS_KEY);
            call.enqueue(new Callback<SearchResponse>() {
                @Override
                public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    isLoading = false;
                    if (response.isSuccessful()) {
                        SearchResponse searchResponse = response.body();
                        if (searchResponse != null && searchResponse.getResults() != null) {
                            List<Photo> photos = searchResponse.getResults();
                            Log.d("photosearch", "추가 검색 성공: " + photos.size() + "개 로드");
                            if (!photos.isEmpty()) {
                                photoAdapter.addPhotos(photos);
                                currentPage++;

                                if (currentPage >= searchResponse.getTotal_pages()) {
                                    isLastPage = true;
                                }
                            } else {
                                Toast.makeText(PhotoSearchActivity.this, "더 이상 검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                                isLastPage = true;
                            }
                        }
                    } else {
                        Log.e("photosearch", "추가 검색 실패: " + response.code() + " " + response.message());
                        Toast.makeText(PhotoSearchActivity.this, "추가 검색 실패: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SearchResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    isLoading = false;
                    Log.e("photosearch", "추가 검색 네트워크 오류: " + t.getMessage());
                    Toast.makeText(PhotoSearchActivity.this, "추가 검색 네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // 랜덤 이미지 모드인 경우 (Unsplash API는 랜덤 이미지에 페이지 개념이 없으므로 count를 늘려 다시 요청)
            Call<List<Photo>> call = unsplashApi.getRandomPhotos(30, BuildConfig.UNSPLASH_ACCESS_KEY);
            call.enqueue(new Callback<List<Photo>>() {
                @Override
                public void onResponse(Call<List<Photo>> call, Response<List<Photo>> response) {
                    progressBar.setVisibility(View.GONE);
                    isLoading = false;
                    if (response.isSuccessful()) {
                        List<Photo> photos = response.body();
                        if (photos != null && !photos.isEmpty()) {
                            Log.d("photosearch", "추가 랜덤 이미지 로드 성공: " + photos.size() + "개 로드");
                            photoAdapter.addPhotos(photos);
                        } else {
                            Toast.makeText(PhotoSearchActivity.this, "추가 랜덤 이미지가 없습니다.", Toast.LENGTH_SHORT).show();
                            isLastPage = true;
                        }
                    } else {
                        Log.e("photosearch", "추가 랜덤 이미지 로드 실패: " + response.code() + " " + response.message());
                        Toast.makeText(PhotoSearchActivity.this, "추가 랜덤 이미지 로드 실패: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Photo>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    isLoading = false;
                    Log.e("photosearch", "추가 랜덤 이미지 네트워크 오류: " + t.getMessage());
                    Toast.makeText(PhotoSearchActivity.this, "추가 랜덤 이미지 네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
