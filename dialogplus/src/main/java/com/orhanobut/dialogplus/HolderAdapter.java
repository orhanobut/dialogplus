package com.orhanobut.dialogplus;

import android.support.annotation.NonNull;
import android.widget.BaseAdapter;

public interface HolderAdapter extends Holder {

  void setAdapter(@NonNull BaseAdapter adapter);

  void setOnItemClickListener(OnHolderListener listener);
}
