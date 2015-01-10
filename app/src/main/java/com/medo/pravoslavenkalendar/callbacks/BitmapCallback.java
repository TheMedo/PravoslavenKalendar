package com.medo.pravoslavenkalendar.callbacks;

import android.graphics.Bitmap;


public interface BitmapCallback {

  public void onSuccess(Bitmap bitmap);

  public void onFailure();
}
