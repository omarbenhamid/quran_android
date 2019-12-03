package com.quran.labs.androidquran.widgets;

import android.content.Context;
import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Toast;

import com.quran.labs.androidquran.database.tahfiz.TahfizDatabase;
import com.quran.labs.androidquran.database.tahfiz.dao.ReviewRangeDAO;
import com.quran.labs.androidquran.ui.helpers.AyahSelectedListener;
import com.quran.labs.androidquran.ui.util.PageController;
import com.quran.labs.androidquran.util.QuranSettings;

public class QuranImagePageLayout extends QuranPageLayout {
  private HighlightingImageView imageView;

  public QuranImagePageLayout(Context context) {
    super(context);
  }

  @Override
  protected View generateContentView(Context context, boolean isLandscape) {
    imageView = new HighlightingImageView(context);
    imageView.setAdjustViewBounds(true);
    imageView.setIsScrollable(isLandscape && shouldWrapWithScrollView());
    return imageView;
  }

  @Override
  public void updateView(@NonNull QuranSettings quranSettings) {
    super.updateView(quranSettings);
    imageView.setNightMode(quranSettings.isNightMode(), quranSettings.getNightModeTextBrightness());
  }

  public HighlightingImageView getImageView() {
    return imageView;
  }

  @Override
  public void setPageController(PageController controller, int pageNumber) {
    super.setPageController(controller, pageNumber);
    final GestureDetector gestureDetector = new GestureAndMoveDetector();
    OnTouchListener gestureListener = (v, event) -> gestureDetector.onTouchEvent(event);
    imageView.setOnTouchListener(gestureListener);
    imageView.setClickable(true);
    imageView.setLongClickable(true);
  }

  private class PageGestureDetector extends GestureDetector.SimpleOnGestureListener {

    @Override
    public boolean onDown(MotionEvent e) {
      return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
      return pageController.handleTouchEvent(event,
          AyahSelectedListener.EventType.SINGLE_TAP, pageNumber);
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
      return pageController.handleTouchEvent(event,
          AyahSelectedListener.EventType.DOUBLE_TAP, pageNumber);
    }

    @Override
    public void onLongPress(MotionEvent event) {
      pageController.handleTouchEvent(event,
          AyahSelectedListener.EventType.LONG_PRESS, pageNumber);
    }
  }

  private class GestureAndMoveDetector extends GestureDetector {
    private int touchSlopSquare;
    private float x0, y0;
    private boolean moving;


    public GestureAndMoveDetector() {
      super(QuranImagePageLayout.this.context, new PageGestureDetector());
      int t = ViewConfiguration.get(context).getScaledTouchSlop();
      touchSlopSquare = t*t;
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
      float x = event.getX();
      float y = event.getY();

      switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
          Log.d("QPL","Down " +x +","+ y);
          x0 = x;
          y0 = y;
          moving = false;
          break;
        case MotionEvent.ACTION_MOVE:
          if(! moving) {
            //Here notify pageController to somehow draw a "temporary" line on image view
            //Idea show above the simbol a eraser icon when moving from left to right and stylo plume
            //When moving from right to left.
            int dx = (int)(x - x0);
            int dy = (int)(y - y0);
            int dstSq = dx * dx + dy * dy;
            if(dstSq <= touchSlopSquare) break;
            moving = true;
          }
          //Here we are moving
          //pageController.handleFingerMotion(x0, y0, x, y);
          Log.d("QPL","Moving to" + x +","+ y);

          break;
        case MotionEvent.ACTION_UP:
          if(moving) {
            Log.d("QPL", "Up " + x + "," + y);
            //Here notify the pageController to do the precise position using glyphdb, and save it.
            //pageController.handleFingerMotionFinish(x0, y0, x, y);
          }
          break;
      }

      return super.onTouchEvent(event);
    }
  }
}
