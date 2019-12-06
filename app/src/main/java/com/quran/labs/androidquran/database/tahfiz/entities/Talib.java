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

  public Talib() {

  }

  public Talib(String name) {
    this.name = name;
  }
}
