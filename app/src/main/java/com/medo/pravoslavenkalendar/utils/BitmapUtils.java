package com.medo.pravoslavenkalendar.utils;


import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;


public class BitmapUtils {

  public static void persistImage(Bitmap bitmap, File file) {

    OutputStream os;
    try {
      os = new FileOutputStream(file);
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
      os.flush();
      os.close();

      Log.d("Pravoslaven", "Image persisted: " + file.getAbsolutePath());
    }
    catch (Exception e) {
      Log.d("Pravoslaven", "Image not persisted: " + e.getMessage());
    }
  }
}
