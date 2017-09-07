package com.quran.labs.androidquran.data;

import com.quran.labs.androidquran.util.NaskhPageProvider;
import com.quran.labs.androidquran.util.QuranScreenInfo;

import android.support.annotation.NonNull;
import android.view.Display;

public class QuranConstants {
  public static final int NUMBER_OF_PAGES = 4;
  public static final int JUZ2_COUNT = 1; //30 for full quran
  public static final int SURA_FIRST = 49;
  public static final int SURA_LAST = 49; //114 normally

  //MP3 Source : http://download.quranicaudio.com/quran/muhammad_siddeeq_al-minshaawee/049.mp3
  //Or ayah by ayah : 'http://mirrors.quranicaudio.com/everyayah/Husary_128kbps_Mujawwad/049001.mp3')
  public static QuranScreenInfo.PageProvider getPageProvider(@NonNull Display display) {
    //return new NaskhPageProvider(display);
    return new QuranScreenInfo.PageProvider() {
      @Override
      public String getWidthParameter() {
        return "1152";
      }

      @Override
      public String getTabletWidthParameter() {
        return "1152";
      }

      @Override
      public void setOverrideParameter(String parameter) {
        //Not needed as in naskh !?
      }
    };
  }
}
