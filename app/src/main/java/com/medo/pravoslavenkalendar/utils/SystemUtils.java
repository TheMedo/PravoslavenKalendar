package com.medo.pravoslavenkalendar.utils;


import android.os.Build;


public class SystemUtils {

  public static boolean versionAtLeast(int version) {

    return Build.VERSION.SDK_INT >= version;
  }
}
