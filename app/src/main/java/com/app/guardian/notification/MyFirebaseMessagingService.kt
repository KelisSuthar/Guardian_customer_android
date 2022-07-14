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
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.ReusedMethod.Companion.isAppIsInBackground
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.Splash.SplashScreen
import com.app.guardian.utils.ApiConstant
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import java.security.AccessController.getContext
import java.util.*


class MyFirebaseMessagingService : FirebaseMessagingService() {
    var body: String? = null
    var title: String? = null
    var intent: Intent? = null
    var countMessage: Int = 0
    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.e("Firebase_Token", "= $s")
        SharedPreferenceManager.putString(ApiConstant.EXTRAS_DEVICETOKEN, s)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val data = JSONObject(Objects.requireNonNull(remoteMessage.data) as Map<*, *>)
        System.out.println("bageCount onMessageReceived :" + data.toString());

        Log.e("bageCount", remoteMessage.notification!!.title.toString())
        var getDataSize = remoteMessage.data.size
        HomeActivity.bage_counter_notification = HomeActivity.bage_counter_notification + 1
        Log.e(
            "bage counter",
            "Bage Counter count :" + HomeActivity.bage_counter_notification.toString()
        )
        val count = SharedPreferenceManager.getInt(AppConstants.NOTIFICATION_BAGE, 0)
        Log.e("bageCount", "BageCount onMessageReceived : " + count.toString())
        Log.e("bageCount", "BageCount onMessageReceived  message: " + data.toString())
        Log.e(
            "bageCount",
            "BageCount onMessageReceived  HomeActivity count: " + HomeActivity.bage_counter_notification.toString()
        )

        SharedPreferenceManager.putInt(
            AppConstants.NOTIFICATION_BAGE,
            count + 1
        )
        val count1 = SharedPreferenceManager.getInt(AppConstants.NOTIFICATION_BAGE, 0)
        Log.e("bageCount", "BageCount onMessageReceived count1: " + count1.toString())

        triggerBroadcastToActivity(this, HomeActivity.bage_counter_notification)


        sendNotification(data, remoteMessage)
        sendBroadcastData(data, remoteMessage)
    }

    fun sendBroadcastData(data: JSONObject, remoteMessage: RemoteMessage) {
        val count = SharedPreferenceManager.getInt(AppConstants.NOTIFICATION_BAGE, 0)
        Log.e("bageCount", "BageCount sendBroadcastData : " + count.toString())

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
        intent.putExtra("type Value", type)
        intent.putExtra("body", body)
        intent.putExtra("data", count)
        intent.putExtra("is_approve", is_approve)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun createNotificationChannel(
        notificationChannelId: String,
        systemTrayNotificationName: String
    ) {
        Log.i("Tag", "createNotificationChannel " + Build.VERSION.SDK_INT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = systemTrayNotificationName
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(notificationChannelId, name, importance)
            notificationChannel.description = systemTrayNotificationName
            notificationChannel.setShowBadge(true)
            notificationChannel.canShowBadge()
            val notificationManager: NotificationManager = applicationContext.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    @SuppressLint(
        "WrongConstant", "InvalidWakeLockTag", "RemoteViewLayout",
        "UnspecifiedImmutableFlag"
    )
    private fun sendNotification(data: JSONObject, remoteMessage: RemoteMessage) {
        Log.i("FIREBASE_DATA", data.toString())
        Log.i("FIREBASE_DATA", data.getString("type"))
        Log.i("FIREBASE_DATA", data.getString("sender_id"))
        Log.i("FIREBASE_DATA", isAppIsInBackground(this).toString())

        val count = SharedPreferenceManager.getInt(AppConstants.NOTIFICATION_BAGE, 0)
        val channelId = getString(com.app.guardian.R.string.app_name)

        Log.e("bageCount", "BageCount sendNotification : " + count.toString())

        if (isAppIsInBackground(this)) {
            intent =
                Intent(this, com.app.guardian.ui.Splash.SplashScreen::class.java).putExtra(
                    AppConstants.IS_NOTIFICATION, true
                ).putExtra(
                    "type", data.getString("type").toString()
                ).putExtra(
                    "sender_id", data.getString("sender_id").toString()
                )
            intent!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent!!.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

        } else {
            SharedPreferenceManager.putString(
                AppConstants.IS_NOTIFICATION_SHARED_TYPE,
                data.getString("type").toString()
            )
            SharedPreferenceManager.putString(
                AppConstants.IS_NOTIFICATION_SHARED_ID,
                data.getString("sender_id")
            )
            intent =
                Intent(this, HomeActivity::class.java)
            intent!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent!!.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        // createNotificationChannel(channelId,remoteMessage.notification!!.title!!);

        val pendingIntent = PendingIntent.getActivity(
            this,
            0 /* Request code */,
            intent,
            PendingIntent.FLAG_MUTABLE

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
        if (data.getString("type") == AppConstants.EXTRA_MEDIATOR_PAYLOAD) {
            notificationLayout.setImageViewResource(R.id.imgChat, R.drawable.ic_notification_video)
        } else if (data.getString("type") == AppConstants.EXTRA_CHAT_MESSAGE_PAYLOAD) {
            notificationLayout.setImageViewResource(R.id.imgChat, R.drawable.ic_notification_chat)
        } else if (data.getString("type") == AppConstants.EXTRA_VIRTUAL_WITNESS_PAYLOAD) {
            notificationLayout.setImageViewResource(R.id.imgChat, R.drawable.ic_notification_video)
        } else if (data.getString("type") == AppConstants.EXTRA_VIDEOCALLREQ_PAYLOAD) {
            notificationLayout.setImageViewResource(R.id.imgChat, R.drawable.ic_notification_video)
        }

        triggerBroadcastToActivity(this, HomeActivity.bage_counter_notification)

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


// Apply the layouts to the notification


        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH,

                )
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(channel)
        }
        val notifId = System.currentTimeMillis().toInt()
        notificationManager.notify(notifId, notificationBuilder.build())
    }


    private fun triggerBroadcastToActivity(context: Context, datavalue: Int) {
        val intent = Intent(HomeActivity.intentAction)
        intent.putExtra("data", datavalue)
        intent.putExtra("code", "000")
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }
}