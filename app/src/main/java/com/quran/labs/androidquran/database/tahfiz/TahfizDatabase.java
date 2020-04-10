package com.quran.labs.androidquran.database.tahfiz;

import android.content.Context;

import com.quran.labs.androidquran.database.DatabaseUtils;
import com.quran.labs.androidquran.database.tahfiz.dao.ReviewRangeDAO;
import com.quran.labs.androidquran.database.tahfiz.entities.ReviewRange;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {ReviewRange.class}, version=5)
public abstract class TahfizDatabase extends RoomDatabase {
  private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
    @Override
    public void migrate(SupportSQLiteDatabase database) {
      database.execSQL("ALTER TABLE review_range  ADD COLUMN synced INTEGER NOT NULL DEFAULT 0");
    }
  };
  private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
    @Override
    public void migrate(SupportSQLiteDatabase database) {
      database.execSQL("ALTER TABLE review_range  ADD COLUMN timestamp INTEGER NOT NULL DEFAULT 1586537663");
    }
  };
  public abstract ReviewRangeDAO reviewRangeDAO();

  private static TahfizDatabase getInstance(Context ctx, String dbName) {
    return Room.databaseBuilder(ctx.getApplicationContext(),
        TahfizDatabase.class, dbName)
        .addMigrations(MIGRATION_3_4, MIGRATION_4_5)
        .fallbackToDestructiveMigration().build();
  }

  public static TahfizDatabase getInstance(Context ctx) {
    return getInstance(ctx, DatabaseUtils.getTalibDBName(ctx,"tahfiz"));
  }

  public static TahfizDatabase getInstance(Context ctx, int talibId) {
    return getInstance(ctx, DatabaseUtils.getTalibDBName(talibId,"tahfiz"));
  }
}
