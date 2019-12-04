package com.quran.labs.androidquran.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.NonRestoringViewPager;

public class NonSwipingViewPager extends NonRestoringViewPager {

  public NonSwipingViewPager(Context context) {
    super(context);
  }

  public NonSwipingViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    return false;
  }
}
