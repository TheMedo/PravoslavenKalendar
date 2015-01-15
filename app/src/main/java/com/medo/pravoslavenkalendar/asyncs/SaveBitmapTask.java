package com.medo.pravoslavenkalendar.asyncs;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.medo.pravoslavenkalendar.utils.BitmapUtils;
import com.medo.pravoslavenkalendar.utils.FileUtils;

import java.io.File;


public class SaveBitmapTask extends AsyncTask<Bitmap, Void, Void> {

  private final Context context;
  private final int dayOfYear;

  public SaveBitmapTask(Context context, int dayOfYear) {

    this.context = context;
    this.dayOfYear = dayOfYear;
  }

  @Override
  protected Void doInBackground(Bitmap... params) {

    // cache the normal image on the SD
    File normalOutputFile = FileUtils.getOutputPictureFile(context, dayOfYear, false);
    if (!normalOutputFile.exists()) {
      BitmapUtils.persistImage(params[0], normalOutputFile);
    }
    return null;
  }
}

