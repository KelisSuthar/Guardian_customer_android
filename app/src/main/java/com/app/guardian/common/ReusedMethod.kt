package com.app.guardian.common

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup.MarginLayoutParams
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.app.guardian.R
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.model.HomeBanners.UserHomeBannerResp
import com.app.guardian.ui.AutoCompleteAdapter
import com.app.guardian.ui.SubscriptionPlan.SubScriptionPlanScreen
import com.bumptech.glide.Glide
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.rilixtech.widget.countrycodepicker.CountryCodePicker
import de.hdodenhof.circleimageview.CircleImageView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin


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
            dialog.setContentView(R.layout.dialog_layout)
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

        fun displayLawyerContactDetails(
            context: Activity,
            strLawyerName: String?,
            email: String?,
            phone: String?,
            lawyerProfilePic: String?
        ) {
            val dialog = Dialog(
                context,
                com.google.android.material.R.style.Base_Theme_AppCompat_Light_Dialog_Alert
            )
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setContentView(R.layout.dialog_lawyer_dial_contact)
            dialog.setCancelable(true)

            val PROFILEURL = dialog.findViewById<CircleImageView>(R.id.imgDialogLawyerPicture)
            val OK = dialog.findViewById<MaterialTextView>(R.id.txtLawyerDialogOk)
            val CALLNOW = dialog.findViewById<MaterialTextView>(R.id.txtDialogCallNow)
            val LAWYERNAME = dialog.findViewById<TextView>(R.id.txtDialogLawyerName)
            val EMAIL = dialog.findViewById<TextView>(R.id.txtLawyerEmailID)
            val PHONE = dialog.findViewById<MaterialTextView>(R.id.txtLawyerContact)
            LAWYERNAME.text = strLawyerName
            if (email != null && email != "") {
                EMAIL.text = email
            } else {
                EMAIL.text = "contactSupport@gmail.com"
            }
            PHONE.text = "+$phone"

            if (lawyerProfilePic != null || lawyerProfilePic != "null") {
                Glide.with(context)
                    .load(lawyerProfilePic)
                    .placeholder(R.drawable.profile)
                    .into(PROFILEURL)
            }

            OK.setOnClickListener {
                dialog.dismiss()
            }
            CALLNOW.setOnClickListener {
                val u: Uri = Uri.parse("tel:" + PHONE.getText().toString())

                val i = Intent(Intent.ACTION_DIAL, u)
                try {
                    // Launch the Phone app's dialer with a phone
                    // number to dial a call.
                    context.startActivity(i)
                } catch (s: SecurityException) {
                    // show() method display the toast with
                    // exception message.
                    displayMessage(context, "An error occurred")
                }

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

        fun showKnowRightDialog(
            context: Context,
            title: String? = "",
            rightsection: String? = "",
            location: String? = "",
            desc: String? = ""
        ) {
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

        fun getAddress(context: Context, MyLat: Double, MyLong: Double): List<Address> {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses: List<Address> = geocoder.getFromLocation(MyLat, MyLong, 100)
            Log.i("THIS_APP", "ADMIN AREA::::::" + addresses[0].adminArea)
            Log.i("THIS_APP", "COUNTRY CODE::::::" + addresses[0].countryCode)
            Log.i("THIS_APP", "COUNTRY NAME::::::" + addresses[0].countryName)
            Log.i("THIS_APP", "FEATURE NAME::::::" + addresses[0].featureName)
            Log.i("THIS_APP", "LOCALITY::::::" + addresses[0].locality)
            Log.i("THIS_APP", "SUBLOCALITY::::::" + addresses[0].subLocality)
            Log.i("THIS_APP", "LOCALE::::::" + addresses[0].locale.toString())
            Log.i("THIS_APP", "POSTAL CODE::::::" + addresses[0].postalCode)
            return addresses
        }


        fun initializeAutocompleteTextView(
            context: Context,
            edtLoginEmail: AutoCompleteTextView,
        ) {

            var adapter: AutoCompleteAdapter? = null
            var responseView: TextView? = null
            var placesClient: PlacesClient? = null

            val apiKey =
                context.getString(R.string.g_map_api_key_1) + context.getString(R.string.g_map_api_key_2) + context.getString(
                    R.string.g_map_api_key_3
                )
            if (apiKey.isEmpty()) {
                responseView!!.text = "error"
                return
            }

            if (!Places.isInitialized()) {
                Places.initialize(context, apiKey)
            }

            placesClient = Places.createClient(context)

            edtLoginEmail.threshold = 1

            edtLoginEmail.onItemClickListener = autocomplete(adapter, responseView, placesClient)
            adapter = AutoCompleteAdapter(context, placesClient)
            edtLoginEmail.setAdapter(adapter)


        }

        fun autocomplete(
            adapter: AutoCompleteAdapter?,
            responseView: TextView?,
            placesClient: PlacesClient
        ): AdapterView.OnItemClickListener {
            val autocompleteClickListener =
                AdapterView.OnItemClickListener { adapterView, view, i, l ->
                    try {
                        val item: AutocompletePrediction? = adapter!!.getItem(i)
                        var placeID: String? = null
                        if (item != null) {
                            placeID = item.placeId
                        }
                        val placeFields: List<Place.Field> =
                            listOf(
                                Place.Field.ID,
                                Place.Field.NAME,
                                Place.Field.ADDRESS,
                                Place.Field.LAT_LNG,
                            )
                        var request: FetchPlaceRequest? = null
                        if (placeID != null) {
                            request = FetchPlaceRequest.builder(placeID, placeFields).build()
                        }
                        if (request != null) {
                            placesClient!!.fetchPlace(request)
                                .addOnSuccessListener { task ->
                                    responseView!!.text =

                                        """${task.place.name}""".trimIndent() + task.place.address
                                }.addOnFailureListener { e ->
                                    e.printStackTrace()
                                    responseView!!.text = e.message
                                }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            return autocompleteClickListener
        }


        fun setLocationDialog(context: Context) {
            val dialog = Dialog(
                context,
                com.google.android.material.R.style.Base_Theme_AppCompat_Light_Dialog_Alert
            )
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setContentView(R.layout.dialog_layout)
            dialog.setCancelable(false)

            val OK = dialog.findViewById<MaterialTextView>(R.id.tvPositive)
            val TITLE = dialog.findViewById<TextView>(R.id.tvTitle)
            val MESSAGE = dialog.findViewById<TextView>(R.id.tvMessage)
            val CANCEL = dialog.findViewById<MaterialTextView>(R.id.tvNegative)
            MESSAGE.gone()
            CANCEL.gone()
            OK.text = "OK"

            TITLE.text = "Please turn on location to continue"
            OK.setOnClickListener {
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                dialog.dismiss()
            }

            dialog.show()
        }

        fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val theta = lon1 - lon2
            var dist = (sin(deg2rad(lat1))
                    * sin(deg2rad(lat2))
                    + (cos(deg2rad(lat1))
                    * cos(deg2rad(lat2))
                    * cos(deg2rad(theta))))
            dist = acos(dist)
            dist = rad2deg(dist)
            dist *= 96.5606 * 1.1515// For Km
//            dist *= 60 * 1.1515// ForMiles
            return dist
        }

        private fun deg2rad(deg: Double): Double {
            return deg * Math.PI / 180.0
        }

        private fun rad2deg(rad: Double): Double {
            return rad * 180.0 / Math.PI

        }

        fun viewPagerScroll(pager: ViewPager, total_pages: Int) {
            var currentPage = 0
            pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    currentPage = position
                }

                override fun onPageSelected(position: Int) {

                }

                override fun onPageScrollStateChanged(state: Int) {

                }
            })

            val handler = Handler()

            val NUM_PAGES = total_pages
            val Update = Runnable {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0
                }
                pager.setCurrentItem(currentPage++, true)
            }
            val timer = Timer()
            timer.schedule(object : TimerTask() {

                override fun run() {
                    handler.post(Update)
                }
            }, 500, 1500)
        }

        fun getCurrentDate(): String {
            val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val currentTime: Date = Calendar.getInstance().time
            val date = fmt.format(currentTime)
            return date
        }

        fun getCurrentDay(): String {
            val fmt = SimpleDateFormat("dd")
            val currentTime: Date = Calendar.getInstance().time
            val date = fmt.format(currentTime)
            return date
        }

        fun changeToDay(str_date: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val outputFormat = SimpleDateFormat("dd")
            var date: Date? = null
            var str: String? = null
            try {
                date = inputFormat.parse(str_date)
                str = outputFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return str!!
        }

        @SuppressLint("SetTextI18n")
        fun showSubscriptionDialog(context: Context, data: UserHomeBannerResp) {
            if (SharedPreferenceManager.getString(
                    AppConstants.USER_ROLE,
                    ""
                ) != AppConstants.APP_ROLE_MEDIATOR
            ) {
                if (data.is_subscribe == 0) {
                    val dialog = Dialog(
                        context,
                        com.google.android.material.R.style.Base_Theme_AppCompat_Light_Dialog_Alert
                    )
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.setContentView(R.layout.home_info_dialog_layout)
                    dialog.setCancelable(false)

                    val OK = dialog.findViewById<MaterialTextView>(R.id.tvPositive)
                    val TITLE = dialog.findViewById<TextView>(R.id.txtTitle)
                    val MESSAGE = dialog.findViewById<TextView>(R.id.tvMessage)
                    val MESSAGE2 = dialog.findViewById<TextView>(R.id.tvMessage2)
                    val CANCEL = dialog.findViewById<MaterialTextView>(R.id.tvNegative)
                    OK.text = context.resources.getString(R.string.subscribe_now)
                    CANCEL.text = "Cancel"
                    TITLE.text = "Hello," + SharedPreferenceManager.getUser()!!.full_name
                    MESSAGE.text =
                        context.resources.getString(R.string.suscription_validation_1)
                    MESSAGE2.text =
                        context.resources.getString(R.string.suscription_validation_2)

                    OK.isAllCaps = false
                    CANCEL.isAllCaps = false

                    CANCEL.setOnClickListener {
                        dialog.dismiss()
                    }
                    OK.setOnClickListener {
                        context.startActivity(
                            Intent(
                                context,
                                SubScriptionPlanScreen::class.java
                            )
                        )
                        dialog.dismiss()
                    }
                    dialog.show()
                }
            }

        }

        fun selectTime(context: Context, txtTime: TextView, isMultiple: Boolean? = false) {
            val currentTime = Calendar.getInstance().time
            val hrsFormatter = SimpleDateFormat("HH", Locale.getDefault())
            val minFormatter = SimpleDateFormat("mm", Locale.getDefault())

            val timePicker = TimePickerDialog(
                context, R.style.DialogTheme,
                // listener to perform task
                // when time is picked
                { view, hourOfDay, minute ->
                    val formattedTime: String = when {
                        hourOfDay == 0 -> {
                            if (minute < 10) {
                                "${hourOfDay + 12}:0${minute} AM"
                            } else {
                                "${hourOfDay + 12}:${minute} AM"
                            }
                        }
                        hourOfDay > 12 -> {
                            if (minute < 10) {
                                "${hourOfDay - 12}:0${minute} PM"
                            } else {
                                "${hourOfDay - 12}:${minute} PM"
                            }
                        }
                        hourOfDay == 12 -> {
                            if (minute < 10) {
                                "${hourOfDay}:0${minute} PM"
                            } else {
                                "${hourOfDay}:${minute} PM"
                            }
                        }
                        else -> {
                            if (minute < 10) {
                                "${hourOfDay}:${minute} AM"
                            } else {
                                "${hourOfDay}:${minute} AM"
                            }
                        }
                    }
                    txtTime.text = formattedTime
                },
                // default hour when the time picker
                // dialog is opened
                hrsFormatter.format(currentTime).toInt(),
                // default minute when the time picker
                // dialog is opened
                minFormatter.format(currentTime).toInt(),
                false
            )

            // then after building the timepicker
            // dialog show the dialog to user
            timePicker.show()
            timePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
            timePicker.getButton(DatePickerDialog.BUTTON_NEUTRAL)
                .setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
            timePicker.getButton(DatePickerDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
        }

        fun selectDate(context: Context, txtDate: TextView) {
            var mYear = 0
            var mMonth = 0
            var mDay = 0

            val c = Calendar.getInstance()
            mYear = c[Calendar.YEAR]
            mMonth = c[Calendar.MONTH]
            mDay = c[Calendar.DAY_OF_MONTH]
            val datePickerDialog = DatePickerDialog(
                context, R.style.DialogTheme,
                { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val calendar = Calendar.getInstance()
                    calendar[year, monthOfYear] = dayOfMonth
                    txtDate.text = getDate(year, monthOfYear, dayOfMonth)
                }, mYear, mMonth, mDay
            )
            datePickerDialog.show()
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEUTRAL)
                .setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))


            val calendar2 = Calendar.getInstance();
            calendar2.add(Calendar.MONTH, 1)
            datePickerDialog.datePicker.minDate = calendar2.timeInMillis


        }

        fun getDate(year: Int, monthOfYear: Int, dayOfMonth: Int): String {
            val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar[year, monthOfYear] = dayOfMonth
            return dateFormatter.format(calendar.time)
        }

        fun callConformationDialog(
            context: Context,
            date: String,
            time: String,
            headder: String?,
            isMultiple: Boolean? = false
        ) {
            val dialog = Dialog(
                context,
                com.google.android.material.R.style.Base_Theme_AppCompat_Light_Dialog_Alert
            )
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setContentView(R.layout.virtual_witness_request_confirmation_dialog)
            dialog.setCancelable(true)

            val ivClose: ImageView = dialog.findViewById(R.id.ivClose)
            val txtDate: TextView = dialog.findViewById(R.id.txtDate)
            val txtTime: TextView = dialog.findViewById(R.id.txtTime)
            val txtDesc: TextView = dialog.findViewById(R.id.txtDesc)
            val txtTitle: TextView = dialog.findViewById(R.id.txtTitle)
            val sub: TextView = dialog.findViewById(R.id.btnRequestSend)

            txtDate.text = date
            txtTime.text = time
            txtTitle.text = headder
            ivClose.setOnClickListener {
                dialog.dismiss()
            }
            sub.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }


        fun setUpDialog(context: Context, dialogLayout: Int,canclable:Boolean): Dialog {
            val dialog = Dialog(
                context,
                com.google.android.material.R.style.Base_Theme_AppCompat_Light_Dialog_Alert
            )
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setContentView(dialogLayout)
            dialog.setCancelable(canclable)
            return dialog
        }
    }


}