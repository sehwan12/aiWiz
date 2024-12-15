package com.example.aiwiz.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aiwiz.LikedPhoto;
import com.example.aiwiz.R;
import com.example.aiwiz.activity.DetailActivity;

import java.util.ArrayList;
import java.util.List;

public class LikedPhotoAdapter extends RecyclerView.Adapter<LikedPhotoAdapter.LikedPhotoViewHolder> {
    private Context context;
    private List<LikedPhoto> likedPhotos;

    public LikedPhotoAdapter(Context context) {
        this.context = context;
        this.likedPhotos = new ArrayList<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setLikedPhotos(List<LikedPhoto> likedPhotos) {
        if (likedPhotos != null) {
            this.likedPhotos = likedPhotos;
        } else {
            this.likedPhotos = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LikedPhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.photo_item, parent, false);
        return new LikedPhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LikedPhotoViewHolder holder, int position) {
        LikedPhoto likedPhoto = likedPhotos.get(position);
        Glide.with(context)
                .load(likedPhoto.getPhotoUrl())
                .into(holder.photoImageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("PHOTO_URL", likedPhoto.getPhotoUrl());
            intent.putExtra("PHOTO_DESCRIPTION", likedPhoto.getDescription());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return (likedPhotos != null) ? likedPhotos.size() : 0;
    }

    public static class LikedPhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView photoImageView;

        public LikedPhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
        }
    }
}
