package com.medo.pravoslavenkalendar.services;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.medo.pravoslavenkalendar.R;
import com.medo.pravoslavenkalendar.model.OrthodoxDay;
import com.medo.pravoslavenkalendar.receivers.OrthodoxWidgetProvider;
import com.medo.pravoslavenkalendar.utils.JsonUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class OrthodoxWidgetService extends RemoteViewsService {

  @Override
  public RemoteViewsFactory onGetViewFactory(Intent intent) {

    return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
  }
}


class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

  private List<OrthodoxDay> items = new ArrayList<>();
  private Context context;
  private Calendar calendar;
  private SimpleDateFormat simpleDateFormat;
  private String[] daysOfWeek;

  public ListRemoteViewsFactory(Context context, Intent intent) {

    this.context = context;
  }

  // Initialize the data set.
  public void onCreate() {
    // In onCreate() you set up any connections / cursors to your data source. Heavy lifting,
    // for example downloading or creating content etc, should be deferred to onDataSetChanged()
    // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
    this.calendar = Calendar.getInstance();
    this.simpleDateFormat = new SimpleDateFormat("dd.MM");
    this.daysOfWeek = context.getResources().getStringArray(R.array.days_of_week);
  }

  @Override
  public void onDataSetChanged() {

    items = JsonUtils.parseCalendar(context);
  }

  @Override
  public void onDestroy() {

    items.clear();
  }

  @Override
  public int getCount() {

    return items.size();
  }

  // Given the position (index) of a WidgetItem in the array, use the item's text value in
  // combination with the app widget item XML file to construct a RemoteViews object.
  public RemoteViews getViewAt(int position) {
    // position will always range from 0 to getCount() - 1.
    OrthodoxDay item = items.get(position);
    calendar.set(Calendar.DAY_OF_YEAR, item.getDayOfYear());

    // Construct a RemoteViews item based on the app widget item XML file, and set the
    // text based on the position.
    RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_item);
    rv.setTextViewText(R.id.text_date, simpleDateFormat.format(calendar.getTime()) + " (" + daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK) - 1] + ")");
    rv.setTextViewText(R.id.text_holiday, item.getHolidays().get(0).getName());
    rv.setTextViewText(R.id.text_holiday_national, item.getNationalHoliday() == null ? "" : item.getNationalHoliday());
    switch (item.getFastingFoods().get(0).toLowerCase()) {
      case "води":
        rv.setImageViewResource(R.id.image_fasting, R.drawable.ic_water);
        break;
      case "масло":
        rv.setImageViewResource(R.id.image_fasting, R.drawable.ic_oil);
        break;
      case "вино":
        rv.setImageViewResource(R.id.image_fasting, R.drawable.ic_wine);
        break;
      case "строг пост":
        rv.setImageViewResource(R.id.image_fasting, R.drawable.ic_fasting);
        break;
      case "без пост":
        rv.setImageViewResource(R.id.image_fasting, R.drawable.ic_meat);
        break;
      case "риба":
        rv.setImageViewResource(R.id.image_fasting, R.drawable.ic_fish);
        break;
      case "млечни":
        rv.setImageViewResource(R.id.image_fasting, R.drawable.ic_dairy);
        break;
    }

    // Next, set a fill-intent, which will be used to fill in the pending intent template
    // that is set on the collection view in StackWidgetProvider.
    Bundle extras = new Bundle();
    extras.putInt(OrthodoxWidgetProvider.EXTRA_ITEM, position);
    Intent fillInIntent = new Intent();
    fillInIntent.putExtras(extras);
    // Make it possible to distinguish the individual on-click
    // action of a given item
    rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent);

    // Return the RemoteViews object.
    return rv;
  }

  @Override
  public RemoteViews getLoadingView() {

    return null;
  }

  @Override
  public int getViewTypeCount() {

    return 1;
  }

  @Override
  public long getItemId(int position) {

    return 0;
  }

  @Override
  public boolean hasStableIds() {

    return false;
  }
}
