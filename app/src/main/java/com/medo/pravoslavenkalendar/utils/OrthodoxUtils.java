package com.medo.pravoslavenkalendar.utils;


import android.content.Context;

import com.medo.pravoslavenkalendar.R;


public class OrthodoxUtils {

  public static String getOldMonthName(Context context, int monthOfYear) {

    return context.getResources().getStringArray(R.array.old_months)[monthOfYear];
  }
}
