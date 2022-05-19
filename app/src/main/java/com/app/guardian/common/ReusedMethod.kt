package com.app.guardian.common

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup.MarginLayoutParams
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.app.guardian.R
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.PhoneEmailSelectorBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.rilixtech.widget.countrycodepicker.CountryCodePicker


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

        fun isInternetAvailable(context: Context): Boolean {
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

        fun checkInputs(editText: EditText): Boolean {
            var isAllEmpty = false
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    isAllEmpty = p0?.length == 0

                }

                override fun afterTextChanged(p0: Editable?) {

                }

            })
            return isAllEmpty
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

        fun displayMessageDialog(
            context: Activity,
            message: String,
            title: String,
            isCancelShow: Boolean,
            posBtnTxt: String,
            negBtnTxt: String
        ) {
            val dialog = Dialog(
                context,
                com.google.android.material.R.style.Base_Theme_AppCompat_Light_Dialog_Alert
            )
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setContentView(R.layout.dialig_layout)
            dialog.setCancelable(false)

            val OK = dialog.findViewById<MaterialTextView>(R.id.tvPositive)
            val TITLE = dialog.findViewById<TextView>(R.id.tvTitle)
            val MESSAGE = dialog.findViewById<TextView>(R.id.tvMessage)
            val CANCEL = dialog.findViewById<MaterialTextView>(R.id.tvNegative)
            TITLE.text = title
            MESSAGE.text = message
            if (posBtnTxt.isNotEmpty()) {
                OK.text = posBtnTxt
            }
            if (negBtnTxt.isNotEmpty()) {
                CANCEL.text = negBtnTxt
            }

            if (isCancelShow) {
                CANCEL.visible()
            } else {
                CANCEL.gone()
            }

            CANCEL.setOnClickListener {
                dialog.dismiss()
            }
            OK.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }

        fun ShowRedBorders(context: Context, editText: EditText) {
            editText.background = ContextCompat.getDrawable(
                context,
                R.drawable.normal_rounded_light_blue_red_borders
            )
        }

        fun ShowNoBorders(context: Context, editText: EditText) {
            editText.background = ContextCompat.getDrawable(
                context,
                R.drawable.normal_rounded_light_blue
            )
        }

        fun change_edittext_background(context: Activity?, editText: TextInputEditText) {
            editText.onFocusChangeListener =
                OnFocusChangeListener { p0, isFoucus ->
                    run {
                        if (isFoucus) {
                            editText.background = ContextCompat.getDrawable(
                                context!!,
                                R.drawable.normal_rounded_light_blue_borders
                            )

                        } else {
                            editText.background = ContextCompat.getDrawable(
                                context!!,
                                R.drawable.normal_rounded_light_blue
                            )
                        }
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
                    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)

                }
                view.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }
            window.statusBarColor = ContextCompat.getColor(context, color)
        }


        fun changePhoneEmailState(
            context: Context,
            b: Boolean,
            editText: EditText,
            ccp: CountryCodePicker,
            parentLayout: ConstraintLayout
        ) {
            parentLayout.background = ContextCompat.getDrawable(
                context,
                R.drawable.normal_rounded_light_blue
            )
            editText.setText("")
            editText.hint = context.resources.getString(R.string.email)
            if (b) {
                editText.inputType = InputType.TYPE_CLASS_TEXT
                ccp.gone()
                editText.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_mail,
                    0,
                    0,
                    0
                )
                editText.compoundDrawablePadding =
                    context.resources.getDimension(com.intuit.sdp.R.dimen._10sdp).toInt()
            } else {
                editText.inputType =
                    InputType.TYPE_CLASS_NUMBER
                editText.hint = context.resources.getString(R.string.phone)
                ccp.visible()
                editText.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    0,
                    0
                )
            }
        }

        fun changePhoneState(
            context: Context,
            editText: TextInputEditText,
            ccp: CountryCodePicker,
            parentLayout: ConstraintLayout
        ) {
            parentLayout.background = ContextCompat.getDrawable(
                context,
                R.drawable.normal_rounded_light_blue
            )
            editText.setText("")
            editText.hint = context.resources.getString(R.string.email)
                editText.inputType =
                    InputType.TYPE_CLASS_NUMBER
                editText.hint = context.resources.getString(R.string.phone)
                ccp.visible()
                editText.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    0,
                    0
                )

        }

        fun showKnowRightDialog(context: Context,title: String?="",rightsection:String?="",location:String?="",desc:String?="") {
            val dialog = Dialog(
                context,
                com.google.android.material.R.style.Base_Theme_AppCompat_Light_Dialog_Alert
            )
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setContentView(R.layout.know_right_dialog)
            dialog.setCancelable(false)

            val ivClose = dialog.findViewById<ImageView>(R.id.ivClose)
            val btnClose = dialog.findViewById<Button>(R.id.btnClose)
            val txtRightName = dialog.findViewById<AppCompatTextView>(R.id.txtRightName)
            val txtRightSections = dialog.findViewById<AppCompatTextView>(R.id.txtRightSections)
            val txtLcation = dialog.findViewById<AppCompatTextView>(R.id.txtLcation)
            val txtDesc = dialog.findViewById<AppCompatTextView>(R.id.txtDesc)

            txtRightName.text = title
            txtRightSections.text = rightsection
            txtLcation.text = location
            txtDesc.text = desc

            ivClose.setOnClickListener {
                dialog.dismiss()
            }
            btnClose.setOnClickListener {
                dialog.dismiss()
            }



            dialog.show()
        }
    }
}