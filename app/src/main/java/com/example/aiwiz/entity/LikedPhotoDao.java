// LikedPhotoDao.java

package com.example.aiwiz.entity;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface LikedPhotoDao {

    @Insert
    void insert(LikedPhoto likedPhoto);

    @Delete
    void delete(LikedPhoto likedPhoto);

    @Query("SELECT * FROM liked_photos WHERE photoId = :photoId LIMIT 1")
    LikedPhoto getLikedPhotoById(String photoId);

    @Query("SELECT * FROM liked_photos")
    List<LikedPhoto> getAllLikedPhotos();

    // photoId가 NULL인 레코드 삭제
    @Query("DELETE FROM liked_photos WHERE photoId IS NULL")
    void deleteNullPhotoIds();
}
