package ch.kinlo.timesheetdx

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider

import android.app.PendingIntent

import android.os.Build

import android.content.Context
import android.content.Intent
import android.content.ComponentName

import android.graphics.Canvas
import android.graphics.Bitmap

import android.preference.PreferenceManager

import android.widget.RemoteViews


import android.util.Log

import androidx.core.content.ContextCompat

class TimesheetAppWidgetProvider : AppWidgetProvider() {
  private fun onUpdate(context: Context) {
    val manager = AppWidgetManager.getInstance(context)
    val component = ComponentName(context.getPackageName(), TimesheetAppWidgetProvider::class.java.name)
    val ids = manager.getAppWidgetIds(component);
    onUpdate(context, manager, ids);
  }
  override fun onUpdate(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetIds: IntArray,
  ) {
    appWidgetIds.forEach { appWidgetId ->
      val toggleTaskIntent: Intent = Intent(context, TimesheetReceiver::class.java)
      toggleTaskIntent.setAction("TOGGLE_TASK")

      val toggleTaskPendingIntent: PendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        toggleTaskIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

      val nextTaskIntent: Intent = Intent(context, TimesheetReceiver::class.java)
      nextTaskIntent.setAction("NEXT_TASK")
      
      val nextTaskPendingIntent: PendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        nextTaskIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

      val prevTaskIntent: Intent = Intent(context, TimesheetReceiver::class.java)
      prevTaskIntent.setAction("PREV_TASK")
      
      val prevTaskPendingIntent: PendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        prevTaskIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        
      val appPendingIntent: PendingIntent = PendingIntent.getActivity(context, 0,
        Intent(context, TimesheetActivity::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)


      // Get the layout for the widget and attach an on-click listener
      // to the button.
      val views: RemoteViews = RemoteViews(context.packageName, R.layout.app_widget)
        .apply {
          setOnClickPendingIntent(R.id.select_task, toggleTaskPendingIntent)
          setOnClickPendingIntent(R.id.next_task, nextTaskPendingIntent)
          setOnClickPendingIntent(R.id.prev_task, prevTaskPendingIntent)
          setOnClickPendingIntent(R.id.current_task, appPendingIntent)
        }
        //val views = RemoteViews(context.getPackageName(), R.layout.app_widget)
      
      
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        views.setImageViewResource(R.id.prev_task, R.drawable.baseline_expand_less_36);
        views.setImageViewResource(R.id.next_task, R.drawable.baseline_expand_more_36);
      } else {
        val bum = listOf(
            Pair(R.id.prev_task, R.drawable.baseline_expand_less_36),
            Pair(R.id.next_task, R.drawable.baseline_expand_more_36),
          )
        for (p in bum) {
          var d = ContextCompat.getDrawable(context, p.second)!!
          var b = Bitmap.createBitmap(d.getIntrinsicWidth(),
                                      d.getIntrinsicHeight(),
                                         Bitmap.Config.ARGB_8888)
          var c = Canvas(b)
          d.setBounds(0, 0, c.getWidth(), c.getHeight())
          d.draw(c)
          views.setImageViewBitmap(p.first, b)
        }
      }

      
      val db = TimesheetDatabase(context)
      val prefs = PreferenceManager.getDefaultSharedPreferences(context)
      
      var taskId = prefs.getLong("app_task", -1)
      val currentId = db.getCurrentTaskId()
      
      if (taskId == -1L || !db.isValidTask(taskId)) {
        if (currentId == 0L) {
          taskId = db.getFirstTaskId(prefs.getBoolean("alphabetise_tasks", false))
        } else {
          taskId = currentId
        }
        val edit = prefs.edit()
        edit.putLong("app_task", taskId)
        edit.commit()
      }
      
      
      if (taskId == currentId && taskId > 0L) {
        views.setImageViewResource(R.id.select_task, R.drawable.vert_toggle_on)
      } else {
        views.setImageViewResource(R.id.select_task, R.drawable.vert_toggle_off)
      }
      
      views.setTextViewText(R.id.current_task, db.getTaskName(taskId))
      
      db.close()

      // Tell the AppWidgetManager to perform an update on the current
      // widget.
      appWidgetManager.updateAppWidget(appWidgetId, views)
    }
  }
  
  override fun onReceive(context: Context, intent: Intent) {
    super.onReceive(context, intent)
    onUpdate(context)
  }
  
  override fun onEnabled(context: Context) {
  }
}
