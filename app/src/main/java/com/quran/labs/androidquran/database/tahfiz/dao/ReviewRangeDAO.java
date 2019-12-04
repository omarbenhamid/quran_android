package com.quran.labs.androidquran.database.tahfiz.dao;

import com.quran.labs.androidquran.database.tahfiz.entities.ReviewRange;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Completable;

@Dao
public interface ReviewRangeDAO {
  @Insert
  public void addReviewRange(ReviewRange ... r);

  @Query("SELECT * FROM review_range WHERE page=:pageNum")
  List<ReviewRange> loadAllByPage(int pageNum);

  @Query("UPDATE review_range SET count=count-1 WHERE page=:pageNum AND line=:line AND firstX < :lastX AND lastX > :firstX")
  int decreaseIntersecting(int pageNum, int line, float firstX, float lastX);

  @Query("DELETE FROM review_range WHERE count <= 0")
  void deleteNullCount();

  @Query("SELECT * FROM review_range WHERE page=:pageNum AND line=:line AND firstX < :lastX AND lastX > :firstX")
  List<ReviewRange> findIntersecting(int pageNum, int line, float firstX, float lastX);

  @Update
  void update(ReviewRange updated);

  @Query("DELETE FROM review_range WHERE id=:id")
  void deleteById(int id);
}
