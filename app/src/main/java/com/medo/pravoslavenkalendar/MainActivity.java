package com.medo.pravoslavenkalendar;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.ListView;

import com.medo.pravoslavenkalendar.adapters.OrthodoxPagerAdapter;
import com.medo.pravoslavenkalendar.callbacks.JsonCallbacks;
import com.medo.pravoslavenkalendar.model.OrthodoxDay;
import com.medo.pravoslavenkalendar.transforms.ParallaxPageTransformer;
import com.medo.pravoslavenkalendar.transforms.ParallaxTransformInformation;
import com.medo.pravoslavenkalendar.utils.JsonUtils;

import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends FragmentActivity implements JsonCallbacks, ViewPager.OnPageChangeListener {

  @InjectView(R.id.pager)
  ViewPager pager;
  @InjectView(R.id.list)
  ListView list;

  private List<OrthodoxDay> orthodoxDays;
  private Calendar calendar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    // set the content view and inject the views
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.inject(this);

    // setup the view pager
    ParallaxPageTransformer pageTransformer = new ParallaxPageTransformer()
            .addViewToParallax(new ParallaxTransformInformation(R.id.image_background, 2, 2));
    //      .addViewToParallax(new ParallaxTransformInformation(R.id.tutorial_img_phone, -0.65f, ParallaxTransformInformation.PARALLAX_EFFECT_DEFAULT));
    pager.setPageTransformer(true, pageTransformer);


    // initialize the calendar by parsing the assets json
    // we don't keep the calendar in database since json parsing
    // with Gson is lighting fast even on lower end devices
    JsonUtils.parseCalendar(this, "2015.json", this);
  }

  @Override
  public void onCalendarReady(List<OrthodoxDay> orthodoxDays) {

    // the list has been parsed, save it
    this.orthodoxDays = orthodoxDays;
    // get a calendar instance
    this.calendar = Calendar.getInstance();

    // initialize the view pager adapter
    OrthodoxPagerAdapter orthodoxPagerAdapter = new OrthodoxPagerAdapter(getSupportFragmentManager(), orthodoxDays);
    pager.setAdapter(orthodoxPagerAdapter);

    // add page change listener and
    // set the view pager to show the current day
    // the calendar DAY_OF_YEAR field starts from 1 i.e. position 0
    pager.setOnPageChangeListener(this);
    int currentPage = calendar.get(Calendar.DAY_OF_YEAR) - 1;
    pager.setCurrentItem(currentPage);
  }

  @Override
  public void onError(Exception ex) {

    // TODO show the error
  }

  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

  }

  @Override
  public void onPageSelected(int position) {

    // get the selected day
    OrthodoxDay orthodoxDay = orthodoxDays.get(position);
    // TODO change the adapter
  }

  @Override
  public void onPageScrollStateChanged(int state) {

  }
}
