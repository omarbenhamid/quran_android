package com.quran.labs.androidquran.database.tahfiz.dao;

import com.quran.labs.androidquran.database.tahfiz.entities.Talib;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Completable;

@Dao
public interface TalibDAO {
  @Query("SELECT * FROM talib ORDER BY name ASC")
  Talib[] listAllTalibs();

  @Insert
  void addTalib(Talib talib);

  @Update
  void update(Talib element);

  @Query("DELETE FROM talib WHERE id=:talibId")
  void delete(int talibId);

  @Query("SELECT * FROM talib WHERE id=:talibId")
  Talib findById(int talibId);

}
