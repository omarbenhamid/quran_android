package com.quran.labs.androidquran.database.tahfiz.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "talib")
public class Talib {
  @PrimaryKey(autoGenerate =  true)
  public int id;

  @ColumnInfo
  public String name;

  @ColumnInfo
  public String hifzoServerKey;

  @ColumnInfo
  public String hifzoUrl;

  public Talib() {

  }

  public Talib(String name) {
    this.name = name;
  }
}
