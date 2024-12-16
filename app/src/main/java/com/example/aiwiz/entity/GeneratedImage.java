package com.example.aiwiz.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.TypeConverters;


import java.util.Date;

@Entity(tableName = "generated_images")
public class GeneratedImage {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "image_data")
    private byte[] imageData;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    // Constructor
    public GeneratedImage(byte[] imageData, String description, Date createdAt) {
        this.imageData = imageData;
        this.description = description;
        this.createdAt = createdAt;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) { // Room은 Setter를 통해 값을 설정
        this.id = id;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
