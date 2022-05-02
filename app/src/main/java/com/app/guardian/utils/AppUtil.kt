package com.app.guardian.utils

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat

object AppUtil {
    fun updateStatusBarColor(context: Activity, color: Int, position: Int) { // Color must be in hexadecimal fromat
        val window = context.window
        val view = window.decorView
        if (position == 0) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                view.systemUiVisibility = view.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        } else if (position == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            }
            view.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
        // window.setStatusBarColor(context.getResources().getColor(color, context.getTheme()));
        window.statusBarColor = ContextCompat.getColor(context, color)
    }
}