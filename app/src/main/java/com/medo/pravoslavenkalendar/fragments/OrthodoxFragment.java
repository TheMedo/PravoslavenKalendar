package com.medo.pravoslavenkalendar.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.medo.pravoslavenkalendar.R;
import com.medo.pravoslavenkalendar.model.OrthodoxDay;
import com.medo.pravoslavenkalendar.utils.Extras;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class OrthodoxFragment extends Fragment {

  @InjectView(R.id.image_background)
  ImageView imageBackground;
  @InjectView(R.id.image_blur)
  ImageView imageBlur;

  private OrthodoxDay orthodoxDay;

  public static OrthodoxFragment newInstance(String jsonOrthodoxDay) {

    OrthodoxFragment fragment = new OrthodoxFragment();
    Bundle args = new Bundle();
    args.putString(Extras.EXTRA_DAY, jsonOrthodoxDay);
    fragment.setArguments(args);
    return fragment;
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
    // TODO switch to normal image instead of placeholder
    Picasso.with(getActivity())
            .load(/*orthodoxDay.getImageUrl()*/ R.drawable.placeholder)
            .fit()
            .into(imageBackground);

    return view;
  }

  @Override
  public void onDestroy() {

    super.onDestroy();
    ButterKnife.reset(this);
  }
}
