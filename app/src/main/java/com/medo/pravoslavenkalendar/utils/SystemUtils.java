package com.medo.pravoslavenkalendar.utils;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.medo.pravoslavenkalendar.R;
import com.medo.pravoslavenkalendar.services.WallpaperService;

import java.io.IOException;
import java.util.Calendar;


public class SystemUtils {

  public static boolean versionAtLeast(int version) {

    return Build.VERSION.SDK_INT >= version;
  }

  public static void shareImage(Context context, String text, String imagePath) {

    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("image/*");
    intent.putExtra(Intent.EXTRA_TEXT, text);
    Uri path = Uri.parse("file://" + imagePath);
    intent.putExtra(Intent.EXTRA_STREAM, path);
    Intent mailer = Intent.createChooser(intent, null);
    context.startActivity(mailer);
  }

  public static Point getScreenSize(Context context) {

    WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    Display display = windowManager.getDefaultDisplay();

    Point point = new Point();
    display.getSize(point);
    return point;
  }

  public static void scheduleWallpaperChange(Context context) {
    // create the wallpaper change intent
    Intent wallpaperIntent = new Intent(context, WallpaperService.class);
    PendingIntent alarmIntent = PendingIntent.getService(context, 0, wallpaperIntent, 0);
    // make the wallpaper change around 1 in the morning
    // but it shouldn't wake the device up
    Calendar triggerTime = Calendar.getInstance();
    triggerTime.set(Calendar.HOUR_OF_DAY, 1);
    // schedule repeating task for daily wallpaper change
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    alarmManager.setInexactRepeating(AlarmManager.RTC, triggerTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
    // notify the user that the wallpaper is being set
    Toast.makeText(context, R.string.hint_wallpaper_set, Toast.LENGTH_SHORT).show();
  }

  public static void cancelWallpaperChange(Context context) {
    // clear the wallpaper
    try {
      WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
      wallpaperManager.clear();
      Log.d("Pravoslaven", "Wallpaper cleared.");
    }
    catch (IOException e) {
      Log.d("Pravoslaven", "Cannot clear wallpaper.");
    }
    // create the wallpaper change intent
    Intent wallpaperIntent = new Intent(context, WallpaperService.class);
    PendingIntent alarmIntent = PendingIntent.getService(context, 0, wallpaperIntent, 0);
    // cancel the repeating task
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    alarmManager.cancel(alarmIntent);
  }
}
