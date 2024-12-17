package com.example.aiwiz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.core.splashscreen.SplashScreen;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aiwiz.R;

public class MainActivity extends AppCompatActivity {

    private ImageView imgPhotoSearch;
    private ImageView imgLikes;
    private ImageView imgAiImage;
    private ImageView imgAiList;
    private ImageView imgAiVideo;
    private ImageView imgHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen=SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ImageView 초기화
        imgPhotoSearch = findViewById(R.id.imgPhotoSearch);
        imgLikes=findViewById(R.id.imgLikes);
        // Click Listener 설정
        imgPhotoSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PhotoSearchActivity.class);
                startActivity(intent);
            }
        });

        imgLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LikedPhotosActivity.class);
                startActivity(intent);
            }
        });

        imgAiImage=findViewById(R.id.imgAIImageGenerate);
        imgAiImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AiImageActivity.class);
                startActivity(intent);
            }
        });

        imgAiList=findViewById(R.id.aiImageList);
        imgAiList.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AiListActivity.class);
                startActivity(intent);
            }
        });

        imgAiVideo=findViewById(R.id.aiVideo);
        imgAiVideo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showComingSoonDialog();
            }
        });

        imgHelp=findViewById(R.id.imgHelp);
        imgHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHelpDialog();
            }
        });
    }

    // 다이얼로그 표시 함수
    private void showComingSoonDialog() {
        new AlertDialog.Builder(this)
                .setTitle("기능 준비 중")
                .setMessage("앞으로 추가될 기능입니다. 많은 기대 부탁드려요~")
                .setPositiveButton("확인", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    /**
     * 도움말 다이얼로그 표시 메서드
     */
    private void showHelpDialog() {
        HelpDialogFragment helpDialog = HelpDialogFragment.newInstance();
        helpDialog.show(getSupportFragmentManager(), "HelpDialogFragment");
    }
}