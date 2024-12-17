package com.example.aiwiz.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.aiwiz.R;
import com.example.aiwiz.entity.GeneratedImage;

import java.util.List;

public class GeneratedImageAdapter extends RecyclerView.Adapter<GeneratedImageAdapter.GeneratedImageViewHolder> {

    private Context context;
    private List<GeneratedImage> generatedImages;
    private OnItemClickListener listener;

    // 인터페이스 정의: 아이템 클릭 시 이벤트 처리
    public interface OnItemClickListener {
        void onItemClick(GeneratedImage generatedImage);
    }

    public GeneratedImageAdapter(Context context, List<GeneratedImage> generatedImages, OnItemClickListener listener) {
        this.context = context;
        this.generatedImages = generatedImages;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GeneratedImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_generated_image, parent, false);
        return new GeneratedImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GeneratedImageViewHolder holder, int position) {
        GeneratedImage generatedImage = generatedImages.get(position);
        holder.bind(generatedImage, listener);
    }

    @Override
    public int getItemCount() {
        return generatedImages.size();
    }

    // ViewHolder 클래스
    public static class GeneratedImageViewHolder extends RecyclerView.ViewHolder {
        ImageView generatedImageView;

        public GeneratedImageViewHolder(@NonNull View itemView) {
            super(itemView);
            generatedImageView = itemView.findViewById(R.id.generatedImageView);
        }

        public void bind(final GeneratedImage generatedImage, final OnItemClickListener listener) {
            // byte[]를 Bitmap으로 변환하여 ImageView에 설정
            byte[] imageData = generatedImage.getImageData();
            if (imageData != null && imageData.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                generatedImageView.setImageBitmap(bitmap);
            }else{
                // 이미지가 없을 경우 placeholder 설정
                generatedImageView.setImageResource(R.drawable.placeholder);
            }


            // 클릭 리스너 설정
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(generatedImage);
                }
            });
        }
    }

    // 데이터 업데이트 메서드
    @SuppressLint("NotifyDataSetChanged")
    public void setGeneratedImages(List<GeneratedImage> generatedImages) {
        this.generatedImages = generatedImages;
        notifyDataSetChanged();
    }
}
