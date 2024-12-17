package com.example.aiwiz.adapter;

// PhotoAdapter.java

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aiwiz.api.Photo;
import com.example.aiwiz.R;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private Context mContext;
    private List<Photo> photoList;
    private OnItemClickListener listener;


    public interface OnItemClickListener {
        void onItemClick(Photo position);
    }

    public PhotoAdapter(Context context, List<Photo> photos, OnItemClickListener listener) {
        this.mContext = context;
        this.photoList = photos;
        this.listener = listener;
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        public ImageView photoImageView;
        public TextView photographerName;

        public PhotoViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
            photographerName = itemView.findViewById(R.id.photographerName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            Photo clickedPhoto = photoList.get(position);
                            if (clickedPhoto != null) {
                                listener.onItemClick(clickedPhoto);
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.photo_item, parent, false);
        return new PhotoViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        Photo currentPhoto = photoList.get(position);
        Glide.with(mContext)
                .load(currentPhoto.getUrls().getSmall())
                //.placeholder(R.drawable.placeholder) // Placeholder 이미지
                .into(holder.photoImageView);
        holder.photographerName.setText(currentPhoto.getUser().getName());
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setPhotos(List<Photo> photos) {
        this.photoList = photos;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addPhotos(List<Photo> morePhotos) {
        this.photoList.addAll(morePhotos);
        notifyDataSetChanged();
    }
}
