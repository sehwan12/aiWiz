package com.example.aiwiz.activity;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.aiwiz.db.AppDatabase;
import com.example.aiwiz.entity.GeneratedImage;
import com.example.aiwiz.entity.GeneratedImageDao;
import com.example.aiwiz.entity.LikedPhoto;
import com.example.aiwiz.entity.LikedPhotoDao;
import com.example.aiwiz.R;
import com.example.aiwiz.adapter.AiImageAdapter;
import com.example.aiwiz.api.GenerateImageRequest;

import com.example.aiwiz.api.HuggingFaceApi;
import com.example.aiwiz.api.RetrofitClient;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AiImageActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button generateImageButton;
    private RecyclerView recyclerView;
    private AiImageAdapter aiImageAdapter;
    private AppDatabase db;
    private LikedPhotoDao likedPhotoDao;
    private GeneratedImageDao generatedImageDao;
    private HuggingFaceApi apiService;
    private ProgressBar progressBar;

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000; // 2초

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aiimage);

        // Toolbar 설정
        toolbar = findViewById(R.id.aiImageToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true); // 뒤로 가기 버튼

        // RecyclerView 초기화
        recyclerView = findViewById(R.id.aiImageRecyclerView);
        int numberOfColumns = 3; // 3x3 그리드
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numberOfColumns);
        recyclerView.setLayoutManager(gridLayoutManager);

        aiImageAdapter = new AiImageAdapter(this);
        recyclerView.setAdapter(aiImageAdapter);

        // 데이터베이스 초기화
        db = AppDatabase.getDatabase(this);
        likedPhotoDao = db.likedPhotoDao();
        generatedImageDao = db.generatedImageDao();

        // Retrofit API 서비스 초기화
        apiService = RetrofitClient.getClient().create(HuggingFaceApi.class);

        // 이미지 생성 버튼 초기화
        generateImageButton = findViewById(R.id.generateImageButton);
        generateImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleGenerateImage();
            }
        });

        // ProgressBar 초기화
        progressBar = findViewById(R.id.progressBar);

        loadLikedPhotos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLikedPhotos();
    }

    private void loadLikedPhotos() {
        // Room 데이터베이스는 메인 스레드에서 접근하지 않으므로 별도의 스레드에서 실행
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final List<LikedPhoto> likedPhotos = likedPhotoDao.getAllLikedPhotos();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (likedPhotos != null && !likedPhotos.isEmpty()) {
                            aiImageAdapter.setPhotos(likedPhotos);
                        } else {
                            Toast.makeText(AiImageActivity.this, "좋아요한 사진이 없습니다.", Toast.LENGTH_SHORT).show();
                            aiImageAdapter.setPhotos(new ArrayList<>()); // 빈 리스트 설정
                        }
                    }
                });
            }
        });
    }

    /**
     * 이미지 생성 버튼 클릭 시 처리 메서드
     */
    private void handleGenerateImage() {
        List<LikedPhoto> selectedPhotos = aiImageAdapter.getSelectedPhotos();
        if (selectedPhotos.isEmpty()) {
            Toast.makeText(this, "생성할 이미지를 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 선택된 사진들의 설명을 하나의 프롬프트로 결합
        StringBuilder promptBuilder = new StringBuilder();
        for (int i = 0; i < selectedPhotos.size(); i++) {
            String description = selectedPhotos.get(i).getDescription();
            if (description != null && !description.isEmpty()) {
                promptBuilder.append(description);
                if (i < selectedPhotos.size() - 1) {
                    promptBuilder.append(", ");
                }
            }
        }

        String prompt = promptBuilder.toString();
        if (prompt.isEmpty()) {
            Toast.makeText(this, "선택된 사진들의 설명이 비어 있습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // AI 이미지 생성 로직 호출
        generateAiImage(prompt);
    }

    /**
     * Hugging Face API를 사용하여 AI 이미지 생성
     */
    private void generateAiImage(String prompt) {
        // ProgressBar 표시
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        // API 호출
        GenerateImageRequest request = new GenerateImageRequest(prompt);
        Call<ResponseBody> call = apiService.generateImage(request);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // ProgressBar 숨기기
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });

                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        // Content-Type 확인
                        String contentType = response.headers().get("Content-Type");
                        if (contentType != null && contentType.startsWith("image/")) {
                            // 이미지 데이터 처리
                            try {
                                byte[] imageBytes = responseBody.bytes();
                                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                if (bitmap != null) {
                                    // Bitmap을 ByteArray로 변환
                                    byte[] bitmapBytes = bitmapToByteArray(bitmap);
                                    // Room 데이터베이스에 저장
                                    saveGeneratedImage(bitmapBytes, prompt);
                                    // 생성된 이미지를 표시
                                    showGeneratedImage(bitmap);
                                } else {
                                    Toast.makeText(AiImageActivity.this, "이미지 디코딩 실패.", Toast.LENGTH_SHORT).show();
                                    Log.e("AiImageActivity", "Bitmap 디코딩 실패.");
                                }
                            } catch (IOException e) {
                                Toast.makeText(AiImageActivity.this, "이미지 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                                Log.e("AiImageActivity", "이미지 처리 오류", e);
                            }
                        } else if (contentType != null && contentType.equals("application/json")) {
                            // JSON 응답 처리 (예: 오류 메시지)
                            try {
                                String jsonResponse = responseBody.string();
                                Log.e("AiImageActivity", "JSON 응답: " + jsonResponse);
                                Toast.makeText(AiImageActivity.this, "이미지 생성 실패: " + jsonResponse, Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                Toast.makeText(AiImageActivity.this, "응답 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                                Log.e("AiImageActivity", "JSON 응답 처리 오류", e);
                            }
                        } else {
                            Toast.makeText(AiImageActivity.this, "알 수 없는 응답 형식입니다.", Toast.LENGTH_SHORT).show();
                            Log.e("AiImageActivity", "알 수 없는 Content-Type: " + contentType);
                        }
                    } else {
                        Toast.makeText(AiImageActivity.this, "응답이 비어 있습니다.", Toast.LENGTH_LONG).show();
                        Log.e("AiImageActivity", "응답 바디가 null입니다.");
                    }
                } else {
                    Toast.makeText(AiImageActivity.this, "이미지 생성 실패: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e("AiImageActivity", "응답 실패: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // ProgressBar 숨기기
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });

                Toast.makeText(AiImageActivity.this, "이미지 생성 실패: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("AiImageActivity", "API 호출 실패", t);
            }
        });
    }

    /**
     * Bitmap을 ByteArray로 변환하는 메서드
     */
    private byte[] bitmapToByteArray(Bitmap bitmap) {
        java.io.ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    /**
     * 생성된 이미지를 Room 데이터베이스에 저장하는 메서드
     */
    private void saveGeneratedImage(byte[] imageData, String description) {
        // 데이터베이스 작업은 별도의 스레드에서 수행
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                GeneratedImage generatedImage = new GeneratedImage(imageData, description, new Date());
                generatedImageDao.insert(generatedImage);
                Log.d("AiImageActivity", "이미지 데이터베이스에 저장 완료.");
            }
        });
    }

    /**
     * Base64 디코딩된 Bitmap 이미지를 표시하는 메서드
     */
    private void showGeneratedImage(Bitmap bitmap) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_generated_image, null);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView generatedImageView = dialogView.findViewById(R.id.generatedImageView);
        generatedImageView.setImageBitmap(bitmap);
        builder.setView(dialogView)
                .setPositiveButton("닫기", null)
                .create()
                .show();
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish(); // 뒤로 가기
        return true;
    }
}