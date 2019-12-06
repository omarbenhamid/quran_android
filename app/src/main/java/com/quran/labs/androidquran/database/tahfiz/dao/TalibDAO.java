package com.quran.labs.androidquran.database.tahfiz.dao;

import com.quran.labs.androidquran.database.tahfiz.entities.Talib;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Completable;

@Dao
public interface TalibDAO {
  @Query("SELECT * FROM talib ORDER BY name ASC")
  Talib[] listAllTalibs();

  @Insert
  void addTalib(Talib talib);

  @Query("UPDATE talib SET lastPage = :lastPage WHERE id=:talibId")
  void updateLastPage(Integer talibId, int lastPage);
}
