package com.medo.pravoslavenkalendar.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.medo.pravoslavenkalendar.R;

import java.io.File;
import java.io.IOException;


public class FileUtils {

  public static File getOutputPictureFile(Context context, int dayOfYear, boolean blurred) {
    // To be safe, you should check that the SDCard is mounted
    // using Environment.getExternalStorageState() before doing this.
    File mediaStorageDir = new File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            context.getString(R.string.app_name));
    // This location works best if you want the created images to be shared
    // between applications and persist after your app has been uninstalled.

    // Create the storage directory if it does not exist
    if (!mediaStorageDir.exists()) {
      if (!mediaStorageDir.mkdirs()) {
        Log.d("Pravoslaven", "Failed to create directory");
        return null;
      }

      // create a no media file to prevent the cached image to appear in gallery
      // http://stackoverflow.com/questions/6713850/android-how-to-hide-folder-from-appearing-in-the-gallery/6713863#6713863
      File noMediaFile = new File(mediaStorageDir.getAbsolutePath() + File.separator + ".nomedia");
      System.out.println(noMediaFile.getAbsolutePath());
      if (!noMediaFile.exists()) {
        try {
          if (!noMediaFile.createNewFile()) {
            Log.d("Pravoslaven", "Failed to create .nomedia file");
          }
          else {
            Log.d("Pravoslaven", ".nomedia file created");
          }
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    // Create a media file name based on the blur status
    if (!blurred) {
      return new File(mediaStorageDir.getPath() + File.separator + dayOfYear + ".jpg");
    }
    else {
      return new File(mediaStorageDir.getPath() + File.separator + dayOfYear + "b.jpg");
    }
  }
}
