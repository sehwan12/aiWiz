// com/example/aiwiz/activity/AiListActivity.java

package com.example.aiwiz.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import android.widget.Toast;

import com.example.aiwiz.R;
import com.example.aiwiz.adapter.GeneratedImageAdapter;

import com.example.aiwiz.db.AppDatabase;
import com.example.aiwiz.entity.GeneratedImage;
import com.example.aiwiz.entity.GeneratedImageDao;

import java.util.List;

public class AiListActivity extends AppCompatActivity implements DetailGeneratedImageDialog.OnImageDeletedListener{

    private Toolbar toolbar;
    private RecyclerView generatedImagesRecyclerView;
    private GeneratedImageAdapter adapter;
    private AppDatabase db;
    private GeneratedImageDao generatedImageDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ailist);

        // Toolbar 설정
        toolbar = findViewById(R.id.aiImageListToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("AI Generated Images");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로 가기 버튼
        }

        // RecyclerView 초기화
        generatedImagesRecyclerView = findViewById(R.id.generatedImageRecyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3); // 3열 그리드
        generatedImagesRecyclerView.setLayoutManager(gridLayoutManager);

        // 어댑터 초기화
        adapter = new GeneratedImageAdapter(this, null, new GeneratedImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(GeneratedImage generatedImage) {
                // 아이템 클릭 시 다이얼로그로 이동
                DetailGeneratedImageDialog dialog = new DetailGeneratedImageDialog();
                Bundle bundle = new Bundle();
                bundle.putInt("GENERATED_IMAGE_ID", generatedImage.getId());
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "DetailGeneratedImageDialog");
            }
        });
        generatedImagesRecyclerView.setAdapter(adapter);

        // 데이터베이스 초기화
        db = AppDatabase.getDatabase(this);
        generatedImageDao = db.generatedImageDao();

        // 데이터 로드
        loadGeneratedImages();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 데이터가 변경되었을 수 있으므로 다시 로드
        loadGeneratedImages();
    }

    /**
     * GeneratedImage 테이블에서 모든 이미지를 조회하고 RecyclerView에 설정하는 메서드
     */
    private void loadGeneratedImages() {
        try {
            List<GeneratedImage> generatedImages = generatedImageDao.getAllGeneratedImages();
            if (generatedImages.isEmpty()) {
                Toast.makeText(this, "생성된 이미지가 없습니다.", Toast.LENGTH_SHORT).show();
            }
            adapter.setGeneratedImages(generatedImages);
            Log.d("AiListActivity", "Loaded " + generatedImages.size() + " generated images.");
        } catch (Exception e) {
            Log.e("AiListActivity", "loadGeneratedImages 오류", e);
            Toast.makeText(this, "이미지를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Toolbar의 뒤로 가기 버튼 클릭 시 액티비티 종료
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish(); // 뒤로 가기
        return true;
    }

    /**
     * 삭제 완료 후 RecyclerView를 갱신하기 위한 인터페이스 메서드 구현
     */
    @Override
    public void onImageDeleted() {
        // 이미지가 삭제되었으므로 RecyclerView를 갱신
        loadGeneratedImages();
        Toast.makeText(this, "삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show();
    }
}
