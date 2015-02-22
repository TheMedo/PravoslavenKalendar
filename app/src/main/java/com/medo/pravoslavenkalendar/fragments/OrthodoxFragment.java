package com.medo.pravoslavenkalendar.fragments;


import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.medo.pravoslavenkalendar.R;
import com.medo.pravoslavenkalendar.asyncs.BlurAndSaveBitmapTask;
import com.medo.pravoslavenkalendar.callbacks.BitmapCallback;
import com.medo.pravoslavenkalendar.callbacks.MainCallback;
import com.medo.pravoslavenkalendar.model.OrthodoxDay;
import com.medo.pravoslavenkalendar.utils.Extras;
import com.medo.pravoslavenkalendar.utils.MathUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class OrthodoxFragment extends Fragment implements BitmapCallback {

  @InjectView(R.id.container)
  RelativeLayout container;
  @InjectView(R.id.image_background)
  ImageView imageBackground;
  @InjectView(R.id.image_blur)
  ImageView imageBlur;

  private OrthodoxDay orthodoxDay;
  private MainCallback callback;
  private Palette palette;

  public static OrthodoxFragment newInstance(String jsonOrthodoxDay) {

    OrthodoxFragment fragment = new OrthodoxFragment();
    Bundle args = new Bundle();
    args.putString(Extras.EXTRA_DAY, jsonOrthodoxDay);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onAttach(Activity activity) {

    callback = (MainCallback) activity;
    super.onAttach(activity);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      // when a new instance is created the current day is passed as an argument
      String jsonOoshie = getArguments().getString(Extras.EXTRA_DAY);
      orthodoxDay = new Gson().fromJson(jsonOoshie, OrthodoxDay.class);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // inflate the fragment layout and inject the views
    View view = inflater.inflate(R.layout.fragment_orthodox_day, container, false);
    ButterKnife.inject(this, view);

    // we set the background image when we create the fragment view
    // but we set the blurred image on demand based on memory availability
    Picasso.with(getActivity())
            .load(orthodoxDay.getImageUrlOrPath(getActivity()))
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_error)
            .into(imageBackground, new Callback() {

              @TargetApi(Build.VERSION_CODES.HONEYCOMB)
              @Override
              public void onSuccess() {
                // don't allow palette update when the fragment is not visible
                if (!isAdded() || getActivity() == null) {
                  return;
                }
                // get the color palette from the loaded image
                Bitmap orthodoxIcon = ((BitmapDrawable) imageBackground.getDrawable()).getBitmap();
                palette = Palette.generate(orthodoxIcon, 3);
                if (getUserVisibleHint()) {
                  // if the fragment is visible when the palette is ready
                  // update the fab color in the main activity
                  callback.onPaletteReady(palette);
                }

                // blur and save the downloaded image
                BlurAndSaveBitmapTask bitmapTask = new BlurAndSaveBitmapTask(
                        getActivity(),
                        OrthodoxFragment.this,
                        orthodoxDay.getDayOfYear());

                // execute the task based on the OS version
                bitmapTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, orthodoxIcon);
              }

              @Override
              public void onError() {

              }
            });
    imageBackground.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {

        callback.togglePanel();
      }
    });

    return view;
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public void onPanelSlide(float percentage) {

    // when the panel is sliding from 0 to 1 and vice versa
    // we need to animate the orthodox image

    // start showing the blur overlay
    imageBlur.setAlpha(percentage);

    // scale the image a bit
    float newScale = MathUtils.scaleInRange(
            percentage,
            0f,
            1f,
            1f,
            1.2f);
    container.setScaleX(newScale);
    container.setScaleY(newScale);
  }

  public Palette getPalette() {

    return palette;
  }

  @Override
  public void onDestroy() {

    super.onDestroy();
    ButterKnife.reset(this);
  }

  @Override
  public void onSuccess(File blurredOutputFile) {

    // prevent NPE when the callback on the main thread occurs
    if (!isAdded() || getActivity() == null) {
      return;
    }
    // load the blurred image overlay
    Picasso.with(getActivity())
            .load(blurredOutputFile)
            .into(imageBlur);
  }

  @Override
  public void onFailure() {

    Log.d("Pravoslaven", "Cannot blur and persist image");
  }
}
