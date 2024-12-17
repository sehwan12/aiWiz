package com.example.aiwiz.entity;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface GeneratedImageDao {

    @Insert
    long insert(GeneratedImage generatedImage);

    @Query("SELECT * FROM generated_images ORDER BY created_at DESC")
    List<GeneratedImage> getAllGeneratedImages();

    @Query("SELECT * FROM generated_images WHERE id = :id LIMIT 1")
    GeneratedImage getGeneratedImageById(int id);

    @Delete
    void delete(GeneratedImage generatedImage);
}
