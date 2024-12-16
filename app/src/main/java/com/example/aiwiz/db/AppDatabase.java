// AppDatabase.java

package com.example.aiwiz.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;

import com.example.aiwiz.entity.GeneratedImage;
import com.example.aiwiz.entity.GeneratedImageDao;
import com.example.aiwiz.entity.LikedPhoto;
import com.example.aiwiz.entity.LikedPhotoDao;

@Database(entities = {LikedPhoto.class, GeneratedImage.class}, version = 2, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract LikedPhotoDao likedPhotoDao();
    public abstract GeneratedImageDao generatedImageDao();

    private static volatile AppDatabase INSTANCE;
    // 마이그레이션 객체 정의
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // 새로운 테이블 생성
            database.execSQL("CREATE TABLE IF NOT EXISTS `generated_images` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `image_data` BLOB, `description` TEXT, `created_at` INTEGER)");
        }
    };


    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "photo_database")
                            .addMigrations(MIGRATION_1_2)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
