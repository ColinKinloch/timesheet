package ch.kinlo.timesheetdx

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import android.util.Log

class TimesheetReceiver: BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    val action = intent.getAction()
    Log.v("YERBB", "hohoho $action")
  }
}
