package com.quran.labs.androidquran.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.viewpager.widget.NonRestoringViewPager;
import androidx.viewpager.widget.ViewPager;

public class VerticalViewPager extends NonRestoringViewPager {

  public VerticalViewPager(Context context) {
    super(context);
    init();
  }

  public VerticalViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  private void init() {
    setPageTransformer(true, new VerticalPageTransformer());
    setOverScrollMode(OVER_SCROLL_NEVER);
  }

  private class VerticalPageTransformer implements ViewPager.PageTransformer {
    @Override
    public void transformPage(View view, float position) {

      if (position < -1) { // [-Infinity,-1)
        // This page is way off-screen to the left.
        view.setAlpha(0);

      } else if (position <= 1) { // [-1,1]
        view.setAlpha(1);

        // Counteract the default slide transition
        view.setTranslationX(view.getWidth() * -position);

        //set Y position to swipe in from top
        float yPosition = - position * view.getHeight();
        view.setTranslationY(yPosition);

      } else { // (1,+Infinity]
        // This page is way off-screen to the right.
        view.setAlpha(0);
      }

    }
  }

  /**
   * Swaps the X and Y coordinates of your touch event.
   */
  void swapXY(MotionEvent ev) {
    float width = getWidth();
    float height = getHeight();

    float newX = width - (ev.getY() / height) * width;
    float newY = (ev.getX() / width) * height;

    ev.setLocation(newX, newY);

  }

  void unswapXY(MotionEvent ev) {
    float width = getWidth();
    float height = getHeight();

    float newX = (ev.getY() / height) * width;
    float newY = height - (ev.getX() / width) * height;

    ev.setLocation(newX, newY);

  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev){
    swapXY(ev);
    boolean ret = super.onInterceptTouchEvent(ev);
    unswapXY(ev);
    return ret;
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    swapXY(ev);
    boolean ret = super.onTouchEvent(ev);
    unswapXY(ev);
    return ret;
  }
}
