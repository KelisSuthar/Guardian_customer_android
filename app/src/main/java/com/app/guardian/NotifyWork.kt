package com.app.guardian

import android.content.Context
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.work.ListenableWorker.Result.success
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.app.guardian.shareddata.base.BaseActivity


class NotifyWork(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {

//        for (i in 1..100) {
//            Log.e(
//                "GUARDIAN_APP",
//                ":::::::::::::::::::::::::::::::::::::::::::"
//            )
////            Log.e("GUARDIAN_APP", ":::::::::::::::::::::::::::::::::::::::::::$flags")
////            Log.e("GUARDIAN_APP", ":::::::::::::::::::::::::::::::::::::::::::$startId")
//        }


        Log.e(
            "GUARDIAN_APP",
            ":::::::::::::::::::::::::::::::::::::::::::"
        )


        val thread: Thread = object : Thread() {
            override fun run() {
                runOnUiThread {
                    var handler = Handler()
                    var runnable: Runnable? = null
                    handler.postDelayed(Runnable {
                        handler.postDelayed(runnable!!, 2000)

                        Log.e(
                            "GUARDIAN_APP",
                            ":::::::::::::::::::::::::::::::::::::::::::"
                        )
                    }.also { runnable = it }, 0)
                }
            }
        }
        thread.start()

        return success()
    }


    companion object {
        const val NOTIFICATION_ID = "appName_notification_id"
        const val NOTIFICATION_NAME = "appName"
        const val NOTIFICATION_CHANNEL = "appName_channel_01"
        const val NOTIFICATION_WORK = "appName_notification_work"
    }
}