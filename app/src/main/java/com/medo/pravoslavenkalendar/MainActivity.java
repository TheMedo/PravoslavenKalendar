package com.medo.pravoslavenkalendar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.medo.pravoslavenkalendar.adapters.OrthodoxPagerAdapter;
import com.medo.pravoslavenkalendar.callbacks.JsonCallbacks;
import com.medo.pravoslavenkalendar.callbacks.MainCallback;
import com.medo.pravoslavenkalendar.fragments.OrthodoxFragment;
import com.medo.pravoslavenkalendar.model.OrthodoxDay;
import com.medo.pravoslavenkalendar.model.OrthodoxHoliday;
import com.medo.pravoslavenkalendar.transforms.ParallaxPageTransformer;
import com.medo.pravoslavenkalendar.transforms.ParallaxTransformInformation;
import com.medo.pravoslavenkalendar.utils.JsonUtils;
import com.medo.pravoslavenkalendar.utils.MathUtils;
import com.medo.pravoslavenkalendar.utils.SystemUtils;
import com.medo.pravoslavenkalendar.views.FadeInTextView;
import com.melnykov.fab.FloatingActionButton;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends BaseActivity implements
        JsonCallbacks,
        MainCallback,
        ViewPager.OnPageChangeListener {

  @InjectView(R.id.panel)
  SlidingUpPanelLayout panel;
  @InjectView(R.id.pager)
  ViewPager pager;
  @InjectView(R.id.drawer)
  RelativeLayout drawer;
  @InjectView(R.id.fab)
  FloatingActionButton fab;
  @InjectView(R.id.image_calendar)
  ImageButton imageCalendar;
  @InjectView(R.id.image_fasting)
  ImageButton imageFasting;
  @InjectView(R.id.image_wallpaper)
  ImageButton imageWallpaper;
  @InjectView(R.id.image_settings)
  ImageButton imageSettings;
  @InjectView(R.id.text_monday)
  TextView textMonday;
  @InjectView(R.id.text_tuesday)
  TextView textTuesday;
  @InjectView(R.id.text_wednesday)
  TextView textWednesday;
  @InjectView(R.id.text_thursday)
  TextView textThursday;
  @InjectView(R.id.text_friday)
  TextView textFriday;
  @InjectView(R.id.text_saturday)
  TextView textSaturday;
  @InjectView(R.id.text_sunday)
  TextView textSunday;
  @InjectView(R.id.text_holiday)
  FadeInTextView textHoliday;
  @InjectView(R.id.text_holiday_national)
  FadeInTextView textHolidayNational;
  @InjectView(R.id.text_date)
  TextView textDate;
  @InjectView(R.id.text_old_month)
  TextView textOldMonth;

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

    // set the custom fonts
    textHoliday.setCustomFont(this, "fonts/kapak.otf");
    textHolidayNational.setCustomFont(this, "fonts/kapak.otf");

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
    selectedDay = calendar.get(Calendar.DAY_OF_YEAR) - 1;
    pager.setCurrentItem(selectedDay);
    onPageSelected(selectedDay);

    // add the fab favorite click listener
    fab.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {

        // toggle the favorite status
        setFavorite(selectedDay, !isFavorite(selectedDay));
        setupButtons(null);
      }
    });
  }

  @Override
  public void onError(Exception ex) {

    ex.printStackTrace();
    // TODO show the error
  }

  @Override
  public void onPaletteReady(Palette palette) {

    setupButtons(palette);
  }

  @Override
  public void togglePanel() {

    if (panel.isPanelExpanded()) {
      panel.collapsePanel();
    }
    else {
      panel.expandPanel();
    }
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
    // change the fab colors based on the
    // dominant orthodox image colors
    try {
      OrthodoxFragment orthodoxFragment = ((OrthodoxPagerAdapter) pager.getAdapter()).getFragment(pager.getCurrentItem());
      setupButtons(orthodoxFragment.getPalette());
    }
    catch (NullPointerException e) {
      // the palette is not ready yet
      // note but ignore the exception
      Log.d("Pravoslaven", "Palette unavailable");
    }
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

  private void setupButtons(Palette palette) {

    // set the fab favorite status
    if (isFavorite(selectedDay)) {
      fab.setImageResource(R.drawable.ic_action_favorite);
    }
    else {
      fab.setImageResource(R.drawable.ic_action_favorite_outline);
    }

    if (palette == null || palette.getSwatches().size() < 2) {
      return;
    }
    // get the two dominant colors from the palette
    int firstColor = palette.getSwatches().get(0).getRgb();
    int secondColor = palette.getSwatches().get(1).getRgb();

    // select the darker of the two as a base for the fab
    int baseColor;
    if (firstColor < secondColor) {
      baseColor = firstColor;
    }
    else {
      baseColor = secondColor;
    }

    // the pressed and ripple colors are lighter variants of the
    // darker dominant color
    // factor < 1.0f == darken
    // factor > 1.0f == lighten
    final int pressedColor = MathUtils.shade(baseColor, 1.2f);
    int rippleColor = MathUtils.shade(baseColor, 1.6f);
    // set the fab color accordingly
    fab.setColorNormal(baseColor);
    fab.setColorPressed(pressedColor);
    fab.setColorRipple(rippleColor);

    // select the menu buttons touch listener
    // there are a lot more elegant ways of doing this
    // but we are supporting API 10 so we need a workaround
    View.OnTouchListener touchListener = new View.OnTouchListener() {

      @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
      @Override
      public boolean onTouch(View view, MotionEvent event) {

        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN:
            // focus the button click based on the palette pressed color

            ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
            drawable.setIntrinsicHeight(view.getHeight());
            drawable.setIntrinsicWidth(view.getHeight());
            drawable.setBounds(new Rect(0, 0, view.getHeight(), view.getHeight()));
            drawable.getPaint().setColor(pressedColor);

            if (SystemUtils.versionAtLeast(Build.VERSION_CODES.JELLY_BEAN)) {
              view.setBackground(drawable);
            }
            else {
              view.setBackgroundDrawable(drawable);
            }
            break;
          case MotionEvent.ACTION_UP:
            // perform the click event
            switch (view.getId()) {
              case R.id.image_calendar:
                // show the calendar
                showCalendar();
                break;
              case R.id.image_fasting:
                // TODO open fasting list
                break;
              case R.id.image_wallpaper:
                // TODO set wallpaper
                break;
              case R.id.image_settings:
                // TODO open settings
                break;
            }
            // don't break here
            // up will trigger a click and background change
            // cancel will only trigger background change
          case MotionEvent.ACTION_CANCEL:
            view.setBackgroundColor(Color.TRANSPARENT);
            break;
        }
        return false;
      }
    };
    // set the touch listener
    imageCalendar.setOnTouchListener(touchListener);
    imageFasting.setOnTouchListener(touchListener);
    imageWallpaper.setOnTouchListener(touchListener);
    imageSettings.setOnTouchListener(touchListener);
  }

  private void showCalendar() {

    final CaldroidFragment calendarDialog = CaldroidFragment.newInstance(
            null,
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.YEAR));
    // set the min date
    Calendar minDate = Calendar.getInstance();
    minDate.set(Calendar.DAY_OF_YEAR, 1);
    minDate.set(Calendar.MONTH, Calendar.JANUARY);
    minDate.set(Calendar.YEAR, 2015);
    calendarDialog.setMinDate(minDate.getTime());
    // set the max date
    Calendar maxDate = Calendar.getInstance();
    maxDate.set(Calendar.DAY_OF_YEAR, 1);
    maxDate.set(Calendar.MONTH, Calendar.JANUARY);
    maxDate.set(Calendar.YEAR, 2016);
    calendarDialog.setMaxDate(maxDate.getTime());
    // set the start day of the week
    Bundle args = new Bundle();
    args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
    calendarDialog.setArguments(args);

    // add the on click listener
    calendarDialog.setCaldroidListener(new CaldroidListener() {

      @Override
      public void onSelectDate(Date date, View view) {
        // dismiss the calendar
        calendarDialog.dismiss();
        // scroll to the selected date
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.setTime(date);
        pager.setCurrentItem(selectedDate.get(Calendar.DAY_OF_YEAR) - 1, true);
      }

      @Override
      public void onChangeMonth(final int month, final int year) {

        // we need a bit of delayed execution
        // since the listener will override our changes
        // when this call ends
        calendarDialog.getMonthTitleTextView().postDelayed(new Runnable() {

          @Override
          public void run() {
            // set the month and year as calendar title
            String monthName = getResources().getStringArray(R.array.months)[month - 1];
            calendarDialog.getMonthTitleTextView().setText(monthName + " " + year);
          }
        }, 1);
      }

      @Override
      public void onCaldroidViewCreated() {

        super.onCaldroidViewCreated();
        // set custom day of week names
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(
                MainActivity.this,
                R.layout.item_calendar_day,
                getResources().getStringArray(R.array.days_of_week));
        calendarDialog.getWeekdayGridView().setAdapter(itemsAdapter);
      }
    });

    // show the dialog
    calendarDialog.show(getSupportFragmentManager(), "Pravoslaven");
  }

  private void setupDrawer() {

    // get the day and all the holidays
    final OrthodoxDay orthodoxDay = orthodoxDays.get(selectedDay);
    final OrthodoxHoliday orthodoxHolidayMajor = orthodoxDay.getHolidays().get(0);

    // set the date
    calendar.set(Calendar.DAY_OF_YEAR, selectedDay + 1);
    textDate.setText(simpleDateFormat.format(calendar.getTime()));
    textOldMonth.setText(oldMonths[calendar.get(Calendar.MONTH)]);

    // set the day of week
    clearDayOfWeekColor();
    switch (calendar.get(Calendar.DAY_OF_WEEK)) {
      case Calendar.MONDAY:
        textMonday.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
        break;
      case Calendar.TUESDAY:
        textTuesday.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
        break;
      case Calendar.WEDNESDAY:
        textWednesday.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
        break;
      case Calendar.THURSDAY:
        textThursday.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
        break;
      case Calendar.FRIDAY:
        textFriday.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
        break;
      case Calendar.SATURDAY:
        textSaturday.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
        break;
      case Calendar.SUNDAY:
        textSunday.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
        break;
    }

    // set the major holiday
    textHoliday.initSpanText(orthodoxHolidayMajor.getName(), getResources().getColor(android.R.color.primary_text_light));
    textHoliday.animateText();
    textHoliday.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {

        // open the holiday description in browser
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(orthodoxHolidayMajor.getDescriptionUrl())));
      }
    });

    // set the national holiday if any
    if (orthodoxDay.getNationalHoliday() != null) {
      textHolidayNational.setVisibility(View.VISIBLE);
      textHolidayNational.initSpanText(orthodoxDay.getNationalHoliday(), getResources().getColor(android.R.color.tertiary_text_light));
      textHolidayNational.animateText();
    }
    else {
      textHolidayNational.setVisibility(View.INVISIBLE);
    }

    int padding = getResources().getDimensionPixelSize(R.dimen.padding_normal);
    // clean the previous items if needed
    LinearLayout linearFasting = (LinearLayout) drawer.findViewById(R.id.linear_fasting);
    linearFasting.removeAllViews();
    // set the fasting foods with their appropriate icon
    for (String fastingFood : orthodoxDay.getFastingFoods()) {
      TextView textFasting = new TextView(this);
      textFasting.setGravity(Gravity.CENTER);
      textFasting.setPadding(padding, 0, padding, padding);
      textFasting.setText(fastingFood);
      switch (fastingFood.toLowerCase()) {
        case "води":
          textFasting.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_water, 0, 0);
          break;
        case "масло":
          textFasting.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_oil, 0, 0);
          break;
        case "вино":
          textFasting.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_wine, 0, 0);
          break;
        case "строг пост":
          textFasting.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_fasting, 0, 0);
          break;
        case "без пост":
          textFasting.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_meat, 0, 0);
          break;
        case "риба":
          textFasting.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_fish, 0, 0);
          break;
        case "млечни":
          textFasting.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_dairy, 0, 0);
          break;
      }
      linearFasting.addView(textFasting);
    }

    // set the other holidays if available
    if (orthodoxDay.getHolidays().size() > 1) {
      // clean the previous items if needed
      LinearLayout linearHolidays = (LinearLayout) drawer.findViewById(R.id.linear_items);
      linearHolidays.removeAllViews();

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
        textOrthodoxHoliday.setPadding(padding, 0, padding, padding);

        textOrthodoxHoliday.setText(orthodoxHolidayMinor.getName());
        textOrthodoxHoliday.setOnClickListener(new View.OnClickListener() {

          @Override
          public void onClick(View v) {
            // open the holiday description in browser
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(orthodoxHolidayMinor.getDescriptionUrl())));
          }
        });
        linearHolidays.addView(textOrthodoxHoliday);
      }
    }
  }

  private void clearDayOfWeekColor() {

    textMonday.setTextColor(getResources().getColor(android.R.color.secondary_text_dark));
    textTuesday.setTextColor(getResources().getColor(android.R.color.secondary_text_dark));
    textWednesday.setTextColor(getResources().getColor(android.R.color.secondary_text_dark));
    textThursday.setTextColor(getResources().getColor(android.R.color.secondary_text_dark));
    textFriday.setTextColor(getResources().getColor(android.R.color.secondary_text_dark));
    textSaturday.setTextColor(getResources().getColor(android.R.color.secondary_text_dark));
    textSunday.setTextColor(getResources().getColor(android.R.color.secondary_text_dark));
  }
}
