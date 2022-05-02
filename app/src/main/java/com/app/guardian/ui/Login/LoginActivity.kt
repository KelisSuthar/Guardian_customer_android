package com.app.guardian.ui.Login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ReusedMethod.updateStatusBarColor(this,R.color.colorPrimaryDark,4)
    }
}