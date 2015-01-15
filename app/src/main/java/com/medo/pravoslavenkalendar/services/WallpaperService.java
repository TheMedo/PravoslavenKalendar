package com.medo.pravoslavenkalendar.services;

import android.app.IntentService;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.util.Log;

import com.medo.pravoslavenkalendar.BuildConfig;
import com.medo.pravoslavenkalendar.utils.FileUtils;
import com.medo.pravoslavenkalendar.utils.SystemUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;


public class WallpaperService extends IntentService {

  public WallpaperService(String name) {

    super(name);
  }

  public WallpaperService() {

    super("WallpaperService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {

    // get the current day
    // and check if we have the cached orthodox icon
    Calendar calendar = Calendar.getInstance();
    File imageFile = FileUtils.getOutputPictureFile(this, calendar.get(Calendar.DAY_OF_YEAR), false);

    Bitmap source;
    if (imageFile.exists()) {
      // decode the local file
      source = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
      Log.d("Pravoslaven", "Loading local file for wallpaper: " + imageFile.getAbsolutePath());
    }
    else {
      // get the remote orthodox icon
      try {
        String url = BuildConfig.API_IMAGE + calendar.get(Calendar.DAY_OF_YEAR) + ".jpg";
        source = Picasso.with(this)
                .load(url)
                .get();
        Log.d("Pravoslaven", "Loading remote file for wallpaper: " + url);
      }
      catch (IOException e) {
        // if not available don't change the wallpaper
        e.printStackTrace();
        return;
      }
    }

    // dim the image so the wallpaper doesn't
    // make the home screen look bad / unreadable
    try {
      Log.d("Pravoslaven", "Dimming bitmap...");
      final RenderScript renderScript = RenderScript.create(this);
      final ScriptC_dim script = new ScriptC_dim(renderScript);
      script.set_factor(0.6f);
      final Allocation alloc1 = Allocation.createFromBitmap(renderScript, source, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
      final Allocation alloc2 = Allocation.createTyped(renderScript, alloc1.getType());
      script.forEach_dim(alloc1, alloc2);
      alloc2.copyTo(source);
    }
    catch (Exception e) {
      e.printStackTrace();
      Log.d("Pravoslaven", "Cannot dim image.");
    }

    // set the bitmap as wallpaper
    try {
      // scale the bitmap a bit so it parallaxes when scrolling the home screens
      Point point = SystemUtils.getScreenSize(this);
      float width = point.x * 1.2f;
      float height = width / source.getWidth() * source.getHeight();

      WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
      wallpaperManager.setBitmap(Bitmap.createScaledBitmap(source, (int) width, (int) height, true));
      wallpaperManager.setWallpaperOffsetSteps(0.5f, 0f);
      Log.d("Pravoslaven", "Wallpaper set.");
    }
    catch (Exception e) {
      e.printStackTrace();
      Log.d("Pravoslaven", "Cannot set wallpaper.");
    }
  }
}
