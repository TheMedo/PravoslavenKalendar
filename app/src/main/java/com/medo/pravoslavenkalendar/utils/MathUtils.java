package com.medo.pravoslavenkalendar.utils;

import android.graphics.Color;


public class MathUtils {

  public static float scaleInRange(float valueIn, float baseMin, float baseMax, float limitMin, float limitMax) {

    return ((limitMax - limitMin) * (valueIn - baseMin) / (baseMax - baseMin)) + limitMin;
  }

  public static int shade(int color, float factor) {

    float r = Color.red(color) * factor;
    float g = Color.green(color) * factor;
    float b = Color.blue(color) * factor;
    int ir = Math.min(255, (int) r);
    int ig = Math.min(255, (int) g);
    int ib = Math.min(255, (int) b);
    return (Color.rgb(ir, ig, ib));
  }
}
