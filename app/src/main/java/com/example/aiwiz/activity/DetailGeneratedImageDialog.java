package com.example.aiwiz.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.DialogCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.aiwiz.R;
import com.example.aiwiz.db.AppDatabase;
import com.example.aiwiz.entity.GeneratedImage;
import com.example.aiwiz.entity.GeneratedImageDao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DetailGeneratedImageDialog extends DialogFragment {

    private static final int REQUEST_WRITE_STORAGE = 112;

    private ImageView generatedImageDetailView;
    private TextView descriptionTextView;
    private ImageButton downloadButton;
    private ImageButton deleteButton;

    private GeneratedImageDao generatedImageDao;
    private GeneratedImage generatedImage;

    // 인터페이스 정의: 삭제 완료 후 Activity에 알리기 위한 콜백
    public interface OnImageDeletedListener {
        void onImageDeleted();
    }

    private OnImageDeletedListener deleteListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnImageDeletedListener) {
            deleteListener = (OnImageDeletedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnImageDeletedListener");
        }
    }

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // 다이얼로그 레이아웃 인플레이트
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_generated_image, null);

        generatedImageDetailView = view.findViewById(R.id.generatedImageView);
        descriptionTextView = view.findViewById(R.id.generatedImageDetailDescription);
        downloadButton = view.findViewById(R.id.downloadButton);
        deleteButton = view.findViewById(R.id.deleteButton);

        // 데이터베이스 초기화
        AppDatabase db = AppDatabase.getDatabase(getContext());
        generatedImageDao = db.generatedImageDao();

        // 전달된 GeneratedImage ID 가져오기
        Bundle args = getArguments();
        if (args != null) {
            int generatedImageId = args.getInt("GENERATED_IMAGE_ID", -1);
            if (generatedImageId != -1) {
                generatedImage = generatedImageDao.getGeneratedImageById(generatedImageId);
                if (generatedImage != null) {
                    // byte[]를 Bitmap으로 변환하여 ImageView에 설정
                    byte[] imageData = generatedImage.getImageData();
                    if (imageData != null && imageData.length > 0) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                        generatedImageDetailView.setImageBitmap(bitmap);
                    } else {
                        // 이미지가 없을 경우 placeholder 설정
                        //generatedImageDetailView.setImageResource(R.drawable.placeholder);
                    }

                    // 설명 설정
                    descriptionTextView.setText(generatedImage.getDescription());
                }
            }
        }

        // 다운로드 버튼 클릭 리스너 설정
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissionAndDownload();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmAndDeleteImage();
            }
        });

        // 다이얼로그 빌더 설정
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setNegativeButton("닫기", null);
        return builder.create();
    }

    /**
     * 이미지 삭제를 확인하고 수행하는 메서드
     */
    private void confirmAndDeleteImage() {
        if (generatedImage == null) {
            Toast.makeText(getContext(), "삭제할 이미지가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 삭제 확인 다이얼로그 표시
        new AlertDialog.Builder(getContext())
                .setTitle("이미지 삭제")
                .setMessage("이 이미지를 삭제하시겠습니까?")
                .setPositiveButton("삭제", (dialog, which) -> deleteImage())
                .setNegativeButton("취소", null)
                .show();
    }

    /**
     * 이미지를 데이터베이스에서 삭제하는 메서드
     */
    private void deleteImage() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                generatedImageDao.delete(generatedImage);
                Log.d("DetailGeneratedImageDialog", "이미지 삭제 완료. ID: " + generatedImage.getId());
                if (getActivity() != null) { // Activity가 null이 아닌지 확인
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "이미지가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        if (deleteListener != null) {
                            deleteListener.onImageDeleted(); // Activity에 삭제 완료 알림
                        }
                        dismiss(); // 다이얼로그 닫기
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) { // Activity가 null이 아닌지 확인
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "이미지 삭제 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show());
                }
                Log.e("DetailGeneratedImageDialog", "이미지 삭제 오류", e);
            }
        });
    }



    /**
     /**
     * Android 버전에 따라 권한을 확인하고 다운로드를 수행하는 메서드
     */
    private void checkPermissionAndDownload() {
        Context context = getContext();
        if (context == null) return;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            // Android Q 이상: 권한 없이 다운로드 수행
            downloadImage();
        } else {
            // Android P 이하: WRITE_EXTERNAL_STORAGE 권한 확인
            boolean hasPermission = (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
            if (!hasPermission) {
                // 권한 요청
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
            } else {
                // 권한이 이미 있는 경우 다운로드 수행
                downloadImage();
            }
        }
    }

    /**
     * 권한 요청 결과 처리
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_WRITE_STORAGE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                downloadImage();
            } else {
                Toast.makeText(getContext(), "저장소 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 이미지를 다운로드하여 저장하는 메서드
     */
    /**
     * 이미지를 다운로드하여 저장하는 메서드
     */
    private void downloadImage() {
        if (generatedImageDetailView.getDrawable() == null) {
            Toast.makeText(getContext(), "다운로드할 이미지가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Bitmap 가져오기
        Bitmap bitmap = ((BitmapDrawable) generatedImageDetailView.getDrawable()).getBitmap();

        // 파일 이름 설정
        String fileName = "GeneratedImage_" + System.currentTimeMillis() + ".jpg";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android Q 이상: MediaStore를 사용하여 저장 (권한 없이 가능)
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/AiWizImages");

            Context context = getContext();
            if (context == null) {
                Toast.makeText(getContext(), "컨텍스트가 null입니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    OutputStream out = context.getContentResolver().openOutputStream(uri);
                    if (out != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                        Toast.makeText(context, "이미지가 다운로드되었습니다.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "이미지 출력 스트림을 열 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "이미지를 다운로드할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "다운로드 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                Log.e("DetailGeneratedImageDialog", "downloadImage 오류", e);
            }
        } else {
            // Android Q 미만: 외부 저장소에 직접 저장 (권한 필요)
            // 저장할 디렉토리 설정 (AiImageActivity와 동일한 경로 사용)
            String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            File myDir = new File(root + "/AiWizImages");
            if (!myDir.exists()) {
                boolean created = myDir.mkdirs();
                if (!created) {
                    Toast.makeText(getContext(), "저장소 디렉토리를 생성할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            File file = new File(myDir, fileName);

            // 파일 저장
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                Toast.makeText(getContext(), "이미지가 다운로드되었습니다: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "다운로드 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                Log.e("DetailGeneratedImageDialog", "downloadImage 오류", e);
            }
        }
    }
}