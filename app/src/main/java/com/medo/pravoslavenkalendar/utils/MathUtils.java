package com.medo.pravoslavenkalendar.utils;

public class MathUtils {

  public static float scaleInRange(float valueIn, float baseMin, float baseMax, float limitMin, float limitMax) {

    return ((limitMax - limitMin) * (valueIn - baseMin) / (baseMax - baseMin)) + limitMin;
  }
}
