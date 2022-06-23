package com.app.guardian

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class BackgroundService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
        for (i in 1..1000) {
            Log.e(
                "GUARDIAN_APP",
                ":::::::::::::::::::::::::::::::::::::::::::" + intent!!.getStringExtra("isNetworkConnected")
            )
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}