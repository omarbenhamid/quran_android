package com.quran.labs.androidquran.widgets;

import android.content.Context;
import androidx.annotation.NonNull;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

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
    private boolean canceled;


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
          x0 = x;
          y0 = y;
          moving = false;
          canceled = false;
          break;
        case MotionEvent.ACTION_MOVE:
          if(canceled) break;
          if(! moving) {
            //Here notify pageController to somehow draw a "temporary" line on image view
            //Idea show above the simbol a eraser icon when moving from left to right and stylo plume
            //When moving from right to left.
            int dx = (int)(x - x0);
            int dy = (int)(y - y0);
            int dstSq = dx * dx + dy * dy;
            if(dstSq <= touchSlopSquare) break;
            moving = true;
            pageController.handleFingerMotionStart(x0, y0);
          } else {
            int dx = (int)(x - x0);
            dx = dx * dx;

            int dy = (int)(y - y0);
            dy = dy * dy;

            if(dy > (dx + touchSlopSquare * 4)) {
              pageController.handleFingerMotionCancel();
              canceled = true;
              break;
            }
          }
          //Here we are moving
          pageController.handleFingerMotionUpdate(x0, y0, x, y);

          break;
        case MotionEvent.ACTION_UP:
          if(canceled) break;
          if(moving) {
            //Here notify the pageController to do the precise position using glyphdb, and save it.
            pageController.handleFingerMotionEnd(x0, y0, x, y);
          }
          break;
      }

      return super.onTouchEvent(event);
    }
  }
}
