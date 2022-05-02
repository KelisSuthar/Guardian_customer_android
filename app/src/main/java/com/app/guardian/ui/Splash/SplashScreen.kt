package com.app.guardian.ui.Splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod
import com.app.guardian.shareddata.base.BaseActivity

class SplashScreen : BaseActivity(),View.OnClickListener {
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorTransparent, 0)
        return  R.layout.activity_splash_screen
    }

    override fun initView() {

    }

    override fun initObserver() {

    }

    override fun handleListener() {

    }


    override fun onClick(p0: View?) {

    }
}