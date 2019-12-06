package com.quran.labs.androidquran.database;

import android.content.Context;
import android.database.Cursor;

import com.quran.labs.androidquran.util.QuranSettings;

public class DatabaseUtils {

  public static String getTalibDBName(Context context, String prefix) {
    Integer talibId = QuranSettings.getInstance(context).getLastTalibId();
    return talibId == null ? prefix : prefix + "-talib-" + talibId;
  }

  public static void closeCursor(Cursor cursor) {
    if (cursor != null) {
      try {
        cursor.close();
      } catch (Exception e) {
        // no op
      }
    }
  }
}
