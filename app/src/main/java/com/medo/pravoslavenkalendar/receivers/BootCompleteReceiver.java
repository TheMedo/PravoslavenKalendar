package com.medo.pravoslavenkalendar.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.medo.pravoslavenkalendar.utils.Extras;
import com.medo.pravoslavenkalendar.utils.SystemUtils;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;


public class BootCompleteReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {

    Log.d("Pravoslaven", "Boot complete received");
    try {
      // create or open an existing database using the default name
      DB snappyDb = DBFactory.open(context);
      if (snappyDb.getBoolean(Extras.EXTRA_WALLPAPER)) {
        // wallpaper change is enabled
        // since the device was rebooted
        // we need to reschedule the wallpaper change
        SystemUtils.scheduleWallpaperChange(context);
        Log.d("Pravoslaven", "Wallpaper change scheduled.");
      }
    }
    catch (SnappydbException e) {
      Log.d("Pravoslaven", "Not scheduling wallpaper change.");
    }
  }
}
