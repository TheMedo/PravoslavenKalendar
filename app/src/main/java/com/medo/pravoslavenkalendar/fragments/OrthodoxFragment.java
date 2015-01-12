package com.medo.pravoslavenkalendar.fragments;


import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.medo.pravoslavenkalendar.R;
import com.medo.pravoslavenkalendar.callbacks.MainCallback;
import com.medo.pravoslavenkalendar.model.OrthodoxDay;
import com.medo.pravoslavenkalendar.utils.Extras;
import com.medo.pravoslavenkalendar.utils.MathUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class OrthodoxFragment extends Fragment {

  @InjectView(R.id.container)
  RelativeLayout container;
  @InjectView(R.id.image_background)
  ImageView imageBackground;
  @InjectView(R.id.image_blur)
  ImageView imageBlur;
  @InjectView(R.id.image_dim)
  ImageView imageDim;

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
            .load(orthodoxDay.getImageUrl())
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_error)
            .fit()
            .into(imageBackground, new Callback() {

              @Override
              public void onSuccess() {
                // don't allow palette update when the fragment is not visible
                if (!isAdded() || getActivity() == null) {
                  return;
                }
                // get the color palette from the loaded image
                palette = Palette.generate(((BitmapDrawable) imageBackground.getDrawable()).getBitmap(), 3);
                if (getUserVisibleHint()) {
                  // if the fragment is visible when the palette is ready
                  // update the fab color in the main activity
                  callback.onPaletteReady(palette);
                }
              }

              @Override
              public void onError() {

              }
            });

    Picasso.with(getActivity())
            .load(orthodoxDay.getImageUrl())
            .transform(new Transformation() {

              @Override
              public Bitmap transform(Bitmap source) {
                // don't transform the image if the fragment
                // is not visible to the user
                if (!isAdded() || getActivity() == null) {
                  return source;
                }

                // create another bitmap that will hold the results of the filter.
                Bitmap blurredBitmap = Bitmap.createBitmap(source);

                // create the Renderscript instance that will do the work.
                RenderScript renderScript = RenderScript.create(getActivity());

                // allocate memory for Renderscript to work with
                Allocation input = Allocation.createFromBitmap(renderScript, source, Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SCRIPT);
                Allocation output = Allocation.createTyped(renderScript, input.getType());

                // load up an instance of the specific script that we want to use.
                ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
                script.setInput(input);
                // set the blur radius
                script.setRadius(25);
                // start the ScriptIntrinsicBlur
                script.forEach(output);
                // copy the output to the blurred bitmap
                output.copyTo(blurredBitmap);

                if (blurredBitmap == null) {
                  return source;
                }
                else {
                  source.recycle();
                  return blurredBitmap;
                }
              }

              @Override
              public String key() {

                return "blur";
              }
            })
            .fit()
            .into(imageBlur);

    return view;
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public void onPanelSlide(float percentage) {

    // when the panel is sliding from 0 to 1 and vice versa
    // we need to animate the orthodox image

    // start dimming the image by tweaking the black overlay opacity
    float newDim = MathUtils.scaleInRange(
            percentage,
            0f,
            1f,
            0f,
            0.4f);
    imageDim.setAlpha(newDim);

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
}
