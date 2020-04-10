package com.quran.labs.androidquran.database.tahfiz.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "review_range")
public class ReviewRange {
  public static final int MAX_COUNT=3;

  @PrimaryKey(autoGenerate = true)
  public int id;

  @ColumnInfo(name="page")
  public int page;

  @ColumnInfo(name="line")
  public int line;

  @ColumnInfo(name="firstX")
  public float firstX;

  @ColumnInfo(name="lastX")
  public float lastX;

  @ColumnInfo(name="count")
  public float count = 1;

  @ColumnInfo
  public boolean synced;

  /** Seconds since epoch */
  @ColumnInfo
  public long timestamp;

  public ReviewRange(int page, int line, float firstX, float lastX) {
    this.page = page;
    this.line = line;
    this.firstX = firstX;
    this.lastX = lastX;
    this.synced = false;
    this.timestamp = currentTimestamp();
  }

  public ReviewRange() {

  }

  public static long currentTimestamp() {
    return new Date().getTime()/1000;
  }

  public boolean contains(float fx, float lx) {
    return firstX <= fx && lastX >= lx;
  }

  public void inc() {
    if(count < MAX_COUNT) count++;
  }

  /**
   * not  contains nor containedin or behaviour iss "unexpected"
   * @param fx
   * @param lx
   */
  public void cutWith(float fx, float lx) {
    if(firstX <= fx) lastX = fx;
    if(lastX >= lx) firstX = lx;
  }

  public void engulf(ReviewRange r) {
    if(firstX > r.firstX) firstX = r.firstX;
    if(lastX < r.lastX) lastX= r.lastX;
    if(count < r.count) count = r.count;
  }

  public void setUnsynced() {
    this.synced=false;
    this.timestamp = currentTimestamp();
  }
}
