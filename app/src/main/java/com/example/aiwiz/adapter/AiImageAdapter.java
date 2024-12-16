package com.example.aiwiz.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aiwiz.entity.LikedPhoto;
import com.example.aiwiz.R;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AiImageAdapter extends RecyclerView.Adapter<AiImageAdapter.AiImageViewHolder> {
    private Context context;
    private List<LikedPhoto> photos;
    private Set<Integer> selectedPositions;

    public AiImageAdapter(Context context) {
        this.context = context;
        this.photos = new ArrayList<>();
        this.selectedPositions = new HashSet<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setPhotos(List<LikedPhoto> likedPhotos) {
        if (likedPhotos != null) {
            this.photos = likedPhotos;
        } else {
            this.photos = new ArrayList<>();
        }
        selectedPositions.clear(); // 데이터가 변경되면 선택 상태 초기화
        notifyDataSetChanged();
    }

    public List<LikedPhoto> getSelectedPhotos() {
        List<LikedPhoto> selectedPhotos = new ArrayList<>();
        for (Integer position : selectedPositions) {
            if (position >= 0 && position < photos.size()) {
                selectedPhotos.add(photos.get(position));
            }
        }
        return selectedPhotos;
    }

    @NonNull
    @Override
    public AiImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ai_image, parent, false);
        return new AiImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AiImageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        LikedPhoto likedPhoto = photos.get(position);
        Glide.with(context)
                .load(likedPhoto.getPhotoUrl())
                .into(holder.photoImageView);

        // 선택 상태에 따른 체크 표시 보이기/숨기기
        if (selectedPositions.contains(position)) {
            holder.checkMark.setVisibility(View.VISIBLE);
        } else {
            holder.checkMark.setVisibility(View.GONE);
        }

        // 클릭 시 선택 토글
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedPositions.contains(position)) {
                    selectedPositions.remove(position);
                    holder.checkMark.setVisibility(View.GONE);
                } else {
                    selectedPositions.add(position);
                    holder.checkMark.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (photos != null) ? photos.size() : 0;
    }

    public static class AiImageViewHolder extends RecyclerView.ViewHolder {
        ImageView photoImageView;
        ImageView checkMark;

        public AiImageViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
            checkMark = itemView.findViewById(R.id.checkMarkImageView);
        }
    }
}
