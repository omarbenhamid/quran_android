package com.quran.labs.androidquran.ui.helpers;

import android.content.Context;
import android.util.JsonReader;

import com.quran.labs.androidquran.R;
import com.quran.labs.androidquran.database.tahfiz.dao.TalibDAO;
import com.quran.labs.androidquran.database.tahfiz.entities.Talib;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.Action;
import okhttp3.Credentials;

public class HifzoSyncAction implements Action {
  //private static final String SYNC_URL="https://hifzo.com/track/api/rangesync";
  private static final String SYNC_URL="http://10.0.2.2:8000/track/api/rangesync";

  private final Context context;
  private String login;
  private String password;
  private TalibDAO dao;

  public HifzoSyncAction(Context context, TalibDAO talibDAO, String login, String password) {
    this.login = login;
    this.password = password;
    this.context = context;
    this.dao = talibDAO;
  }
  @Override
  public void run() throws Exception {
    String authHeader = Credentials.basic(login,password);  //Java 8
    HttpURLConnection conn = (HttpURLConnection) new URL(SYNC_URL).openConnection();
    conn.setRequestProperty("Authorization", authHeader);
    if(conn.getResponseCode() == 401) throw new Exception(context.getString(R.string.hifzo_bad_login));
    if(conn.getResponseCode() != 200) throw new Exception(context.getString(R.string.hifzo_svr_error));
    JsonReader r = new JsonReader(new InputStreamReader(conn.getInputStream()));
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
