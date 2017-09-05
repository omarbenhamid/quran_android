package com.quran.labs.androidquran.data;

import com.quran.labs.androidquran.util.NaskhPageProvider;
import com.quran.labs.androidquran.util.QuranScreenInfo;

import android.support.annotation.NonNull;
import android.view.Display;

public class QuranConstants {
  public static final int NUMBER_OF_PAGES = 4;
  public static final int JUZ2_COUNT = 1; //30 for full quran
  public static final int SURA_FIRST = 1;
  public static final int SURA_LAST = 1; //114 normally

  public static QuranScreenInfo.PageProvider getPageProvider(@NonNull Display display) {
    return new NaskhPageProvider(display);
  }
}
