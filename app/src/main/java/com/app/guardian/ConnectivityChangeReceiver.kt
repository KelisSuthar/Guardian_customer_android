package com.app.guardian

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.app.guardian.common.ReusedMethod


class ConnectivityChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val i = Intent(context, BackgroundService::class.java)
        context!!.stopService(intent)
        i.putExtra("isNetworkConnected", ReusedMethod.isNetworkConnected(context))
        if (ReusedMethod.isNetworkConnected(context)) {
            context.startService(i)
        } else {
            context.startService(i)
        }

    }
}