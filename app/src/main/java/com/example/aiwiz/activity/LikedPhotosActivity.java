package com.example.aiwiz.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import com.example.aiwiz.AppDatabase;
import com.example.aiwiz.LikedPhoto;
import com.example.aiwiz.LikedPhotoDao;
import com.example.aiwiz.R;
import com.example.aiwiz.adapter.LikedPhotoAdapter;
import com.example.aiwiz.adapter.PhotoAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LikedPhotosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LikedPhotoAdapter photoAdapter;
    private AppDatabase db;
    private LikedPhotoDao likedPhotoDao;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_photos);

        // Toolbar 설정 (선택 사항)
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.likedPhotosToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true); // 뒤로 가기 버튼

        // RecyclerView 초기화
        recyclerView = findViewById(R.id.likedPhotosRecyclerView);
        int numberOfColumns = 3; // 3x3 그리드
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numberOfColumns);
        recyclerView.setLayoutManager(gridLayoutManager);

        photoAdapter = new LikedPhotoAdapter(this);
        recyclerView.setAdapter(photoAdapter);

        // 데이터베이스 초기화
        db = AppDatabase.getDatabase(this);
        likedPhotoDao = db.likedPhotoDao();

        loadLikedPhotos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLikedPhotos();
    }

    private void loadLikedPhotos() {
        List<LikedPhoto> likedPhotos = likedPhotoDao.getAllLikedPhotos();
        if (likedPhotos != null && !likedPhotos.isEmpty()) {
            photoAdapter.setLikedPhotos(likedPhotos);
        } else {
            Toast.makeText(this, "좋아요한 사진이 없습니다.", Toast.LENGTH_SHORT).show();
            photoAdapter.setLikedPhotos(new ArrayList<>()); // 빈 리스트 설정
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // 뒤로 가기
        return true;
    }
}
