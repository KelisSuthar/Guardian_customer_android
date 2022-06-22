package com.app.guardian

import android.app.IntentService
import android.content.Intent
import android.util.Log

class BackgroundService(val name: String?) : IntentService(name) {
    override fun onHandleIntent(intent: Intent?) {
        for (i in 1..1000)
        {
            Log.e("GUARDIAN_APP",":::::::::::::::::::::::::::::::::::::::::::"+intent!!.getStringExtra("isNetworkConnected"))
            Log.e("GUARDIAN_APP", ":::::::::::::::::::::::::::::::::::::::::::$name")
        }

    }
}