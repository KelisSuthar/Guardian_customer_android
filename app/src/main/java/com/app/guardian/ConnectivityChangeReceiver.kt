package com.app.guardian

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import com.app.guardian.common.ReusedMethod


class ConnectivityChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
//        val i = Intent(context, BackgroundService::class.java)
//        context!!.stopService(intent)
//        i.putExtra("isNetworkConnected", ReusedMethod.isNetworkConnected(context))
        if (ReusedMethod.isNetworkConnected(context!!)) {
//            context.startService(i)
            Log.e(
                "GUARDIAN_APP",
                ":::::::::::::::::::::::::::::::::::::::::::" + "ON_RECEIVE" + "Connected"
            )
        } else {
            Log.e(
                "GUARDIAN_APP",
                ":::::::::::::::::::::::::::::::::::::::::::" + "ON_RECEIVE" + "NOT Connected"
            )
//            context.startService(i)
        }
//        val comp = ComponentName(
//            context!!.packageName,
//            BackgroundService::class.java.name
//        )
//        intent?.putExtra("isNetworkConnected", isConnected(context))
//        context.startService(Intent(context, BackgroundService::class.java))
//        context.startService(intent!!.setComponent(comp))
        Log.e(
            "GUARDIAN_APP",
            ":::::::::::::::::::::::::::::::::::::::::::" + "ON_RECEIVE"
        )
    }

    fun isConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isAvailable && networkInfo.isConnected
    }
}