package com.app.guardian.ui.Splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.databinding.ActivitySplashScreenBinding
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.Login.LoginActivity
import com.app.guardian.ui.SubscriptionPlan.SubScriptionPlanScreen
import com.app.guardian.utils.ApiConstant
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class SplashScreen : AppCompatActivity(), View.OnClickListener {
    lateinit var mBinding: ActivitySplashScreenBinding
    var notification_type = ""
    var notification_id = ""
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


    //use for new screen open according to which notification are come
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.e(
            "NOTIFICATION_SPLASh",
            "onNewIntent call"
        )

        val i = getIntent()
        val extras = i.extras
        if (extras != null) {
            for (key in extras.keySet()) {
                val value = extras[key]
                Log.d(
                    "Notification",
                    "Extras received at onNewIntent splash onNewIntent :  Key: $key Value: $value"
                )
            }
            val title = extras.getString("title")
            val message = extras.getString("body")
            if (message != null && message.length > 0) {
                getIntent().removeExtra("body")
            }
        }
//        setIntent(intent)
//        val getIntent = intent
    }

    @SuppressLint("LogNotTimber")
    private fun initView() {
        Log.e(
            "NOTIFICATION_SPLASh",
            "initView call"
        )

        val i = getIntent()
        val extras = i.extras
        if (extras != null) {
            for (key in extras.keySet()) {
                val value = extras[key]
                Log.d(
                    "Notification",
                    "Extras received at initView splash:  Key: $key Value: $value"
                )
            }
            val title = extras.getString("title")
            val message = extras.getString("body")
            if (message != null && message.length > 0) {
                getIntent().removeExtra("body")
            }
            notification_type =
                intent.getStringExtra("type").toString()
            notification_id =
                intent.getStringExtra("sender_id").toString()
        }

//        mBinding = getBinding()
        if (intent != null && intent.extras != null) {
            notification_type =
                intent.getStringExtra("type").toString()
            notification_id =
                intent.getStringExtra("sender_id").toString()
        }
        Log.i(
            "NOTIFICATION_TYPE_INIT",
            notification_type.toString()
        )

        Log.i(
            "NOTIFICATION_ID_INIT",
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
                Log.i("NOTIFICATION_TYPE", notification_type)
                if (intent != null && intent.extras != null) {

                    if (notification_type == AppConstants.EXTRA_CHAT_MESSAGE_PAYLOAD) {
                        Log.i("NOTIFICATION_TYPE", notification_type)
                        startActivity(
                            Intent(
                                this@SplashScreen,

                                HomeActivity::class.java
                            ).putExtra(
                                AppConstants.EXTRA_NOTIFICATION_DATA_TYPE, notification_type
                            )
                                .putExtra(
                                    AppConstants.EXTRA_NOTIFICATION_DATA_ID, notification_id
                                )

                        )
                    } else {
                        startActivity(
                            Intent(
                                this@SplashScreen,

                                HomeActivity::class.java
                            ).putExtra(
                                AppConstants.EXTRA_NOTIFICATION_DATA_TYPE, notification_type
                            )
                                .putExtra(
                                    AppConstants.EXTRA_NOTIFICATION_DATA_ID, notification_id
                                )
                        )


//                        else if (intent.getStringExtra(AppConstants.EXTRA_NOTIFICATION_DATA_TYPE) == AppConstants.EXTRA_MEDIATOR_PAYLOAD) {
//                            Log.i(
//                                "NOTIFICATION_DATA",
//                                intent.getStringExtra(AppConstants.EXTRA_NOTIFICATION_DATA_TYPE)!!
//                            )
//                        } else if (intent.getStringExtra(AppConstants.EXTRA_NOTIFICATION_DATA_TYPE) == AppConstants.EXTRA_VIRTUAL_WITNESS_PAYLOAD) {
//                            Log.i(
//                                "NOTIFICATION_DATA",
//                                intent.getStringExtra(AppConstants.EXTRA_NOTIFICATION_DATA_TYPE)!!
//                            )
//                        }
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

//        val broadcastReceiver = ConnectivityChangeReceiver()
//        registerReceiver(broadcastReceiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))
//        IntentFilter(Intent.ACTION_ANSWER)
//        val constraints: Constraints = Constraints.Builder()
//            .setRequiredNetworkType(NetworkType.CONNECTED)
//            .build()
//        val notificationWork = OneTimeWorkRequest.Builder(NotifyWork::class.java)
//            .setConstraints(constraints).build()
//
//        val instanceWorkManager = WorkManager.getInstance(this)
//        instanceWorkManager.beginUniqueWork(
//            NOTIFICATION_WORK,
//            ExistingWorkPolicy.REPLACE, notificationWork
//        ).enqueue()


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
}