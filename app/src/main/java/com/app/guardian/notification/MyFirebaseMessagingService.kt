package com.app.guardian.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.SharedPreferenceManager
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
        sendNotification(data,remoteMessage)
        sendBroadcastData(data,remoteMessage)
    }

    fun sendBroadcastData(data: JSONObject, remoteMessage: RemoteMessage) {
        if(data.length() !=0){
            title = data.getString("title")
            body = data.optString("body")
        }else{
            body = remoteMessage.notification?.body
            title = remoteMessage.notification?.title
        }
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

    @SuppressLint("WrongConstant", "InvalidWakeLockTag")
    private fun sendNotification(data: JSONObject, remoteMessage: RemoteMessage) {


//
//        val pendingIntent = PendingIntent.getActivity(
//            this,
//            0 /* Request code */,
//            intent,
//            PendingIntent.FLAG_ONE_SHOT
//        )
//        body = data.optString("body")
//        title = data.optString("title")
        val channelId = getString(R.string.app_name)
        //val uriObjectofYourAudioFile = Uri.parse("android.resource://" + baseContext.packageName + "/" + R.raw.ccp_afrikaans)
        if(data.length() !=0){
            title = data.getString("title")
            body = data.optString("body")
        }else{
            body = remoteMessage.notification?.body
            title = remoteMessage.notification?.title
        }
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setPriority(Notification.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            // .setSound(uriObjectofYourAudioFile)
//            .setContentIntent(pendingIntent)


        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(channel)
        }
        val notifId = System.currentTimeMillis().toInt()
        notificationManager.notify(notifId, notificationBuilder.build())
    }


}