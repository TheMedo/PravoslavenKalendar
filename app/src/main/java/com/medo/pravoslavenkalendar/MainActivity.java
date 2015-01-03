package com.medo.pravoslavenkalendar;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.medo.pravoslavenkalendar.adapters.OrthodoxPagerAdapter;
import com.medo.pravoslavenkalendar.callbacks.JsonCallbacks;
import com.medo.pravoslavenkalendar.model.OrthodoxDay;
import com.medo.pravoslavenkalendar.utils.JsonUtils;

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

    // initialize the calendar by parsing the assets json
    JsonUtils.parseCalendar(this, "2015.json", this);
    // TODO show loading screen (this should be practically unnoticeable on newer devices
  }

  @Override
  public void onCalendarReady(List<OrthodoxDay> orthodoxDays) {

    // TODO remove the loading screen
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
