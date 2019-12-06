package com.quran.labs.androidquran.database.tahfiz;

import android.content.Context;

import com.quran.labs.androidquran.database.tahfiz.dao.ReviewRangeDAO;
import com.quran.labs.androidquran.database.tahfiz.dao.TalibDAO;
import com.quran.labs.androidquran.database.tahfiz.entities.ReviewRange;
import com.quran.labs.androidquran.database.tahfiz.entities.Talib;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Talib.class}, version=3)
public abstract class SharedTahfizDatabase extends RoomDatabase {
  public abstract TalibDAO talibDAO();

  public static SharedTahfizDatabase getInstance(Context ctx) {
    return Room.databaseBuilder(ctx.getApplicationContext(),
        SharedTahfizDatabase.class, "tahfiz_shared")
        .fallbackToDestructiveMigration().build();
  }
}
