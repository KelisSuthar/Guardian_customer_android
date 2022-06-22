package com.app.guardian.notification

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.Login.LoginActivity
import com.app.guardian.ui.SubscriptionPlan.SubScriptionPlanScreen
import com.app.guardian.utils.ApiConstant
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import java.util.*


class MyFirebaseMessagingService : FirebaseMessagingService() {
    var body: String? = null
    var title: String? = null
    var intent: Intent? = null

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.e("Firebase_Token", "= $s")
        SharedPreferenceManager.putString(ApiConstant.EXTRAS_DEVICETOKEN, s)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val data = JSONObject(Objects.requireNonNull(remoteMessage.data) as Map<*, *>)

        Log.e("FireBase", remoteMessage.notification!!.title.toString())
        var getDataSize = remoteMessage.data.size
        HomeActivity.bage_counter_notification = HomeActivity.bage_counter_notification + 1
        Log.e(
            "bage counter",
            "Bage Counter count :" + HomeActivity.bage_counter_notification.toString()
        )
        val count = SharedPreferenceManager.getInt(AppConstants.NOTIFICATION_BAGE,0)
        SharedPreferenceManager.putInt(AppConstants.NOTIFICATION_BAGE,
            count+1
        )
        triggerBroadcastToActivity(this, HomeActivity.bage_counter_notification)

        sendNotification(data, remoteMessage)
        sendBroadcastData(data, remoteMessage)
    }

    fun sendBroadcastData(data: JSONObject, remoteMessage: RemoteMessage) {

        body = remoteMessage.notification?.body
        title = remoteMessage.notification?.title

        val sound = data.optString("sound")
        val color = data.optString("color")
        val type = data.optString("type")
        val is_approve = data.optString("is_approve")
        val intent = Intent(AppConstants.BROADCAST_REC_INTENT)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("notificationData", data.toString())
        intent.putExtra("title", title)
        intent.putExtra("type", type)
        intent.putExtra("body", body)
        intent.putExtra("is_approve", is_approve)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    @SuppressLint("WrongConstant", "InvalidWakeLockTag", "RemoteViewLayout")
    private fun sendNotification(data: JSONObject, remoteMessage: RemoteMessage) {
        Log.i("FIREBASE_DATA", data.toString())

        if (!SharedPreferenceManager.getBoolean(AppConstants.IS_LOGIN, false)) {

            intent =
                Intent(
                    this,

                    LoginActivity::class.java
                )

        } else if (!SharedPreferenceManager.getBoolean(AppConstants.IS_SUBSCRIBE, false)) {
            intent =
                Intent(
                    this,

                    SubScriptionPlanScreen::class.java
                )

        } else {
            intent =
                Intent(this, HomeActivity::class.java).putExtra(
                    AppConstants.IS_NOTIFICATION, true
                ).putExtra(
                    AppConstants.EXTRA_NOTIFICATION_DATA, data.getString("type")
                )

        }
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)





        val channelId = getString(com.app.guardian.R.string.app_name)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0 /* Request code */,
            intent,
            PendingIntent.FLAG_IMMUTABLE

        )
        val notificationLayout = RemoteViews(
            applicationContext.packageName,
            R.layout.notification_layout
        )
        notificationLayout.setTextViewText(
            R.id.txtNotificationTitle,
            remoteMessage.notification!!.title
        )
        notificationLayout.setTextViewText(
            R.id.txtNotificationBody,
            remoteMessage.notification!!.body
        )
        if (data.optString("type") == AppConstants.EXTRA_MEDIATOR_PAYLOAD) {
            notificationLayout.setImageViewResource(R.id.imgChat, R.drawable.ic_notification_video)
        } else if (data.optString("type") == AppConstants.EXTRA_CHAT_MESSAGE_PAYLOAD) {
            notificationLayout.setImageViewResource(R.id.imgChat, R.drawable.ic_notification_chat)
        } else if (data.optString("type") == AppConstants.EXTRA_VIRTUAL_WITNESS_PAYLOAD) {
            notificationLayout.setImageViewResource(R.id.imgChat, R.drawable.ic_notification_video)
        }


        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setPriority(Notification.PRIORITY_MAX)
            .setDefaults(Notification.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(remoteMessage.notification!!.title)
            .setContentText(remoteMessage.notification!!.body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationBuilder.setCustomHeadsUpContentView(notificationLayout)

//        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

//        val pm:PowerManager = this.getSystemService(POWER_SERVICE) as PowerManager
//        val isScreenOn = pm.isScreenOn
//        if (!isScreenOn) {
//            val wl: PowerManager.WakeLock = pm.newWakeLock(
//                PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
//                "MyLock"
//            )
//            wl.acquire(10000)
//            val wl_cpu: PowerManager.WakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock")
//            wl_cpu.acquire(10000)
//        }

        // Since android Oreo notification channel is needed.

        // Get the layouts to use in the custom notification


// Apply the layouts to the notification


        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(channel)
        }
        val notifId = System.currentTimeMillis().toInt()
        notificationManager.notify(notifId, notificationBuilder.build())
    }

    fun isAppIsInBackground(context: Context): Boolean {
        var isInBackground = true
        val am =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            val runningProcesses =
                am.runningAppProcesses
            for (processInfo in runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (activeProcess in processInfo.pkgList) {
                        if (activeProcess == context.packageName) {
                            isInBackground = false
                        }
                    }
                }
            }
        } else {
            val taskInfo = am.getRunningTasks(1)
            val componentInfo = taskInfo[0].topActivity
            if (componentInfo!!.packageName == context.packageName) {
                isInBackground = false
            }
        }
        return isInBackground
    }

    private fun triggerBroadcastToActivity(context: Context, datavalue: Int) {
        val intent = Intent(HomeActivity.intentAction)
        intent.putExtra("data", datavalue)
        intent.putExtra("code", "000")
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }
}