package com.medo.pravoslavenkalendar.callbacks;

import java.io.File;


public interface BitmapCallback {

  public void onSuccess(File blurredOutputFile);

  public void onFailure();
}
