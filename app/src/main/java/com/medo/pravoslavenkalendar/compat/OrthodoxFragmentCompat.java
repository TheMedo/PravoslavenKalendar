package com.medo.pravoslavenkalendar.compat;


import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.medo.pravoslavenkalendar.R;
import com.medo.pravoslavenkalendar.asyncs.SaveBitmapTask;
import com.medo.pravoslavenkalendar.callbacks.MainCallback;
import com.medo.pravoslavenkalendar.model.OrthodoxDay;
import com.medo.pravoslavenkalendar.utils.Extras;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class OrthodoxFragmentCompat extends Fragment {

  @InjectView(R.id.container)
  RelativeLayout container;
  @InjectView(R.id.image_background)
  ImageView imageBackground;

  private OrthodoxDay orthodoxDay;
  private MainCallback callback;

  public static OrthodoxFragmentCompat newInstance(String jsonOrthodoxDay) {

    OrthodoxFragmentCompat fragment = new OrthodoxFragmentCompat();
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
                // blur and save the downloaded image
                SaveBitmapTask bitmapTask = new SaveBitmapTask(getActivity(), orthodoxDay.getDayOfYear());
                bitmapTask.execute(orthodoxIcon);
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

  @Override
  public void onDestroy() {

    super.onDestroy();
    ButterKnife.reset(this);
  }
}
