package com.medo.pravoslavenkalendar;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.medo.pravoslavenkalendar.utils.Extras;
import com.medo.pravoslavenkalendar.utils.SystemUtils;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;


public class BaseActivity extends FragmentActivity {

  protected DB snappyDb;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    try {
      //create or open an existing database using the default name
      snappyDb = DBFactory.open(this);
    }
    catch (SnappydbException e) {
      Log.d("Pravoslaven", "Cannot open SnappyDB.");
    }
  }

  @Override
  protected void onDestroy() {

    try {
      snappyDb.close();
    }
    catch (SnappydbException e) {
      Log.d("Pravoslaven", "Cannot close SnappyDB.");
    }
    super.onDestroy();
  }

  protected void setFavorite(int dayOfYear, boolean favorite) {

    try {
      // update the favorite status for the current day
      snappyDb.putBoolean(String.valueOf(dayOfYear), favorite);
    }
    catch (SnappydbException e) {
      Log.d("Pravoslaven", "Cannot set favorite for day: " + dayOfYear);
    }
  }

  protected boolean isFavorite(int dayOfYear) {

    try {
      // get the favorite status for the current day
      return snappyDb.getBoolean(String.valueOf(dayOfYear));
    }
    catch (SnappydbException e) {
      Log.d("Pravoslaven", "No favorite status for: " + dayOfYear);
      // return false if no value is persisted in the app
      return false;
    }
  }

  protected void setWallpaperEnabled(boolean isEnabled) {

    // schedule or cancel the wallpaper change based on the new state
    if (isEnabled) {
      SystemUtils.scheduleWallpaperChange(this);
    }
    else {
      SystemUtils.cancelWallpaperChange(this);
    }

    try {
      // update the favorite status for the current day
      snappyDb.putBoolean(Extras.EXTRA_WALLPAPER, isEnabled);
    }
    catch (SnappydbException e) {
      Log.d("Pravoslaven", "Cannot set wallpaper enabled: " + isEnabled);
    }
  }

  protected boolean isWallaperEnabled() {

    try {
      // get the favorite status for the current day
      return snappyDb.getBoolean(Extras.EXTRA_WALLPAPER);
    }
    catch (SnappydbException e) {
      Log.d("Pravoslaven", "No wallpaper enabled status.");
      // return false if no value is persisted in the app
      return false;
    }
  }
}
