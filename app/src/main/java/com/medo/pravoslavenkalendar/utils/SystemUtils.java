package com.medo.pravoslavenkalendar.utils;


import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.medo.pravoslavenkalendar.MainActivity;
import com.medo.pravoslavenkalendar.R;
import com.medo.pravoslavenkalendar.services.WallpaperService;

import java.io.IOException;
import java.text.SimpleDateFormat;
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
    triggerTime.add(Calendar.DAY_OF_YEAR, 1);
    // schedule repeating task for daily wallpaper change
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    alarmManager.setInexactRepeating(AlarmManager.RTC, triggerTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
    // start the service immediately
    context.startService(wallpaperIntent);
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

  public static void showProgressNotification(Context context, int dayOfYear) {

    // create indeterminate progress notification style
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.ic_stat_cross)
            .setColor(context.getResources().getColor(R.color.red_light))
            .setContentTitle(context.getString(R.string.app_name_condensed))
            .setContentText(context.getString(R.string.hint_wallpaper_set))
            .setOngoing(true)
            .setProgress(0, 100, true);

    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    // use the day of year as notification id
    notificationManager.notify(dayOfYear, builder.build());
    Log.d("Pravoslaven", "Notification shown for: " + dayOfYear);
  }

  public static void cancelNotification(Context context, int dayOfYear) {
    // hide the progress notification
    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancel(dayOfYear);
    Log.d("Pravoslaven", "Notification hidden for: " + dayOfYear);
  }

  public static void showWallpaperNotification(Context context, Bitmap orthodoxIcon, String summaryText) {

    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM");
    // create the big notification style
    // the big picture should be the ooshie we are unlocking
    // the big image should be the app icon
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.ic_stat_cross)
            .setColor(context.getResources().getColor(R.color.red_light))
            .setContentTitle(context.getString(R.string.app_name_condensed) + " - " + simpleDateFormat.format(calendar.getTime()))
            .setStyle(
                    new NotificationCompat.BigPictureStyle()
                            .bigPicture(orthodoxIcon)
                            .setSummaryText(summaryText))
            .setAutoCancel(true);

    // creates an explicit intent for starting the main activity
    // add the unlocked ooshie so we can focus it
    Intent resultIntent = new Intent(context, MainActivity.class);

    // the stack builder object will contain an artificial back stack for the started activity.
    // this ensures that navigating backward from the activity leads out of your application to the home screen.
    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
    // adds the back stack for the Intent (but not the intent itself)
    stackBuilder.addParentStack(MainActivity.class);
    // adds the intent that starts the activity to the top of the stack
    stackBuilder.addNextIntent(resultIntent);
    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    builder.setContentIntent(resultPendingIntent);
    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    // use a random notification since all notifications should be different
    notificationManager.notify(calendar.get(Calendar.DAY_OF_YEAR), builder.build());
    Log.d("Pravoslaven", "Notification shown for: " + summaryText);
  }
}
