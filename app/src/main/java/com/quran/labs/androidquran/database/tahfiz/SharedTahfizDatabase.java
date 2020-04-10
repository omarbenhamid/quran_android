package com.quran.labs.androidquran.database.tahfiz;

import android.content.Context;

import com.quran.labs.androidquran.database.tahfiz.dao.ReviewRangeDAO;
import com.quran.labs.androidquran.database.tahfiz.dao.TalibDAO;
import com.quran.labs.androidquran.database.tahfiz.entities.ReviewRange;
import com.quran.labs.androidquran.database.tahfiz.entities.Talib;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Talib.class}, version=4)
public abstract class SharedTahfizDatabase extends RoomDatabase {
  private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
    @Override
    public void migrate(SupportSQLiteDatabase database) {
      database.execSQL("ALTER TABLE talib  ADD COLUMN hifzoServerKey TEXT");
      database.execSQL("ALTER TABLE talib  ADD COLUMN hifzoUrl TEXT");
    }
  };

  public abstract TalibDAO talibDAO();

  public static SharedTahfizDatabase getInstance(Context ctx) {

    return Room.databaseBuilder(ctx.getApplicationContext(),
        SharedTahfizDatabase.class, "tahfiz_shared")
        .addMigrations(MIGRATION_3_4)
        .fallbackToDestructiveMigration().build();
  }
}
