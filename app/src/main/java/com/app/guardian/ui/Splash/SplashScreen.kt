package com.app.guardian.ui.Splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.AppConstants.IS_NOTIFICATION
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.databinding.ActivitySplashScreenBinding
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.Login.LoginActivity
import com.app.guardian.ui.SubscriptionPlan.SubScriptionPlanScreen
import com.app.guardian.ui.videocalljoin.VideoCallJoinActivity
import com.app.guardian.utils.ApiConstant
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject

class SplashScreen : AppCompatActivity(), View.OnClickListener {
    lateinit var mBinding: ActivitySplashScreenBinding
    var notification_type = ""
    var notification_id = ""
    var notification_URL = ""
    var notification_meeting_id = ""
    var DEVICE_TOKEN = SharedPreferenceManager.getString(ApiConstant.EXTRAS_DEVICETOKEN, "")

//
//    override fun getResource(): Int {
//        ReusedMethod.updateStatusBarColor(this, R.color.colorTransparent, 0)
//        return R.layout.activity_splash_screen
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ReusedMethod.updateStatusBarColor(this, R.color.colorTransparent, 0)
        mBinding = ActivitySplashScreenBinding.inflate(layoutInflater)
        val view = mBinding.root
        setContentView(view)

        initView()


    }


//    //use for new screen open according to which notification are come
//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        Log.e(
//            "NOTIFICATION_SPLASh",
//            "onNewIntent call"
//        )
//
//        val i = getIntent()
//        val extras = i.extras
//        if (extras != null) {
//            for (key in extras.keySet()) {
//                val value = extras[key]
//                Log.d(
//                    "Notification",
//                    "Extras received at onNewIntent splash onNewIntent :  Key: $key Value: $value"
//                )
//            }
//            val title = extras.getString("title")
//            val message = extras.getString("body")
//            if (message != null && message.length > 0) {
//                getIntent().removeExtra("body")
//            }
//        }
//        setIntent(intent)
//        val getIntent = intent
//    }

    @SuppressLint("LogNotTimber")
    private fun initView() {
        Log.e(
            "NOTIFICATION_SPLASh",
            "initView call"
        )

        val i = intent
        val extras = i.extras
        if (extras != null) {
            for (key in extras.keySet()) {
                val value = extras[key]
                Log.d(
                    "Notification",
                    "Extras received at initView splash:  Key: $key Value: $value"
                )
            }

            notification_type =
                intent.extras?.getString("type").toString()
            notification_id =
                intent.extras?.getString("sender_id").toString()
            notification_URL =
                intent.extras?.getString("url").toString()
            notification_meeting_id =
                intent.extras?.getString("room_id").toString()
        }

        Log.i(
            "NOTIFICATION_INIT",
            notification_type.toString()
        )

        Log.i(
            "NOTIFICATION_INIT",
            notification_id.toString()
        )

        Handler().postDelayed({
            finish()
            if (!SharedPreferenceManager.getBoolean(AppConstants.IS_LOGIN, false)) {
                startActivity(
                    Intent(
                        this@SplashScreen,
                        LoginActivity::class.java
                    )
                )
            } else if (!SharedPreferenceManager.getBoolean(AppConstants.IS_SUBSCRIBE, false)) {
                startActivity(
                    Intent(
                        this@SplashScreen,

                        SubScriptionPlanScreen::class.java
                    )
                )
            } else {
                if (intent != null && intent.extras != null) {
                    if (notification_type != AppConstants.EXTRA_VIDEOCALLREQ_PAYLOAD) {
                        startActivity(
                            Intent(
                                this@SplashScreen,

                                HomeActivity::class.java
                            ).putExtra(
                                "type", notification_type
                            )
                                .putExtra(
                                    "sender_id", notification_id
                                )
                                .putExtra(
                                    IS_NOTIFICATION, true
                                )

                        )
                    } else {
                        when (SharedPreferenceManager.getLoginUserRole()) {
                            AppConstants.APP_ROLE_LAWYER -> {

                            }
                            AppConstants.APP_ROLE_USER -> {
                                joinMeeting(notification_meeting_id)
                            }
                            AppConstants.APP_ROLE_MEDIATOR -> {

                            }

                        }
                    }
                } else {
                    startActivity(
                        Intent(
                            this@SplashScreen,
                            HomeActivity::class.java

                        )
                    )
                }
            }
        }, 2000)
    }

    override fun onResume() {
        super.onResume()
        if (DEVICE_TOKEN.isNullOrEmpty() || DEVICE_TOKEN == "") {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FirebaseToken", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                // Get new FCM registration token
                val token = task.result
                SharedPreferenceManager.putString(ApiConstant.EXTRAS_DEVICETOKEN, token)
                DEVICE_TOKEN = SharedPreferenceManager.getString(ApiConstant.EXTRAS_DEVICETOKEN, "")
                Log.e("FinalGeneratedToken", "=$token")
            })
        } else

            Log.e("StoredDeviceToken", DEVICE_TOKEN.toString())

    }


    override fun onClick(p0: View?) {

    }

    private fun joinMeeting(meeting_Id: String) {
        AndroidNetworking.post("https://api.videosdk.live/v1/meetings/$meeting_Id")
            .addHeaders("Authorization", resources.getString(R.string.video_call_auth))
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    val meetingId = response.getString("meetingId")
                    Log.e("VIDEO_CALL", "JOIN_MEATING_RESP:    $response")
                    val intent =
                        Intent(this@SplashScreen, VideoCallJoinActivity::class.java)
                    intent.putExtra("token", resources.getString(R.string.video_call_auth))
                    intent.putExtra("meetingId", meetingId)
                    intent.putExtra(
                        AppConstants.EXTRA_NAME,
                        SharedPreferenceManager.getUser()?.full_name
                    )
                    intent.putExtra(
                        AppConstants.IS_JOIN,
                        true
                    )
                    intent.putExtra(
                        AppConstants.EXTRA_URL,
                        "https://api.videosdk.live/v1/meetings/$meetingId"
                    )
                    intent.putExtra(AppConstants.EXTRA_ROOM_ID, meetingId)
                    startActivity(intent)
                    finish()

                }

                override fun onError(anError: ANError) {
                    anError.printStackTrace()
                    ReusedMethod.displayMessage(
                        this@SplashScreen,
                        anError.message.toString()
                    )
                    ReusedMethod.displayMessage(this@SplashScreen, "Your Token Has Expired")
                    Log.e("VIDEO_CALL", "JOIN_MEATING_ERRRO:    " + anError.errorBody)

                }
            })
    }
}