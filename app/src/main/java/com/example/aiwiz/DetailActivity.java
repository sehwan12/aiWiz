package com.example.aiwiz;

// DetailActivity.java

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class DetailActivity extends AppCompatActivity {

    private ImageView detailImageView;
    private TextView detailDescription;
    private Button downloadButton;
    private Button likeButton;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail); // 상세 페이지 XML 파일

        detailImageView = findViewById(R.id.detailImageView);
        detailDescription = findViewById(R.id.detailDescription);
        downloadButton = findViewById(R.id.downloadButton);
        likeButton = findViewById(R.id.likeButton);
        String photoDescription = getIntent().getStringExtra("PHOTO_DESCRIPTION");
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("PHOTO_URL")) {
            String photoUrl = getIntent().getStringExtra("PHOTO_URL");
            if (photoUrl != null && !photoUrl.isEmpty()) {
                Glide.with(this)
                        .load(photoUrl)
                        //.placeholder(R.drawable.placeholder)
                        //.error(R.drawable.error_placeholder)
                        .into(detailImageView);
                detailDescription.setText(photoDescription != null ? photoDescription : "설명이 없습니다.");
            }
        }

        // 다운로드 버튼 클릭 이벤트
        downloadButton.setOnClickListener(v -> {
            // 다운로드 로직 구현
        });

        // 좋아요 버튼 클릭 이벤트
        likeButton.setOnClickListener(v -> {
            // 좋아요 로직 구현
        });
    }
}
