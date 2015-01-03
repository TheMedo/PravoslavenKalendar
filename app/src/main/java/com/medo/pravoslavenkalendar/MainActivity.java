package com.medo.pravoslavenkalendar;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.medo.pravoslavenkalendar.adapters.OrthodoxPagerAdapter;
import com.medo.pravoslavenkalendar.callbacks.JsonCallbacks;
import com.medo.pravoslavenkalendar.model.OrthodoxDay;
import com.medo.pravoslavenkalendar.transforms.ParallaxPageTransformer;
import com.medo.pravoslavenkalendar.transforms.ParallaxTransformInformation;
import com.medo.pravoslavenkalendar.utils.JsonUtils;
import com.medo.pravoslavenkalendar.utils.SystemUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends FragmentActivity implements JsonCallbacks {

  @InjectView(R.id.pager)
  ViewPager viewPager;

  private List<OrthodoxDay> orthodoxDays;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    // set the content view and inject the views
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.inject(this);

    // setup the view pager
    if (SystemUtils.versionAtLeast(Build.VERSION_CODES.HONEYCOMB)) {

      ParallaxPageTransformer pageTransformer = new ParallaxPageTransformer()
              .addViewToParallax(new ParallaxTransformInformation(R.id.image_background, 2, 2));
      //      .addViewToParallax(new ParallaxTransformInformation(R.id.tutorial_img_phone, -0.65f, ParallaxTransformInformation.PARALLAX_EFFECT_DEFAULT));
      viewPager.setPageTransformer(true, pageTransformer);
    }

    // initialize the calendar by parsing the assets json
    // we don't keep the calendar in database since json parsing
    // with Gson is lighting fast even on lower end devices
    JsonUtils.parseCalendar(this, "2015.json", this);
  }

  @Override
  public void onCalendarReady(List<OrthodoxDay> orthodoxDays) {

    // the list has been parsed, save it
    this.orthodoxDays = orthodoxDays;
    // initialize the view pager adapter
    OrthodoxPagerAdapter orthodoxPagerAdapter = new OrthodoxPagerAdapter(getSupportFragmentManager(), orthodoxDays);
    viewPager.setAdapter(orthodoxPagerAdapter);
  }

  @Override
  public void onError(Exception ex) {

    // TODO show the error
  }
}
