package com.quran.labs.androidquran.ui.util;

import android.view.MotionEvent;

import com.quran.labs.androidquran.ui.helpers.AyahSelectedListener;

public interface PageController {
  boolean handleTouchEvent(MotionEvent event, AyahSelectedListener.EventType eventType, int page);
  void handleRetryClicked();
  void onScrollChanged(int x, int y, int oldx, int oldy);
  void handleFingerMotionStart(float x, float y);
  void handleFingerMotionUpdate(float x0, float y0, float x, float y);
  void handleFingerMotionEnd(float x0, float y0, float x, float y);
}
