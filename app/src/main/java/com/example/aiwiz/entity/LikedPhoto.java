// LikedPhoto.java

package com.example.aiwiz.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "liked_photos")
public class LikedPhoto {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String photoId; // Unsplash Photo ID
    private String photoUrl; // 사진의 URL
    private String description; // 사진 설명

    // Constructor
    public LikedPhoto(String photoId, String photoUrl, String description) {
        this.photoId = photoId;
        this.photoUrl = photoUrl;
        this.description = description;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) { // Room requires setter for auto-generated ID
        this.id = id;
    }

    public String getPhotoId() {
        return photoId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getDescription() {
        return description;
    }
}
