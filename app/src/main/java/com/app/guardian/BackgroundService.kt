package com.app.guardian

import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log


class BackgroundService: Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val extras = intent!!.extras
        for (i in 1..100) {
            Log.e(
                "GUARDIAN_APP",
                ":::::::::::::::::::::::::::::::::::::::::::" + intent!!.getStringExtra("isNetworkConnected")
            )
            Log.e(
                "GUARDIAN_APP",
                ":::::::::::::::::::::::::::::::::::::::::::" + extras!!.getString("isNetworkConnected")
            )
//            Log.e("GUARDIAN_APP", ":::::::::::::::::::::::::::::::::::::::::::$flags")
//            Log.e("GUARDIAN_APP", ":::::::::::::::::::::::::::::::::::::::::::$startId")
        }
        return super.onStartCommand(intent, flags, startId)
    }

}