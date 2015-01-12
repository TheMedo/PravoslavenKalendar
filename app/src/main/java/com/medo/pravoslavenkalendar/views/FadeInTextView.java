package com.medo.pravoslavenkalendar.views;

import android.content.Context;
import android.graphics.Color;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.TextView;


public class FadeInTextView extends AdvancedTextView {

  private float delayTime = 1.5f;
  private int color;
  private CharSequence text;

  private float[] offsets;
  private long startTimeMs = 0;

  private Runnable updateRunnable;

  public FadeInTextView(Context context) {

    super(context);
  }

  public FadeInTextView(Context context, AttributeSet attrs) {

    super(context, attrs);
  }

  private static float clamp(float val, float min, float max) {

    return Math.max(min, Math.min(max, val));
  }

  private static float parametric(float t, float min, float max) {

    return t * (max - min) + min;
  }

  public void animateText() {

    startTimeMs = 0;
    if (updateRunnable != null) {
      removeCallbacks(updateRunnable);
    }

    updateRunnable = new Runnable() {

      @Override
      public void run() {

        if (doTick()) {
          // Update at 60fps if not every character is at full alpha
          postDelayed(updateRunnable, 1000 / 60);
        }
      }
    };

    post(updateRunnable);
    doTick();
  }

  private Boolean doTick() {

    if (startTimeMs == 0) {
      startTimeMs = SystemClock.uptimeMillis();
    }

    long currentTimeMs = SystemClock.uptimeMillis();
    long deltaTimeMs = Math.max(currentTimeMs - startTimeMs, 0);

    int r = (color >> 16) & 0xFF;
    int g = (color >> 8) & 0xFF;
    int b = (color) & 0xFF;

    SpannableString mSpanText = new SpannableString(this.text);
    Boolean anyLeft = false;

    // on each tick, for each character, let delta = max(time from start time, 0),
    for (int i = 0; i < offsets.length; i++) {
      float tt = ((deltaTimeMs / 1000f) / offsets[i]);
      tt = Math.min(tt, 1f);
      tt = 1f - (1f - tt) * (1f - tt);

      int targetAlpha = (int) parametric(clamp(tt, 0f, 1f), 0f, 255f);
      if (targetAlpha < 255) {
        anyLeft = true;
      }
      mSpanText.setSpan(new ForegroundColorSpan(Color.argb(targetAlpha, r, g, b)), i, i + 1, 0);
    }

    setText(mSpanText, TextView.BufferType.SPANNABLE);
    return anyLeft;
  }

  public void setDelayTime(float delayTime) {

    this.delayTime = delayTime;
  }

  public void initSpanText(CharSequence text, int color) {

    this.text = text == null ? "" : text;
    this.color = color;

    startTimeMs = 0;
    // generate an array of floats the size of the length of text
    // each float is random value between 0 and .6f
    offsets = new float[text == null ? 0 : text.length()];
    for (int i = 0; i < offsets.length; i++) {
      offsets[i] = ((float) Math.random()) * delayTime;
    }
  }
}