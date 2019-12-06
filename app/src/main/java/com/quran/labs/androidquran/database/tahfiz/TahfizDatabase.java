package com.quran.labs.androidquran.database.tahfiz;

import android.content.Context;

import com.quran.labs.androidquran.database.tahfiz.dao.ReviewRangeDAO;
import com.quran.labs.androidquran.database.tahfiz.entities.ReviewRange;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ReviewRange.class}, version=3)
public abstract class TahfizDatabase extends RoomDatabase {
  public abstract ReviewRangeDAO reviewRangeDAO();

  public static TahfizDatabase getInstance(Context ctx) {
    return Room.databaseBuilder(ctx.getApplicationContext(),
        TahfizDatabase.class, MultiTalibSQLiteOpenHelper.getDBName(ctx,"tahfiz"))
        .fallbackToDestructiveMigration().build();
  }
}
