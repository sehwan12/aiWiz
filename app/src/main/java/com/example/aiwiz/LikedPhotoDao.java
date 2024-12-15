// LikedPhotoDao.java

package com.example.aiwiz;

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
}
