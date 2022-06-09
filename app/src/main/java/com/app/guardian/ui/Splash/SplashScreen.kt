package com.app.guardian.ui.Splash

import android.content.Intent
import android.os.Handler
import android.util.Log
import android.view.View
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.databinding.ActivitySplashScreenBinding
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.Login.LoginActivity
import com.app.guardian.ui.SelectRole.SelectRoleScreen
import com.app.guardian.ui.SubscriptionPlan.SubScriptionPlanScreen
import com.app.guardian.utils.ApiConstant
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class SplashScreen : BaseActivity(), View.OnClickListener {
    lateinit var mBinding: ActivitySplashScreenBinding
    var DEVICE_TOKEN = SharedPreferenceManager.getString(ApiConstant.EXTRAS_DEVICETOKEN, "")
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorTransparent, 0)
        return R.layout.activity_splash_screen
    }

    override fun initView() {
        mBinding = getBinding()
        Handler().postDelayed({
            finish()
            if (!SharedPreferenceManager.getBoolean(AppConstants.IS_LOGIN, false)) {
                startActivity(
                    Intent(
                        this@SplashScreen,

                        SelectRoleScreen::class.java
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
                startActivity(
                    Intent(
                        this@SplashScreen,

                        HomeActivity::class.java
                    )
                )
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
        }else{
            Log.e("StoredDeviceToken", DEVICE_TOKEN.toString())
        }

    }

    override fun initObserver() {

    }

    override fun handleListener() {

    }


    override fun onClick(p0: View?) {

    }
}