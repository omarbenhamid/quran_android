package com.quran.labs.androidquran.data;

public class QuranData {

  public static int[] SURA_NUM_AYAHS = BaseQuranData.SURA_NUM_AYAHS;
  public static boolean[] SURA_IS_MAKKI = BaseQuranData.SURA_IS_MAKKI;
  public static int[][] QUARTERS = BaseQuranData.QUARTERS;

  public static int[] SURA_PAGE_START = {
      -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,//20
      -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,1 //49
      ,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1 //Surah 0 = Aj Hujurat
  };

  public static int[] PAGE_SURA_START = {
      49, 49, 49, 49
  };

  public static int[] PAGE_AYAH_START = {
      1, 2, 4, 7
  };


  public static int[] JUZ_PAGE_START = {
      1
  };

  public static int[] PAGE_RUB3_START = {
      -1, -1, -1, -1 //=> First is beginnign of rub3
  };
}
