package com.quran.labs.androidquran;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.quran.labs.androidquran.database.BookmarksDBAdapter;
import com.quran.labs.androidquran.database.tahfiz.SharedTahfizDatabase;
import com.quran.labs.androidquran.database.tahfiz.dao.TalibDAO;
import com.quran.labs.androidquran.database.tahfiz.entities.Talib;
import com.quran.labs.androidquran.ui.QuranActivity;
import com.quran.labs.androidquran.ui.helpers.TalibListAdapter;
import com.quran.labs.androidquran.util.QuranSettings;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


public class QuranSelectTalibActivity extends Activity {
  private RecyclerView mRecyclerView;
  private TalibDAO talibDAO;
  private CompositeDisposable disposables = new CompositeDisposable();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_quran_select_talib);

    mRecyclerView = findViewById(R.id.recycler_view);
    mRecyclerView.setHasFixedSize(true);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    mRecyclerView.setItemAnimator(new DefaultItemAnimator());

    final TalibListAdapter adapter =
        new TalibListAdapter(this, mRecyclerView);
    mRecyclerView.setAdapter(adapter);

    talibDAO = SharedTahfizDatabase.getInstance(this).talibDAO();

    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener( (View view) -> {
      final EditText talibName = new EditText(this);
      new AlertDialog.Builder(this)
          .setTitle(R.string.dialog_add_talib)
          .setView(talibName)
          .setPositiveButton(R.string.add_talib, (DialogInterface dialog, int which) -> {
            disposables.add(Completable
                .fromRunnable( () -> talibDAO.addTalib(new Talib(talibName.getText().toString())))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adapter::reload));

          }).show();
    });
  }

  @Override
  protected void onDestroy() {
    disposables.clear();
    super.onDestroy();
  }

  protected void runListView() {
    Intent i = new Intent(this, QuranActivity.class);
    i.putExtra(QuranActivity.EXTRA_SHOW_TRANSLATION_UPGRADE,
        getIntent().getBooleanExtra(QuranActivity.EXTRA_SHOW_TRANSLATION_UPGRADE, false));
    startActivity(i);
  }

  public void selectTalib(Talib t) {
    QuranSettings settings = QuranSettings.getInstance(this);
    ((QuranApplication)getApplication()).resetApplicationComponent();
    settings.setLastTalibId(t.id);
    runListView();

    /*TODO:
    1. this lastpage setting does not seem to suffice.
    2. Also set scroll in index view ?
    3. Titles in index view = talib name
    */
  }


}
