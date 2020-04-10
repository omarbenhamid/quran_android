package com.quran.labs.androidquran;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.quran.labs.androidquran.database.tahfiz.SharedTahfizDatabase;
import com.quran.labs.androidquran.database.tahfiz.dao.TalibDAO;
import com.quran.labs.androidquran.database.tahfiz.entities.Talib;
import com.quran.labs.androidquran.ui.QuranActionBarActivity;
import com.quran.labs.androidquran.ui.QuranActivity;
import com.quran.labs.androidquran.ui.helpers.HifzoSyncAction;
import com.quran.labs.androidquran.ui.helpers.TalibListAdapter;
import com.quran.labs.androidquran.util.QuranSettings;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


public class QuranSelectTalibActivity extends QuranActionBarActivity {
  private static final String LOG_TAG = "QTA";
  private RecyclerView mRecyclerView;
  private TalibListAdapter talibListAdapter;

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

    talibListAdapter = new TalibListAdapter(this, mRecyclerView);
    mRecyclerView.setAdapter(talibListAdapter);

    talibDAO = SharedTahfizDatabase.getInstance(this).talibDAO();

    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener( (View view) -> {
      addTalib();
    });
  }

  private void addTalib() {
    final EditText talibName = new EditText(this);
    new AlertDialog.Builder(this)
        .setTitle(R.string.dialog_add_talib)
        .setView(talibName)
        .setPositiveButton(R.string.add_talib, (DialogInterface dialog, int which) -> {
          disposables.add(Completable
              .fromRunnable( () -> talibDAO.addTalib(new Talib(talibName.getText().toString())))
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(talibListAdapter::reload));

        }).show();
  }

  @Override
  protected void onDestroy() {
    disposables.clear();
    super.onDestroy();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    menu.add(R.string.add_talib).setOnMenuItemClickListener((MenuItem i) -> {
      addTalib();
      return true;
    });
    menu.add(R.string.hifzo_sync).setOnMenuItemClickListener((MenuItem i) -> {
      final View dlgView = getLayoutInflater().inflate(R.layout.hifzo_sync, null);
      QuranSettings settings = QuranSettings.getInstance(this);
      ((TextView)dlgView.findViewById(R.id.hifzo_login)).setText(settings.getSavedHifzoLogin());
      String sp = settings.getSavedHifzoPassword();
      ((TextView)dlgView.findViewById(R.id.hifzo_pass)).setText(sp);
      ((Switch)dlgView.findViewById(R.id.hifzo_rem_pass)).setChecked(!sp.isEmpty());

      dlgView.findViewById(R.id.hifzo_syncerr).setVisibility(View.INVISIBLE);
      AlertDialog dlg = new AlertDialog.Builder(this)
          .setTitle(R.string.hifzo_sync)
          .setView(dlgView)
          .setPositiveButton(R.string.sync_now, null)
          .setNegativeButton(R.string.cancel, null).show();

      dlg.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
          (View v) -> {
            dlgView.findViewById(R.id.hifzo_syncerr).setVisibility(View.INVISIBLE);
            String login = ((TextView)dlgView.findViewById(R.id.hifzo_login)).getText().toString();
            String password = ((TextView)dlgView.findViewById(R.id.hifzo_pass)).getText().toString();
            boolean savePass = ((Switch)dlgView.findViewById(R.id.hifzo_rem_pass)).isChecked();
            if(!savePass) settings.setSavedHifzoPassword("");

            final ProgressDialog progress = new ProgressDialog(QuranSelectTalibActivity.this);
            progress.show();
            disposables.add(
                Observable.fromPublisher(new HifzoSyncAction(this, talibDAO, login, password))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        (String msg) -> {
                          progress.setMessage(msg);
                        },
                        (Throwable ex) -> {
                            progress.dismiss();
                            ((TextView)dlgView.findViewById(R.id.hifzo_syncerr)).setText(ex.getMessage());
                            dlgView.findViewById(R.id.hifzo_syncerr).setVisibility(View.VISIBLE);
                            talibListAdapter.reload();
                            Log.d(LOG_TAG, "Error connecting to hifzo", ex);
                        },
                        () -> {
                          progress.dismiss();
                          settings.setSavedHifzoLogin(login);
                          if(savePass)
                            settings.setSavedHifzoPassword(password);
                          talibListAdapter.reload();
                          dlg.dismiss();
                        })
            );

          }
      );
      return true;
    });

    return super.onCreateOptionsMenu(menu);
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
  }


  public void editTalib(Talib element) {
    final EditText talibName = new EditText(this);
    talibName.setText(element.name);

    AlertDialog.Builder b = new AlertDialog.Builder(this)
        .setTitle(R.string.dialog_edit_talib)
        .setView(talibName)
        .setNegativeButton(R.string.delete_talib, (DialogInterface dialog, int which) -> {
          new AlertDialog.Builder(this)
              .setTitle(R.string.dialog_delete_talib)
              .setMessage(R.string.confirm_delete_talib)
              .setPositiveButton(R.string.delete_talib, (DialogInterface d, int w) -> {
                disposables.add(Completable
                    .fromRunnable(() -> {
                      talibDAO.delete(element.id);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(talibListAdapter::reload)
                );

              }).show();
        });
    if(element.hifzoServerKey == null) {
      b.setPositiveButton(R.string.update_talib, (DialogInterface dialog, int which) -> {
        disposables.add(Completable
            .fromRunnable(() -> {
              element.name = talibName.getText().toString();
              talibDAO.update(element);
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(talibListAdapter::reload)
        );
      });
    }else{
      talibName.setEnabled(false);
      b.setPositiveButton(R.string.hifzo_open, (DialogInterface dialog, int which) ->
      {
        startActivity(new Intent(Intent.ACTION_VIEW,
            Uri.parse(element.hifzoUrl)));
      });
    }

    b.show();
  }
}
