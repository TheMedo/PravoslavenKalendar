package com.medo.pravoslavenkalendar.receivers;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.RemoteViews;

import com.medo.pravoslavenkalendar.MainActivity;
import com.medo.pravoslavenkalendar.R;
import com.medo.pravoslavenkalendar.services.OrthodoxWidgetService;
import com.medo.pravoslavenkalendar.utils.Extras;
import com.medo.pravoslavenkalendar.utils.SystemUtils;

import java.util.Calendar;


public class OrthodoxWidgetProvider extends AppWidgetProvider {

  public static final String TOAST_ACTION = "com.medo.pravoslavenkalendar.TOAST_ACTION";
  public static final String EXTRA_ITEM = "com.medo.pravoslavenkalendar.EXTRA_ITEM";

  private static HandlerThread sWorkerThread;
  private static Handler sWorkerQueue;

  public OrthodoxWidgetProvider() {

    sWorkerThread = new HandlerThread("OrthodoxWidgetProviderWorker");
    sWorkerThread.start();
    sWorkerQueue = new Handler(sWorkerThread.getLooper());
  }

  // Called when the BroadcastReceiver receives an Intent broadcast.
  // Checks to see whether the intent's action is TOAST_ACTION. If it is, the app widget
  // displays a Toast message for the current item.
  @Override
  public void onReceive(Context context, Intent intent) {

    if (intent.getAction().equals(TOAST_ACTION)) {
      int viewIndex = intent.getIntExtra(EXTRA_ITEM, Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - 1);

      Intent mainIntent = new Intent(context, MainActivity.class);
      mainIntent.putExtra(Extras.EXTRA_DAY, viewIndex);
      mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(mainIntent);
    }
    super.onReceive(context, intent);
  }

  @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
  public void onUpdate(Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {

    // no widget for lower APIs
    if (!SystemUtils.versionAtLeast(Build.VERSION_CODES.ICE_CREAM_SANDWICH)) {
      return;
    }

    // perform this loop procedure for each App Widget that belongs to this provider
    for (final int appWidgetId : appWidgetIds) {
      // Set up the intent that starts the StackViewService, which will
      // provide the views for this collection.
      Intent intent = new Intent(context, OrthodoxWidgetService.class);
      // Add the app widget ID to the intent extras.
      intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
      intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
      // Instantiate the RemoteViews object for the app widget layout.
      final RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
      // Set up the RemoteViews object to use a RemoteViews adapter.
      // This adapter connects
      // to a RemoteViewsService  through the specified intent.
      // This is how you populate the data.
      rv.setRemoteAdapter(appWidgetId, R.id.list_view, intent);

      // The empty view is displayed when the collection has no items.
      // It should be in the same layout used to instantiate the RemoteViews
      // object above.
      rv.setEmptyView(R.id.list_view, R.id.empty_view);

      // This section makes it possible for items to have individualized behavior.
      // It does this by setting up a pending intent template. Individuals items of a collection
      // cannot set up their own pending intents. Instead, the collection as a whole sets
      // up a pending intent template, and the individual items set a fillInIntent
      // to create unique behavior on an item-by-item basis.
      Intent toastIntent = new Intent(context, OrthodoxWidgetProvider.class);
      // Set the action for the intent.
      // When the user touches a particular view, it will have the effect of
      // broadcasting TOAST_ACTION.
      toastIntent.setAction(OrthodoxWidgetProvider.TOAST_ACTION);
      toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
      intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
      PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
      rv.setPendingIntentTemplate(R.id.list_view, toastPendingIntent);

      // a workaround so we can scroll the widget
      // to the current day
      // http://stackoverflow.com/questions/25618733/android-remoteviews-listview-scroll
      sWorkerQueue.postDelayed(new Runnable() {

        @Override
        public void run() {

          rv.setScrollPosition(R.id.list_view, Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - 1);
          appWidgetManager.partiallyUpdateAppWidget(appWidgetId, rv);
        }
      }, 3000);

      appWidgetManager.updateAppWidget(appWidgetId, rv);
    }
  }
}
