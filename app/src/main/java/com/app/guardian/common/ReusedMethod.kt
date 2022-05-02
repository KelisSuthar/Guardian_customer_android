package com.app.guardian.common

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.app.guardian.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText


class ReusedMethod {
    var alertDialog: AlertDialog? = null

    companion object {
        fun getApplicationName(context: Context): String {
            val applicationInfo = context.applicationInfo
            val stringId = applicationInfo.labelRes
            return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(
                stringId
            )
        }


        fun showSnackBar(context: Activity?, message: String?, length: Int) {
            if (context != null) {
                val contextView = context.findViewById<View>(android.R.id.content)
                val snackbar = Snackbar.make(contextView, message!!, Snackbar.LENGTH_SHORT)
                val snackBarView = snackbar.view
                // snackBarView.background = context.getDrawable(R.drawable.ic_email_active)
                val tv =
                    snackBarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                tv.textSize = 12f
                tv.setTextColor(ContextCompat.getColor(context, R.color.white))
                tv.gravity = Gravity.CENTER
                val params = snackBarView.layoutParams as MarginLayoutParams
                params.setMargins(2, params.topMargin, 2, params.bottomMargin + 0)
                snackBarView.layoutParams = params
                snackbar.show()
            }
        }

        fun hideKeyboard(activity: Activity) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val f = activity.currentFocus
            if (null != f && null != f.windowToken && EditText::class.java.isAssignableFrom(f.javaClass)) imm.hideSoftInputFromWindow(
                f.windowToken,
                0
            ) else activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        }

        fun isNetworkConnected(context: Context): Boolean {
            if (context != null) {
                val connectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                return if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                    true
                } else {
                    CustomToast.showToast(
                        context, context.resources.getString(R.string.internet_message_alert), true,
                        ContextCompat.getColor(context, R.color.white),
                        ContextCompat.getColor(context, R.color.black), true
                    )

                    false
                }
            }
            return false
        }

        private fun isInternetAvailable(context: Context): Boolean {
            var result = false
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkCapabilities = connectivityManager.activeNetwork ?: return false
                val actNw =
                    connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
                result = when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } else {
                connectivityManager.run {
                    connectivityManager.activeNetworkInfo?.run {
                        result = when (type) {
                            ConnectivityManager.TYPE_WIFI -> true
                            ConnectivityManager.TYPE_MOBILE -> true
                            ConnectivityManager.TYPE_ETHERNET -> true
                            else -> false
                        }

                    }
                }
            }

            return result
        }

        fun getMapsAPIKeyFromManifest(context: Context): String? {
            var apiKey: String? = null
            try {
                val ai = context.packageManager
                    .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
                val bundle = ai.metaData
                apiKey = bundle.getString("com.google.android.geo.API_KEY")
                //            getPresenter().setGoogleAPIKey(apiKey);
                return apiKey
            } catch (e: PackageManager.NameNotFoundException) {
                ShowLogToast.ShowLogError(
                    "GETKEYERROR",
                    "Failed to load meta-data, NameNotFound: " + e.message
                )
            } catch (e: NullPointerException) {
                ShowLogToast.ShowLogError(
                    "GETKEYERROR",
                    "Failed to load meta-data, NullPointer: " + e.message
                )
            }

            return null
        }

        fun displayMessage(context: Activity, message: String) {
            CustomToast.showToast(
                context, message,
                true,
                ContextCompat.getColor(context, R.color.white),
                ContextCompat.getColor(context, R.color.black),
                true
            )
        }

        fun change_edittext_background(context: Activity?, editText: TextInputEditText) {
            editText.onFocusChangeListener =
                OnFocusChangeListener { p0, isFoucus ->
                    run {
//                        if (isFoucus) {
//                            editText.background = ContextCompat.getDrawable(
//                                context!!,
//                                R.drawable.normal_rounded_white_back_borders
//                            )
//
//                        } else {
//                            editText.background = ContextCompat.getDrawable(
//                                context!!,
//                                R.drawable.normal_rounded_white_back
//                            )
//                        }
                    }
                }

        }

        fun updateStatusBarColor(context: Activity, color: Int, position: Int) {
            // Color must be in hexadecimal format
            val window = context.window
            val view = window.decorView
            if (position == 0) {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                );
            } else if (position == 2) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    view.systemUiVisibility =
                        view.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
            } else if (position == 4) {
                context.window.apply {
                    clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    statusBarColor = Color.TRANSPARENT
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                    // view.systemUiVisibility = view.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inc()
                }
                view.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }
            window.statusBarColor = ContextCompat.getColor(context, color)
        }
    }
}