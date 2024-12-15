package com.example.aiwiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private ImageView imgPhotoSearch;
    // 다른 ImageView들은 추후에 추가할 수 있습니다.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ImageView 초기화
        imgPhotoSearch = findViewById(R.id.imgPhotoSearch);

        // Click Listener 설정
        imgPhotoSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PhotoSearchActivity.class);
                startActivity(intent);
            }
        });

        // 다른 ImageView들도 필요 시 클릭 리스너를 추가하세요.
    }
}