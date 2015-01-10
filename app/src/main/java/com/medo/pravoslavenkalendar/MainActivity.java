package com.medo.pravoslavenkalendar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.medo.pravoslavenkalendar.adapters.OrthodoxPagerAdapter;
import com.medo.pravoslavenkalendar.callbacks.JsonCallbacks;
import com.medo.pravoslavenkalendar.fragments.OrthodoxFragment;
import com.medo.pravoslavenkalendar.model.OrthodoxDay;
import com.medo.pravoslavenkalendar.model.OrthodoxHoliday;
import com.medo.pravoslavenkalendar.transforms.ParallaxPageTransformer;
import com.medo.pravoslavenkalendar.transforms.ParallaxTransformInformation;
import com.medo.pravoslavenkalendar.utils.JsonUtils;
import com.medo.pravoslavenkalendar.utils.SystemUtils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends FragmentActivity implements JsonCallbacks, ViewPager.OnPageChangeListener {

  @InjectView(R.id.panel)
  SlidingUpPanelLayout panel;
  @InjectView(R.id.pager)
  ViewPager pager;
  @InjectView(R.id.drawer)
  RelativeLayout drawer;

  private List<OrthodoxDay> orthodoxDays;
  private Calendar calendar;
  private SimpleDateFormat simpleDateFormat;
  private String[] oldMonths;

  private int selectedDay;

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
      pager.setPageTransformer(true, pageTransformer);
    }


    // initialize the calendar by parsing the assets json
    // we don't keep the calendar in database since json parsing
    // with Gson is lighting fast even on lower end devices
    JsonUtils.parseCalendar(this, "2015.json", this);
    // initialize the simple date formatter
    simpleDateFormat = new SimpleDateFormat("dd.MM");
    oldMonths = getResources().getStringArray(R.array.old_months);

    // measure the drawer view
    drawer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

      @SuppressLint("NewApi")
      @Override
      public void onGlobalLayout() {

        // the view has been measured
        // remove the layout listeners based on the SDK version
        if (SystemUtils.versionAtLeast(Build.VERSION_CODES.JELLY_BEAN)) {
          drawer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
        else {
          drawer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
        // the view has been measured
        // use the values to set the panel initial height
        // it should be sum of the header height plus the fab margin offset
        panel.setPanelHeight(drawer.findViewById(R.id.linear_header).getHeight() + getResources().getDimensionPixelSize(R.dimen.fab_size_half));
        // we should parallax the view pager with the footer buttons height
        panel.setParalaxOffset(drawer.findViewById(R.id.linear_footer).getHeight());
        panel.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

          @Override
          public void onPanelSlide(View view, float percentage) {

            if (SystemUtils.versionAtLeast(Build.VERSION_CODES.HONEYCOMB)) {
              // get the current pager fragment
              // animate the orthodox icon based on the panel offset
              OrthodoxFragment orthodoxFragment = ((OrthodoxPagerAdapter) pager.getAdapter()).getFragment(pager.getCurrentItem());
              orthodoxFragment.onPanelSlide(percentage);
            }
          }

          @Override
          public void onPanelCollapsed(View view) {

          }

          @Override
          public void onPanelExpanded(View view) {

          }

          @Override
          public void onPanelAnchored(View view) {

          }

          @Override
          public void onPanelHidden(View view) {

          }
        });
      }
    });
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
    selectedDay = calendar.get(Calendar.DAY_OF_YEAR) - 1;
    pager.setCurrentItem(selectedDay);
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
    selectedDay = position;
    // modify the info
    setupDrawer();
  }

  @Override
  public void onPageScrollStateChanged(int state) {

    // when we start flipping the view pager
    // we need to collapse the panel
    if (panel.isPanelExpanded()) {
      panel.collapsePanel();
    }
  }

  @Override
  public void onBackPressed() {

    // collapse the panel on back pressed
    if (panel.isPanelExpanded()) {
      panel.collapsePanel();
    }
    else {
      super.onBackPressed();
    }
  }

  private void setupDrawer() {

    // get the day and all the holidays
    final OrthodoxDay orthodoxDay = orthodoxDays.get(selectedDay);
    final OrthodoxHoliday orthodoxHolidayMajor = orthodoxDay.getHolidays().get(0);

    // set the date
    calendar.set(Calendar.DAY_OF_YEAR, selectedDay + 1);
    ((TextView) drawer.findViewById(R.id.text_date)).setText(simpleDateFormat.format(calendar.getTime()));
    ((TextView) drawer.findViewById(R.id.text_old_month)).setText(oldMonths[calendar.get(Calendar.MONTH)]);

    // set the major holiday
    ((TextView) drawer.findViewById(R.id.text_holiday)).setText(orthodoxHolidayMajor.getName());
    drawer.findViewById(R.id.text_holiday).setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {

        // open the holiday description in browser
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(orthodoxHolidayMajor.getDescriptionUrl())));
      }
    });

    // set the fasting foods
    String fastingFoods = "";
    for (String fastingFood : orthodoxDay.getFastingFoods()) {
      fastingFoods += fastingFood + " ";
    }
    ((TextView) drawer.findViewById(R.id.text_fasting)).setText(fastingFoods.trim());

    // set the other holidays if available
    if (orthodoxDay.getHolidays().size() > 1) {
      // clean the previous items if needed
      LinearLayout linearItems = (LinearLayout) drawer.findViewById(R.id.linear_items);
      linearItems.removeAllViews();

      int padding = getResources().getDimensionPixelSize(R.dimen.padding_normal);
      // iterate through all minor holidays and add them in the drawer
      for (int i = 1; i < orthodoxDay.getHolidays().size(); i++) {

        // get the minor holiday
        final OrthodoxHoliday orthodoxHolidayMinor = orthodoxDay.getHolidays().get(i);
        // create a text view and set all minor holiday parameters
        TextView textOrthodoxHoliday = new TextView(this);
        textOrthodoxHoliday.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textOrthodoxHoliday.setGravity(Gravity.CENTER);
        textOrthodoxHoliday.setMaxLines(1);
        textOrthodoxHoliday.setEllipsize(TextUtils.TruncateAt.END);
        textOrthodoxHoliday.setPadding(0, 0, 0, padding);

        textOrthodoxHoliday.setText(orthodoxHolidayMinor.getName());
        textOrthodoxHoliday.setOnClickListener(new View.OnClickListener() {

          @Override
          public void onClick(View v) {
            // open the holiday description in browser
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(orthodoxHolidayMinor.getDescriptionUrl())));
          }
        });
        linearItems.addView(textOrthodoxHoliday);
      }
    }
  }
}
