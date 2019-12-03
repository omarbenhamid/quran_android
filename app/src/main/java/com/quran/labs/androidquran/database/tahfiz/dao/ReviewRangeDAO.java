package com.quran.labs.androidquran.database.tahfiz.dao;

import com.quran.labs.androidquran.database.tahfiz.entities.ReviewRange;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface ReviewRangeDAO {
  @Insert
  public void addReviewRange(ReviewRange ... r);

  @Query("SELECT * FROM review_range WHERE page=:pageNum")
  List<ReviewRange> loadAllByPage(int pageNum);
}
