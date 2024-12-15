// AppDatabase.java

package com.example.aiwiz;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {LikedPhoto.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract LikedPhotoDao likedPhotoDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "photo_database")
                            .allowMainThreadQueries() // UI 스레드에서 DB 작업 허용 (권장하지 않음)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
