package ch.kinlo.timesheetdx

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider

import android.app.PendingIntent

import android.os.Build

import android.content.Context
import android.content.Intent

import android.graphics.Canvas;
import android.graphics.Bitmap;

import android.widget.RemoteViews;

import android.util.Log

import androidx.core.content.ContextCompat

import androidx.work.WorkRequest

class TimesheetAppWidgetProviderK : AppWidgetProvider() {
  override fun onUpdate(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetIds: IntArray,
  ) {
  appWidgetIds.forEach { appWidgetId ->
    Log.v("YERBB", "goo");


    val intent: Intent = Intent(context, TimesheetReceiver::class.java)
    intent.setAction("YOLO")

    val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
      context,
      0,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    // Get the layout for the widget and attach an on-click listener
    // to the button.
    val views: RemoteViews = RemoteViews(context.packageName, R.layout.app_widget)
      .apply {
        setOnClickPendingIntent(R.id.select_task, pendingIntent)
      }
    
    
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      views.setImageViewResource(R.id.prev_task, R.drawable.baseline_expand_less_36);
      views.setImageViewResource(R.id.next_task, R.drawable.baseline_expand_more_36);
    } else {
      val bum = listOf(
          Pair(R.id.prev_task, R.drawable.baseline_expand_less_36),
          Pair(R.id.next_task, R.drawable.baseline_expand_more_36),
        )
      for (p in bum) {
        var d = ContextCompat.getDrawable(context, p.first)!!
        var b = Bitmap.createBitmap(d.getIntrinsicWidth(),
                                    d.getIntrinsicHeight(),
                                       Bitmap.Config.ARGB_8888)
        var c = Canvas(b)
        d.setBounds(0, 0, c.getWidth(), c.getHeight())
        d.draw(c)
        views.setImageViewBitmap(p.second, b)
      }
    }

    // Tell the AppWidgetManager to perform an update on the current
    // widget.
    appWidgetManager.updateAppWidget(appWidgetId, views)
  }

  }
  override fun onEnabled(context: Context) {
    Log.v("YERBB", "gugu")
  }
}
