package com.app.guardian.ui.aboutus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod

class AboutUsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
    }
}