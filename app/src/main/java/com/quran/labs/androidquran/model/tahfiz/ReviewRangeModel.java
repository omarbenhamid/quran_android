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


  public Completable updateDatabaseWithFingerMothion(int pageNumber, FingerMotionRange range) {
    return Completable.fromRunnable( () -> {
      ReviewRangeDAO dao = db.reviewRangeDAO();
      if(range.isDeleting()) {
        dao.decreaseIntersecting(pageNumber, range.getLine(),
            range.getFirstX(), range.getLastX());
        dao.deleteNullCount();
        return;
      }

      for(ReviewRange r : dao.findIntersecting(pageNumber, range.getLine(),
          range.getFirstX(), range.getLastX())) {
        if(r.contains(range.getFirstX(), range.getLastX())) {
          r.inc();
          dao.update(r);
          continue;
        }

        if(r.containedIn(range.getFirstX(), range.getLastX())){
          dao.deleteById(r.id);
        }

        r.cutWith(range.getFirstX(), range.getLastX());
        dao.update(r);
      }

      dao.addReviewRange(new ReviewRange(pageNumber, range.getLine(), range.getFirstX(), range.getLastX()));
    }).subscribeOn(Schedulers.io());
  }
}
