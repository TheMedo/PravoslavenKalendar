package com.medo.pravoslavenkalendar.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.medo.pravoslavenkalendar.R;


public class AdvancedTextView extends TextView {

  public AdvancedTextView(Context context) {

    super(context);
  }

  public AdvancedTextView(Context context, AttributeSet attrs) {

    super(context, attrs);
    setCustomFont(context, attrs);
  }

  public AdvancedTextView(Context context, AttributeSet attrs, int defStyle) {

    super(context, attrs, defStyle);
    setCustomFont(context, attrs);
  }

  private void setCustomFont(Context context, AttributeSet attrs) {

    TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AdvancedTextView);
    String customFont = typedArray.getString(R.styleable.AdvancedTextView_customFont);
    setCustomFont(context, customFont);
    typedArray.recycle();
  }

  void setCustomFont(Context context, String asset) {

    try {
      setTypeface(Typeface.createFromAsset(context.getAssets(), asset));
    }
    catch (Exception e) {
      Log.d("Pravoslaven", "Could not get typeface: " + e.getMessage());
    }
  }
}