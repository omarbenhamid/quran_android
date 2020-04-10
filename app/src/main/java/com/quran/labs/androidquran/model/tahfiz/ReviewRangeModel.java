package com.quran.labs.androidquran.model.tahfiz;

import android.content.Context;

import com.quran.labs.androidquran.database.tahfiz.TahfizDatabase;
import com.quran.labs.androidquran.database.tahfiz.dao.ReviewRangeDAO;
import com.quran.labs.androidquran.database.tahfiz.entities.ReviewRange;
import com.quran.labs.androidquran.di.ActivityScope;
import com.quran.page.common.data.FingerMotionRange;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

@ActivityScope
public class ReviewRangeModel {
  private TahfizDatabase db;

  @Inject
  public ReviewRangeModel(Context context) {
    db = TahfizDatabase.getInstance(context);
  }

  public TahfizDatabase getDatabase() {
    return db;
  }

  public Completable updateDatabaseWithFingerMotion(int pageNumber, FingerMotionRange range) {
    return Completable.fromRunnable( () -> {
      ReviewRangeDAO dao = db.reviewRangeDAO();
      if(range.isDeleting()) {
        dao.decreaseIntersecting(pageNumber, range.getLine(),
            range.getFirstX(), range.getLastX(), ReviewRange.currentTimestamp());
        dao.deleteNullCount();
        return;
      }

      ReviewRange newRange = new ReviewRange(pageNumber, range.getLine(),
          range.getFirstX(), range.getLastX());
      newRange.count = 0;

      for(ReviewRange r : dao.findIntersecting(pageNumber, range.getLine(),
          range.getFirstX(), range.getLastX())) {
        if(r.contains(range.getFirstX(), range.getLastX())) {
          r.inc();
          r.setUnsynced();
          dao.update(r);
          return;
        }

        newRange.engulf(r);
        dao.deleteById(r.id);
      }
      newRange.inc(); //0 if ingulfed no body, if ingulfed something => 2
      dao.addReviewRange(newRange);
    }).subscribeOn(Schedulers.io());
  }
}
