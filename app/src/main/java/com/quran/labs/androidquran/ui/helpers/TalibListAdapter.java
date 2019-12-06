package com.quran.labs.androidquran.ui.helpers;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quran.labs.androidquran.QuranSelectTalibActivity;
import com.quran.labs.androidquran.R;
import com.quran.labs.androidquran.database.tahfiz.SharedTahfizDatabase;
import com.quran.labs.androidquran.database.tahfiz.entities.Talib;


import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TalibListAdapter extends
    RecyclerView.Adapter<TalibListAdapter.ViewHolder>
    implements View.OnClickListener, View.OnLongClickListener {

  private QuranSelectTalibActivity context;
  private LayoutInflater inflater;
  private Talib[] elements;
  private RecyclerView recyclerView;
  private SparseBooleanArray checkedState;

  public TalibListAdapter(QuranSelectTalibActivity context, RecyclerView recyclerView) {
    inflater = LayoutInflater.from(context);
    this.recyclerView = recyclerView;
    this.context = context;
    checkedState = new SparseBooleanArray();
    reload(); //Initial reload
  }

  public Disposable reload() {
    return Observable.fromCallable(SharedTahfizDatabase.getInstance(context).talibDAO()::listAllTalibs)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe((Talib[] talibs) -> {
          setElements(talibs);
        });
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public int getItemCount() {
    return elements == null ? 0 : elements.length;
  }

  private Talib getTalib(int position) {
    return elements[position];
  }

  public boolean isItemChecked(int position) {
    return checkedState.get(position);
  }

  public void setItemChecked(int position, boolean checked) {
    checkedState.put(position, checked);
    notifyItemChanged(position);
  }

  public List<Talib> getCheckedItems() {
    final List<Talib> result = new ArrayList<>();
    final int count = checkedState.size();
    final int elements = getItemCount();
    for (int i = 0; i < count; i++) {
      final int key = checkedState.keyAt(i);
      // TODO: figure out why sometimes elements > key
      if (checkedState.get(key) && elements > key) {
        result.add(getTalib(key));
      }
    }
    return result;
  }

  public void uncheckAll() {
    checkedState.clear();
    notifyDataSetChanged();
  }

  public void setElements(Talib[] elements) {
    this.elements = elements;
    notifyDataSetChanged();
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final View view = inflater.inflate(R.layout.talib_list_row, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int pos) {
    final Talib item = elements[pos];
    holder.title.setText(item.name);
    holder.setChecked(isItemChecked(pos));
    holder.setEnabled(true);
  }

  @Override
  public void onClick(View v) {
    final int position = recyclerView.getChildAdapterPosition(v);
    if (position != RecyclerView.NO_POSITION) {
      context.selectTalib(elements[position]);
    }
  }

  @Override
  public boolean onLongClick(View v) {
    final int position = recyclerView.getChildAdapterPosition(v);
    if (position != RecyclerView.NO_POSITION) {
      context.editTalib(elements[position]);
    }

    return false;
  }

  class ViewHolder extends RecyclerView.ViewHolder {

    TextView title;
    View view;


    ViewHolder(View itemView) {
      super(itemView);
      view = itemView;
      title = itemView.findViewById(R.id.title);
    }

    void setEnabled(boolean enabled) {
      view.setEnabled(enabled);
      itemView.setOnClickListener(enabled ? TalibListAdapter.this : null);
      itemView.setOnLongClickListener(enabled ? TalibListAdapter.this : null);
    }

    void setChecked(boolean checked) {
      view.setActivated(checked);
    }

  }
}
