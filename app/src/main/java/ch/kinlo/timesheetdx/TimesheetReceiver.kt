package ch.kinlo.timesheetdx

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import android.preference.PreferenceManager

import android.widget.RemoteViews

import android.util.Log

class TimesheetReceiver: BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    val app = context.applicationContext
    
    val db = TimesheetDatabase(context)
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    
    var taskId = prefs.getLong("app_task", -1)
    val currentId = db.getCurrentTaskId()
    
    val action = intent.getAction()
    when (action) {
      "TOGGLE_TASK" -> {
        if (taskId == currentId && taskId > 0L) {
          db.completeCurrentTask()
        } else if (taskId > 0L) {
          db.changeTask(taskId)
        }
      }
      "NEXT_TASK" -> {
        var nextTaskId = -1L;
        val c = db.getTasks(prefs.getBoolean("alphabetise_tasks", false))
        while (!c.isAfterLast()) {
          val cTaskId = c.getLong(c.getColumnIndex("_id"))
          if (cTaskId == taskId) {
            if (c.isLast()) {
              c.moveToFirst()
              nextTaskId = c.getLong(c.getColumnIndex("_id"))
            } else {
              c.moveToNext()
              nextTaskId = c.getLong(c.getColumnIndex("_id"))
            }
            break
          }
          c.moveToNext()
        }
        val edit = prefs.edit()
        edit.putLong("app_task", nextTaskId)
        edit.commit()
      }
      "PREV_TASK" -> {
        var nextTaskId = -1L;
          val c = db.getTasks(prefs.getBoolean("alphabetise_tasks", false))
          while (!c.isAfterLast()) {
            val cTaskId = c.getLong(c.getColumnIndex("_id"))
            if (cTaskId == taskId) {
              if (c.isFirst()) {
                c.moveToLast()
                nextTaskId = c.getLong(c.getColumnIndex("_id"))
              } else {
                c.moveToPrevious()
                nextTaskId = c.getLong(c.getColumnIndex("_id"))
              }
              break
            }
            c.moveToNext()
          }
          val edit = prefs.edit()
          edit.putLong("app_task", nextTaskId)
          edit.commit()
      }
    }
    
    db.close()
    
    val replyIntent = Intent(context, TimesheetAppWidgetProviderK::class.java)
    
    context.sendBroadcast(replyIntent)
  }
}
