package com.quran.labs.androidquran.ui.helpers;

import android.content.Context;
import android.database.Cursor;
import android.util.JsonReader;

import com.quran.data.page.provider.madani.MadaniPageProvider;
import com.quran.data.source.PageProvider;
import com.quran.labs.androidquran.R;
import com.quran.labs.androidquran.data.AyahInfoDatabaseHandler;
import com.quran.labs.androidquran.data.AyahInfoDatabaseProvider;
import com.quran.labs.androidquran.database.tahfiz.TahfizDatabase;
import com.quran.labs.androidquran.database.tahfiz.dao.TalibDAO;
import com.quran.labs.androidquran.database.tahfiz.entities.ReviewRange;
import com.quran.labs.androidquran.database.tahfiz.entities.Talib;
import com.quran.labs.androidquran.module.application.ApplicationModule;
import com.quran.labs.androidquran.util.QuranFileUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Credentials;

public class HifzoSyncAction implements Publisher<String> {

  //private static final String SYNC_URL="https://hifzo.com/track/api/rangesync";
  private static final String SYNC_URL="http://10.0.2.2:8000/track/api/rangesync";

  private final Context context;
  private final String authHeader;
  private TalibDAO dao;
  private AyahInfoDatabaseHandler ayahInfoDB;

  public HifzoSyncAction(Context context, TalibDAO talibDAO,
                         String login, String password) {
    this.authHeader = Credentials.basic(login,password);
    this.context = context;
    this.dao = talibDAO;
    this.ayahInfoDB = buildAyahInfoDBHandler();
  }
  @Override
  public void subscribe(Subscriber<? super String> progressMonitor) {
    try {
      progressMonitor.onNext(context.getString(R.string.hifzo_updating_talibs));
      downloadTalibList();
      Talib[] talibs = dao.listAllTalibs();
      String progressMsg = context.getString(R.string.hifzo_uploading_data);
      for(int i=0; i < talibs.length; i++) {
        progressMonitor.onNext(String.format(progressMsg, i+1, talibs.length));
        uploadTalib(talibs[i]);
      }
      progressMonitor.onComplete();
    }catch(Throwable ex){
      progressMonitor.onError(ex);
    }

  }

  private AyahInfoDatabaseHandler buildAyahInfoDBHandler() {
    PageProvider pp = new MadaniPageProvider();

    QuranFileUtils qfu = new QuranFileUtils(context, pp, null);
    String widthParam = pp.getPageSizeCalculator(
        ApplicationModule.provideDisplaySize(ApplicationModule.provideDisplay(context))
    ).getWidthParameter();

    AyahInfoDatabaseProvider aid = new AyahInfoDatabaseProvider(context, "_"+widthParam, qfu);
    return aid.getAyahInfoHandler();
  }

  private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");
  private void uploadTalib(Talib talib) throws Exception {
    if(ayahInfoDB == null) throw new Exception(context.getString(R.string.hifzo_ayahdb_notready));
    JSONArray reviewRanges = new JSONArray();
    TahfizDatabase db = TahfizDatabase.getInstance(context, talib.id);
    try {
      for(ReviewRange range: db.reviewRangeDAO().listUnsynced()) {
        JSONObject rr = new JSONObject();
        reviewRanges.put(rr);
        rr.put("date", DATEFORMAT.format(new Date(range.timestamp*1000)));
        rr.put("strength", range.count);
        rr.put("page",range.page);
        rr.put("line", range.line);
        rr.put("startX", range.firstX);
        rr.put("endX", range.lastX);
        Cursor c = ayahInfoDB.findIntersectingGlyphs(range.page, range.line, range.firstX, range.lastX);
        try {
          if(c.isAfterLast())
            continue;
          c.moveToNext();
          int sura = c.getInt(0);
          rr.put("sura", sura);
          rr.put("startAyah", c.getInt(1));
          rr.put("startWord", c.getInt(2));

          while(! c.isLast()) c.moveToNext();
          //The same line is always the same sura
          assert(sura == c.getInt(0));

          rr.put("endAyah", c.getInt(1));
          rr.put("endWord", c.getInt(2));

        }finally{
          c.close();
        }
      }

      if(reviewRanges.length() != 0) {
        JSONObject t = new JSONObject();
        t.put("serverKey",talib.hifzoServerKey);
        t.put("talibName", talib.name);
        t.put("reviewRanges", reviewRanges);
        JsonReader resp = request("POST", t);
        Map<String,String> r = readObject(resp);
        if(r.get("serverKey") != null &&
            !r.get("serverKey").equals(talib.hifzoServerKey)) {
          talib.hifzoServerKey = r.get("serverKey");
          talib.hifzoUrl = r.get("talibUrl");
          dao.update(talib);
        }
      }

      db.reviewRangeDAO().markAllSynced();
    }finally{
      db.close();
    }
  }



  private JsonReader request(String method, JSONObject body) throws Exception {
    HttpURLConnection conn = (HttpURLConnection) new URL(SYNC_URL).openConnection();
    conn.setRequestMethod(method);
    conn.setRequestProperty("Authorization", authHeader);

    if(body != null) {
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setDoOutput(true);
      OutputStream os = conn.getOutputStream();
      os.write(body.toString().getBytes(Charset.forName("UTF-8")));
      os.close();
    }

    if(conn.getResponseCode() == 401) {
      throw new Exception(context.getString(R.string.hifzo_bad_login));
    }
    if(conn.getResponseCode() != 200) {
      throw new Exception(context.getString(R.string.hifzo_svr_error));
    }
    return new JsonReader(new InputStreamReader(conn.getInputStream()));
  }

  private void downloadTalibList() throws Exception {
    JsonReader r = request("GET", null);
    r.beginArray();
    while(r.hasNext()) {
      Map<String,String> o = readObject(r);
      String sk = o.get("serverKey");
      if(sk == null) throw new Exception(context.getString(R.string.hifzo_svr_error));

      String name = o.get("talibName");
      if(name == null) name=sk;
      String url = o.get("talibUrl");
      if(url == null) url= "https://hifzo.com/";

      Talib t = dao.findByServerKey(sk);

      if(t == null) {
        t = new Talib(name);
        t.hifzoUrl = url;
        t.hifzoServerKey = sk;
        dao.addTalib(t);
      }else{
        if(! name.equals(t.name) ||
            ! url.equals(t.hifzoUrl)) {
          t.name = name;
          t.hifzoUrl = url;
          dao.update(t);
        }
      }
    }
    r.endArray();
    r.close();
  }

  private Map<String, String> readObject(JsonReader r) throws Exception{
    Map<String,String> ret = new HashMap<>();
    r.beginObject();
    while(r.hasNext())
      ret.put(r.nextName(), r.nextString());
    r.endObject();
    return ret;
  }


}
