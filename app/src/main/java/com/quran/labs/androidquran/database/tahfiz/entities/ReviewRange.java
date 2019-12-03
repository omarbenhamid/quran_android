package com.quran.labs.androidquran.database.tahfiz.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "review_range")
public class ReviewRange {
  @PrimaryKey
  public int id;

  @ColumnInfo(name="sura")
  public int sura;

  @ColumnInfo(name="ayah")
  public int ayah;

  @ColumnInfo(name="page")
  public int page;

  @ColumnInfo(name="start_pos")
  public int startPos;

  @ColumnInfo(name="end_pos")
  public int endPos;
}
