package com.medo.pravoslavenkalendar.utils;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;


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
}
