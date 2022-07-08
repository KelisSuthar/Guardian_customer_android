package com.app.guardian

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Handler
import android.util.Log
import com.app.guardian.common.ReusedMethod
import com.app.guardian.shareddata.base.BaseActivity


class ConnectivityChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("ConnectivityChange", "TRUE")

    }

}