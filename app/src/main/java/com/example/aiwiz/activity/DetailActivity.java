package com.example.aiwiz.activity;

// DetailActivity.java

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.aiwiz.db.AppDatabase;
import com.example.aiwiz.entity.LikedPhotoDao;
import com.example.aiwiz.entity.LikedPhoto;
import com.example.aiwiz.R;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;

    private ImageView detailImageView;
    private TextView detailDescription;
    private ImageButton downloadButton;
    private ImageButton likeButton;

    private String photoUrl;
    private String photoId;
    private String photoDescription;

    private AppDatabase db;
    private LikedPhotoDao likedPhotoDao;

    private ExecutorService executorService;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail); // 상세 페이지 XML 파일

        detailImageView = findViewById(R.id.detailImageView);
        detailDescription = findViewById(R.id.detailDescription);
        downloadButton = findViewById(R.id.downloadButton);
        likeButton = findViewById(R.id.likeButton);

        //데이터베이스 초기화
        db = AppDatabase.getDatabase(this);
        likedPhotoDao = db.likedPhotoDao();


        Intent intent = getIntent();
        photoUrl = getIntent().getStringExtra("PHOTO_URL");
        photoDescription = getIntent().getStringExtra("PHOTO_DESCRIPTION");
        photoId=getIntent().getStringExtra("PHOTO_ID");

        if (intent != null && intent.hasExtra("PHOTO_URL")) {
            if (photoUrl != null && !photoUrl.isEmpty()) {
                Glide.with(this)
                        .load(photoUrl)
                        .into(detailImageView);
                detailDescription.setText(photoDescription != null ? photoDescription : "설명이 없습니다.");
            }
        }

        //좋아요 버튼 초기 상태 설정
        updateLikeButton();

        // 다운로드 버튼 클릭 이벤트
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDownload();
            }
        });

        // 좋아요 버튼 클릭 이벤트
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLike();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown(); // ExecutorService 종료
        }
    }

    /**
     * 좋아요 버튼의 상태를 업데이트합니다.
     */
    private void updateLikeButton() {
        if (likedPhotoDao.getLikedPhotoById(photoId) != null) {
            likeButton.setImageResource(R.drawable.heartfilled); // 채워진 하트 아이콘
        } else {
            likeButton.setImageResource(R.drawable.heartlinear); // 빈 하트 아이콘
        }
    }

    /**
     * 좋아요 상태를 토글합니다.
     */
    private void toggleLike() {
        LikedPhoto likedPhoto = likedPhotoDao.getLikedPhotoById(photoId);
        if (likedPhoto != null) {
            // 이미 좋아요한 상태: 좋아요 취소
            likedPhotoDao.delete(likedPhoto);
            likeButton.setImageResource(R.drawable.heartlinear);
            Toast.makeText(this, "좋아요 취소", Toast.LENGTH_SHORT).show();
        } else {
            // 좋아요하지 않은 상태: 좋아요 추가
            LikedPhoto newLikedPhoto = new LikedPhoto(photoId, photoUrl, photoDescription);
            likedPhotoDao.insert(newLikedPhoto);
            likeButton.setImageResource(R.drawable.heartfilled);
            Toast.makeText(this, "좋아요 추가", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 다운로드 권한을 확인하고, 권한이 없으면 요청합니다.
     */
    private void handleDownload() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 이상: 권한 없이 MediaStore 사용
            Log.d("DetailActivity", "Android Q 이상: 권한 없이 다운로드 시도");
            downloadImage();
        } else {
            // Android 9 이하: 권한 확인 및 요청
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d("DetailActivity", "WRITE_EXTERNAL_STORAGE 권한 없음: 권한 요청");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                // 권한이 이미 있는 경우 다운로드 수행
                Log.d("DetailActivity", "WRITE_EXTERNAL_STORAGE 권한 있음: 다운로드 수행");
                downloadImage();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("DetailActivity", "WRITE_EXTERNAL_STORAGE 권한 승인됨: 다운로드 수행");
                downloadImage();
            } else {
                Log.d("DetailActivity", "WRITE_EXTERNAL_STORAGE 권한 거부됨");
                Toast.makeText(this, "저장소 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 이미지를 다운로드하여 저장합니다.
     */
    private void downloadImage() {
        if (photoUrl == null || photoUrl.isEmpty()) {
            Toast.makeText(this, "다운로드할 이미지가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(photoUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    InputStream input = connection.getInputStream();

                    String fileName = "Unsplash_" + photoId + "_" + System.currentTimeMillis() + ".jpg";

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // Android 10 이상
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                        values.put(MediaStore.Downloads.MIME_TYPE, "image/jpeg");
                        values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/AIWiz");
                        values.put(MediaStore.Downloads.IS_PENDING, 1);

                        Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                        if (uri != null) {
                            OutputStream output = getContentResolver().openOutputStream(uri);
                            if (output != null) {
                                byte[] buffer = new byte[1024];
                                int bytesRead;
                                while ((bytesRead = input.read(buffer)) != -1) {
                                    output.write(buffer, 0, bytesRead);
                                }
                                output.flush();
                                output.close();
                            }

                            values.clear();
                            values.put(MediaStore.Images.Media.IS_PENDING, 0);
                            getContentResolver().update(uri, values, null, null);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(DetailActivity.this, "이미지가 다운로드되었습니다.", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } else {
                        // Android 9 이하
                        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/AIWiz");
                        if (!downloadsDir.exists()) {
                            downloadsDir.mkdirs();
                        }
                        File imageFile = new File(downloadsDir, fileName);
                        FileOutputStream output = new FileOutputStream(imageFile);
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = input.read(buffer)) != -1) {
                            output.write(buffer, 0, bytesRead);
                        }
                        output.flush();
                        output.close();
                        input.close();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(DetailActivity.this, "이미지가 다운로드되었습니다: " + imageFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                } catch (Exception e) {
                    Log.e("DetailActivity", "이미지 다운로드 실패", e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DetailActivity.this, "이미지 다운로드 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

}
