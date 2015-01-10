package com.medo.pravoslavenkalendar.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.medo.pravoslavenkalendar.callbacks.JsonCallbacks;
import com.medo.pravoslavenkalendar.model.OrthodoxDay;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;


public class JsonUtils {

  public static void parseCalendar(final Context context, final String assetsName, final JsonCallbacks jsonCallbacks) {

    // load the calendar
    // initialize the GSON parser and prepare the calendar list
    Gson gson = new Gson();
    List<OrthodoxDay> orthodoxDays;
    try {
      // get the input stream reader for the assets json file that contains the calendar
      InputStream inputStream = context.getAssets().open(assetsName);
      InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
      // parse the json file to create the list of orthodox days
      orthodoxDays = gson.fromJson(new JsonReader(inputStreamReader), new TypeToken<List<OrthodoxDay>>() {}.getType());
      jsonCallbacks.onCalendarReady(orthodoxDays);
    }
    catch (Exception e) {
      // an error occurred, propagate it to the UI for handling
      jsonCallbacks.onError(e);
    }
  }
}
