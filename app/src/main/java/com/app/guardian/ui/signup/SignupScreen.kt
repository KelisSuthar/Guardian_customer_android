package com.app.guardian.ui.signup

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import androidx.core.content.PermissionChecker
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.common.*
import com.app.guardian.common.ReusedMethod.Companion.ShowNoBorders
import com.app.guardian.common.ReusedMethod.Companion.ShowRedBorders
import com.app.guardian.common.ReusedMethod.Companion.checkInputs
import com.app.guardian.common.ReusedMethod.Companion.displayMessage
import com.app.guardian.common.ReusedMethod.Companion.displayMessageDialog
import com.app.guardian.common.ReusedMethod.Companion.isNetworkConnected
import com.app.guardian.common.ReusedMethod.Companion.setLocationDialog
import com.app.guardian.common.extentions.checkLoationPermission
import com.app.guardian.common.extentions.checkPermissions
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.ActivitySignupScreenBinding
import com.app.guardian.model.specializationList.SpecializationListResp
import com.app.guardian.model.viewModels.AuthenticationViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.termsandcondtions.TermAndConditionsActivity
import com.app.guardian.ui.Login.LoginActivity
import com.app.guardian.ui.signup.adapter.ImageAdapter
import com.app.guardian.ui.signup.adapter.SpecializationAdapter
import com.app.guardian.utils.ApiConstant
import com.app.guardian.utils.Config
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*


class SignupScreen : BaseActivity(), View.OnClickListener {
    private val mViewModel: AuthenticationViewModel by viewModel()
    private val authViewModel: AuthenticationViewModel by viewModel()
    var DEVICE_TOKEN = SharedPreferenceManager.getString(ApiConstant.EXTRAS_DEVICETOKEN, "")

    lateinit var mBinding: ActivitySignupScreenBinding
    var imageAdapter: ImageAdapter? = null
    var images = ArrayList<String>()
    var specializationList = ArrayList<SpecializationListResp>()
    var PROFILE_IMG_CODE = 101
    var DOCUMENT_CODE = 102
    var profile_img = ""
    var is_lawyer = false
    var is_mediator = false
    var is_user = false
    var ROLE = ""
    var selectedid = -1

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    //    var adapter: AutoCompleteAdapter? = null
//    var responseView: TextView? = null
//    var placesClient: PlacesClient? = null
//    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private var locationManager: LocationManager? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    var isAllEmpty = true
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return R.layout.activity_signup_screen
    }


    override fun initView() {
        auth = FirebaseAuth.getInstance()//Firebase

        mBinding = getBinding()
        mBinding.noInternetSignUp.llNointernet.gone()
        mBinding.nsSignUp.visible()
        mBinding.noDataSignup.gone()
        setAdaper()
//        initializeAutocompleteTextView()
        checkInputs(mBinding.edtFullname)
        checkInputs(mBinding.edtEmail)
        checkInputs(mBinding.edtMobileNum)
        mBinding.ccp.setCountryForPhoneCode(1)
        mBinding.ccpOffice.setCountryForPhoneCode(1)
        locationManager = getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest?.interval = 20 * 1000
        checkPermissions(
            this,
            AppConstants.EXTRA_CAMERA_PERMISSION,
            android.Manifest.permission.CAMERA
        )
        checkPermissions(
            this,
            AppConstants.EXTRA_READ_PERMISSION,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        checkPermissions(
            this,
            AppConstants.EXTRA_WRITE_PERMISSION,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        when {
            SharedPreferenceManager.getString(
                AppConstants.USER_ROLE,
                AppConstants.APP_ROLE_USER
            ) == AppConstants.APP_ROLE_USER -> {
                is_user = true
                ROLE = AppConstants.APP_ROLE_USER
            }
            SharedPreferenceManager.getString(
                AppConstants.USER_ROLE,
                AppConstants.APP_ROLE_USER
            ) == AppConstants.APP_ROLE_LAWYER -> {
                is_lawyer = true
                ROLE = AppConstants.APP_ROLE_LAWYER
                mBinding.edtSpecializations.visible()
                mBinding.fmOficeNum.visible()
                mBinding.edtYearsOfExp.visible()
                mBinding.edtRegisteredLicenceNum.visible()
            }
            SharedPreferenceManager.getString(
                AppConstants.USER_ROLE,
                AppConstants.APP_ROLE_USER
            ) == AppConstants.APP_ROLE_MEDIATOR -> {
                is_mediator = true
                ROLE = AppConstants.APP_ROLE_MEDIATOR
                mBinding.edtSpecializations.visible()
                mBinding.edtYearsOfExp.visible()
            }
        }

       mBinding.ccp.setOnCountryChangeListener {
        Log.i("THIS_APP",mBinding.ccp.selectedCountryCode)
        Log.i("THIS_APP",mBinding.ccpOffice.selectedCountryCode)
       }
        mBinding.ccpOffice.setOnCountryChangeListener {
        Log.i("THIS_APP",mBinding.ccp.selectedCountryCode)
        Log.i("THIS_APP",mBinding.ccpOffice.selectedCountryCode)
       }

        setFocus()

    }

    private fun setFocus() {
        mBinding.edtFullname.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                val value = mBinding.edtFullname.text?.trim().toString()

                if (!hasFocus) {
                    if (value.length > 3 && !TextUtils.isEmpty(value)) {
                        ShowNoBorders(this@SignupScreen, mBinding.edtFullname)
                    }
                }
            }

        mBinding.edtEmail.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                val value = mBinding.edtEmail.text?.trim().toString()

                if (!hasFocus) {
                    if (SmartUtils.emailValidator(value) && !TextUtils.isEmpty(value)) {
                        ShowNoBorders(this@SignupScreen, mBinding.edtEmail)
                    }
                }
            }

        mBinding.edtMobileNum.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                val value = mBinding.edtMobileNum.text?.trim().toString()

                if (!hasFocus) {
                    if (value.length >= 10 && !TextUtils.isEmpty(value)) {
                        ShowNoBorders(this@SignupScreen, mBinding.edtMobileNum)
                    }
                }
            }

        mBinding.edtPass.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                val value = mBinding.edtPass.text?.trim().toString()
                if (!hasFocus) {

                    if (value.length >= 8 && !TextUtils.isEmpty(value) && SmartUtils.checkSpecialPasswordValidation(
                            value
                        )
                    ) {
                        ShowNoBorders(this@SignupScreen, mBinding.edtPass)
                    }
                }
            }

        mBinding.edtConPass.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                val value = mBinding.edtConPass.text?.trim().toString()
                if (!hasFocus) {

                    if (value.length >= 8 && !TextUtils.isEmpty(value) && SmartUtils.checkSpecialPasswordValidation(
                            value
                        )
                    ) {
                        ShowNoBorders(this@SignupScreen, mBinding.edtConPass)
                    }
                }
            }

        mBinding.edtProvience.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                val value = mBinding.edtProvience.text?.trim().toString()
                if (!hasFocus) {

                    if (!TextUtils.isEmpty(value)) {
                        ShowNoBorders(this@SignupScreen, mBinding.edtProvience)
                    }
                }
            }

        mBinding.edtPostalCode.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                val value = mBinding.edtPostalCode.text?.trim().toString()
                if (!hasFocus) {

                    if (!TextUtils.isEmpty(value) && (value.length > 3 || value.length < 9)) {
                        ShowNoBorders(this@SignupScreen, mBinding.edtPostalCode)
                    }
                }
            }
        mBinding.edtRegisteredLicenceNum.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                val value = mBinding.edtRegisteredLicenceNum.text?.trim().toString()
                if (!hasFocus) {

                    if (!TextUtils.isEmpty(value) || value.length<5 ) {
                        ShowNoBorders(this@SignupScreen, mBinding.edtRegisteredLicenceNum)
                    }
                }
            }

        mBinding.edtSpecializations.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                val value = mBinding.edtSpecializations.text?.trim().toString()
                if (!hasFocus) {
                    if (!TextUtils.isEmpty(value)) {
                        ShowNoBorders(this@SignupScreen, mBinding.edtSpecializations)
                    }
                }
            }

        mBinding.edtYearsOfExp.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                val value = mBinding.edtYearsOfExp.text?.trim().toString()
                if (!hasFocus) {
                    if (!TextUtils.isEmpty(value) && value != "0") {
                        ShowNoBorders(this@SignupScreen, mBinding.edtYearsOfExp)
                    }
                }
            }

        mBinding.edtOfficeNum.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                val value = mBinding.edtOfficeNum.text?.trim().toString()
                if (!hasFocus) {
                    if (!TextUtils.isEmpty(value) && value.length == 10) {
                        ShowNoBorders(this@SignupScreen, mBinding.edtOfficeNum)
                    }
                }
            }
    }

    override fun onResume() {
        super.onResume()
        mBinding.noDataSignup.gone()
        mBinding.noInternetSignUp.llNointernet.gone()
        mBinding.nsSignUp.visible()
        if (SharedPreferenceManager.getString(
                AppConstants.USER_ROLE,
                ""
            ) != AppConstants.APP_ROLE_USER
        ) {
            callSpecializationAPI()
        }

        getLatLong()

        if (checkLoationPermission(this)) {


            if (locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!) {

                mFusedLocationClient?.requestLocationUpdates(
                    locationRequest!!,
                    locationCallback!!,
                    Looper.getMainLooper()
                )
            } else {

                setLocationDialog(this)
            }
        }


    }


    private fun setAdaper() {
        mBinding.rvImage.adapter = null
        imageAdapter = ImageAdapter(this, images, object : ImageAdapter.onItemClicklisteners {
            override fun onCancelCick(position: Int) {
                images.removeAt(position)
                imageAdapter?.notifyDataSetChanged()
            }

        })
        mBinding.rvImage.adapter = imageAdapter
    }


    override fun initObserver() {
        mViewModel.getSignupResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {


                            finish()
                            startActivity(
                                Intent(
                                    this@SignupScreen,
                                    LoginActivity::class.java
                                )
                            )
//                            overridePendingTransition(R.anim.rightto, R.anim.left)
                            displayMessage(this, it.message.toString())
                        } else {
                            displayMessage(this, it.message.toString())
                        }
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            displayMessage(this, getString(R.string.text_error_network))

                        Config.CUSTOM_ERROR ->
                            errorObj.customMessage
                                ?.let {}
                    }
                }
            }
        }
        //SPECIALIZATION LIST RESP
        mViewModel.getSpecializationListResp().observe(this) { response ->
            response?.let { requestState ->

                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        specializationList.clear()
                        if (it.status) {
                            specializationList.addAll(data)
                        } else {
                            displayMessage(this, it.message.toString())
                        }
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            displayMessage(this, getString(R.string.text_error_network))

                        Config.CUSTOM_ERROR ->
                            errorObj.customMessage
                                ?.let {}
                    }
                }
            }
        }
    }

    private fun chatRegistration(email: String, password: String) {
        showLoadingIndicator(true)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    val userId: String = user?.uid ?: ""

                    databaseReference = FirebaseDatabase.getInstance()
                        .getReference(resources.getString(R.string.userList)).child(userId)

                    // SharedPreferenceManager.putString("userId", userId)
                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap[resources.getString(R.string.chat_id)] = userId
                    hashMap[resources.getString(R.string.chat_full_name)] =
                        mBinding.edtFullname.text?.trim()
                            .toString()
                    hashMap[resources.getString(R.string.chat_email)] = email
                    hashMap[resources.getString(R.string.chat_lastSeen)] = ""
                    hashMap[resources.getString(R.string.chat_isOnline)] = "true"
                    hashMap[resources.getString(R.string.chat_role)] = ROLE
                    Log.i("GUARDIAN_APP_IT", it.toString())

                    databaseReference.setValue(hashMap).addOnCompleteListener(this) { p ->
                        Log.i("GUARDIAN_APP_P_SUCCESS", p.isSuccessful.toString())
                        Log.i("GUARDIAN_APP_P", p.toString())
                        if (p.isSuccessful) {

                            Log.i("GUARDIAN_APP", p.isSuccessful.toString())
                            Log.i("GUARDIAN_APP", p.toString())
//                            callApi(userId)
                        } else {
                            displayMessageDialog(this, "", p.exception.toString(), false, "OK", "")
                            showLoadingIndicator(false)
                        }
                    }
                }
            }

    }

    override fun handleListener() {
        mBinding.ll1.setOnClickListener(this)
        mBinding.ivAddImage.setOnClickListener(this)
        mBinding.txtTermsAndConditions.setOnClickListener(this)
        mBinding.txtDoNotHaveAccount.setOnClickListener(this)
        mBinding.edtProvience.setOnClickListener(this)
        mBinding.edtPostalCode.setOnClickListener(this)
        mBinding.btnSigUp.setOnClickListener(this)
        mBinding.edtSpecializations.setOnClickListener(this)

        mBinding.headderSignUp.ivBack.setOnClickListener(this)
        mBinding.noInternetSignUp.btnTryAgain.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnTryAgain -> {
                onResume()
            }
            R.id.edtSpecializations -> {
                specializationDialog()
            }
            R.id.txtDoNotHaveAccount -> {
                finish()
                startActivity(
                    Intent(
                        this@SignupScreen,
                        LoginActivity::class.java
                    )
                )
                overridePendingTransition(R.anim.rightto, R.anim.left)
            }
            R.id.ivBack -> {
                onBackPressed()
                overridePendingTransition(R.anim.leftto, R.anim.right)
            }
            R.id.edtProvience -> {
//                val fields =
//                    listOf(
//                        Place.Field.ID,
//                        Place.Field.NAME,
//                        Place.Field.ADDRESS,
//                        Place.Field.LAT_LNG
//                    )
//                val intent =
//                    Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
//                        .build(this)
//                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
            }
            R.id.edtPostalCode -> {
//                val fields =
//                    listOf(
//                        Place.Field.ID,
//                        Place.Field.NAME,
//                        Place.Field.ADDRESS,
//                        Place.Field.LAT_LNG
//                    )
//                val intent =
//                    Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
//                        .build(this)
//                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
            }
            R.id.ll1 -> {
                ImagePicker.with(this)
//                    .crop()
                    .compress(1024)
                    .maxResultSize(
                        1080,
                        1080
                    )
                    .start(PROFILE_IMG_CODE)
//                val galleryIntent =
//                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                startActivityForResult(galleryIntent, PROFILE_IMG_CODE)
            }

            R.id.ivAddImage -> {

                if (images.size < 5) {
                    ImagePicker.with(this)
//                    .crop()
                        .compress(1024)
                        .maxResultSize(
                            1080,
                            1080
                        )
                        .start(DOCUMENT_CODE)

                } else {
                    displayMessageDialog(this, "", "Maximum Image Limit Reached", false, "Ok", "")
                }

            }
            R.id.btnSigUp -> {
                validations()
            }
            R.id.txtTermsAndConditions -> {
                startActivity(Intent(this@SignupScreen, TermAndConditionsActivity::class.java))
                overridePendingTransition(R.anim.rightto, R.anim.left)
            }

        }
    }

    private fun specializationDialog() {

        val dialog = Dialog(
            this,
            com.google.android.material.R.style.Base_Theme_AppCompat_Light_Dialog_Alert
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialog_specializatio_list)
        dialog.setCancelable(true)

        var specializationAdapter: SpecializationAdapter? = null
        specializationAdapter = SpecializationAdapter(
            this,
            specializationList,
            selectedid,
            object : SpecializationAdapter.onItemClicklisteners {
                override fun onItemClick(position: Int, id: Int) {
                    mBinding.edtSpecializations.setText(specializationList[position].title)
                    dialog.dismiss()
                    selectedid = id
                    specializationAdapter!!.notifyDataSetChanged()
                }

            })

        val recyclerView: RecyclerView = dialog.findViewById(R.id.rv)
        recyclerView.adapter = specializationAdapter

        dialog.show()
    }

    private fun validations() {
        IntegratorImpl.isValidSignUp(
            is_lawyer, is_mediator,
            profile_img, mBinding.edtFullname.text?.trim().toString(),
            mBinding.edtEmail.text?.trim().toString(),
            mBinding.edtSpecializations.text?.trim().toString(),
            mBinding.edtYearsOfExp.text?.trim().toString(),
            mBinding.edtOfficeNum.text?.trim().toString(),
            mBinding.edtMobileNum.text?.trim().toString(),
            mBinding.edtPass.text?.trim().toString(),
            mBinding.edtConPass.text?.trim().toString(),
            mBinding.edtProvience.text?.trim().toString(),
            mBinding.edtPostalCode.text?.trim().toString(),
            mBinding.edtRegisteredLicenceNum.text?.trim().toString(),
            images,
            object : ValidationView.SignUp {
                override fun profileImgValidations() {
                    displayMessageDialog(
                        this@SignupScreen,
                        "",
                        resources.getString(R.string.empty_profile_pic),
                        false,
                        "OK",
                        ""
                    )
                }

                override fun fullname_empty() {
                    displayMessageDialog(
                        this@SignupScreen,
                        "",
                        resources.getString(R.string.empty_name),
                        false,
                        "OK",
                        ""
                    )
                    ShowRedBorders(this@SignupScreen, mBinding.edtFullname)
                }

                override fun fulllNameValidation() {
                    displayMessageDialog(
                        this@SignupScreen,
                        "",
                        resources.getString(R.string.valid_name),
                        false,
                        "OK",
                        ""
                    )
                    ShowRedBorders(this@SignupScreen, mBinding.edtFullname)
                }

                override fun email_empty() {
                    displayMessageDialog(
                        this@SignupScreen,
                        "",
                        resources.getString(R.string.empty_email),
                        false,
                        "OK",
                        ""
                    )
                    ShowRedBorders(this@SignupScreen, mBinding.edtEmail)
                }

                override fun emailValidation() {
                    displayMessageDialog(
                        this@SignupScreen,
                        "",
                        resources.getString(R.string.valid_email),
                        false,
                        "OK",
                        ""
                    )
                    ShowRedBorders(this@SignupScreen, mBinding.edtEmail)
                }

                override fun empty_specialization() {
                    displayMessageDialog(
                        this@SignupScreen,
                        "",
                        resources.getString(R.string.empty_specialization),
                        false,
                        "OK",
                        ""
                    )
                    ShowRedBorders(this@SignupScreen, mBinding.edtSpecializations)
                }

                override fun valid_specialization() {
                    displayMessageDialog(
                        this@SignupScreen,
                        "",
                        resources.getString(R.string.valid_specialization),
                        false,
                        "OK",
                        ""
                    )
                    ShowRedBorders(this@SignupScreen, mBinding.edtSpecializations)
                }

                override fun empty_years_exp() {
                    displayMessageDialog(
                        this@SignupScreen,
                        "",
                        resources.getString(R.string.empty_exp),
                        false,
                        "OK",
                        ""
                    )
                    ShowNoBorders(this@SignupScreen, mBinding.edtFullname)
                    ShowNoBorders(this@SignupScreen, mBinding.edtEmail)
                    ShowNoBorders(this@SignupScreen, mBinding.edtSpecializations)
                    ShowRedBorders(this@SignupScreen, mBinding.edtYearsOfExp)
                }

                override fun valid_years_exp() {
                    displayMessageDialog(
                        this@SignupScreen,
                        "",
                        resources.getString(R.string.valid_exp),
                        false,
                        "OK",
                        ""
                    )
                    ShowNoBorders(this@SignupScreen, mBinding.edtFullname)
                    ShowNoBorders(this@SignupScreen, mBinding.edtEmail)
                    ShowNoBorders(this@SignupScreen, mBinding.edtSpecializations)
                    ShowRedBorders(this@SignupScreen, mBinding.edtYearsOfExp)
                }

                override fun empty_office_num() {
                    displayMessageDialog(
                        this@SignupScreen,
                        "",
                        resources.getString(R.string.empty_office_number),
                        false,
                        "OK",
                        ""
                    )
                    ShowNoBorders(this@SignupScreen, mBinding.edtFullname)
                    ShowNoBorders(this@SignupScreen, mBinding.edtEmail)
                    ShowNoBorders(this@SignupScreen, mBinding.edtSpecializations)
                    ShowNoBorders(this@SignupScreen, mBinding.edtYearsOfExp)
                    ShowRedBorders(this@SignupScreen, mBinding.edtOfficeNum)
                }

                override fun valid_office_num() {
                    displayMessageDialog(
                        this@SignupScreen,
                        "",
                        resources.getString(R.string.valid_office_number),
                        false,
                        "OK",
                        ""
                    )
                    ShowNoBorders(this@SignupScreen, mBinding.edtFullname)
                    ShowNoBorders(this@SignupScreen, mBinding.edtEmail)
                    ShowNoBorders(this@SignupScreen, mBinding.edtSpecializations)
                    ShowNoBorders(this@SignupScreen, mBinding.edtYearsOfExp)
                    ShowRedBorders(this@SignupScreen, mBinding.edtOfficeNum)
                }

                override fun moNumber_empty() {
                    displayMessageDialog(
                        this@SignupScreen,
                        "",
                        resources.getString(R.string.empty_number),
                        false,
                        "OK",
                        ""
                    )
                    ShowNoBorders(this@SignupScreen, mBinding.edtFullname)
                    ShowNoBorders(this@SignupScreen, mBinding.edtEmail)
                    ShowNoBorders(this@SignupScreen, mBinding.edtSpecializations)
                    ShowNoBorders(this@SignupScreen, mBinding.edtYearsOfExp)
                    ShowNoBorders(this@SignupScreen, mBinding.edtOfficeNum)
                    ShowRedBorders(this@SignupScreen, mBinding.edtMobileNum)
                }

                override fun moNumberValidation() {
                    displayMessageDialog(
                        this@SignupScreen,
                        "",
                        resources.getString(R.string.valid_number),
                        false,
                        "OK",
                        ""
                    )
                    ShowNoBorders(this@SignupScreen, mBinding.edtFullname)
                    ShowNoBorders(this@SignupScreen, mBinding.edtEmail)
                    ShowNoBorders(this@SignupScreen, mBinding.edtSpecializations)
                    ShowNoBorders(this@SignupScreen, mBinding.edtYearsOfExp)
                    ShowNoBorders(this@SignupScreen, mBinding.edtOfficeNum)
                    ShowRedBorders(this@SignupScreen, mBinding.edtMobileNum)
                }

                override fun password_empty() {
                    displayMessageDialog(
                        this@SignupScreen,
                        "",
                        resources.getString(R.string.empty_pass),
                        false,
                        "OK",
                        ""
                    )
                    ShowNoBorders(this@SignupScreen, mBinding.edtFullname)
                    ShowNoBorders(this@SignupScreen, mBinding.edtEmail)
                    ShowNoBorders(this@SignupScreen, mBinding.edtSpecializations)
                    ShowNoBorders(this@SignupScreen, mBinding.edtYearsOfExp)
                    ShowNoBorders(this@SignupScreen, mBinding.edtOfficeNum)
                    ShowNoBorders(this@SignupScreen, mBinding.edtMobileNum)
                    ShowRedBorders(this@SignupScreen, mBinding.edtPass)

                }

                override fun newpasswordMinValidation() {
                    displayMessageDialog(
                        this@SignupScreen,
                        "",
                        resources.getString(R.string.valid_pass),
                        false,
                        "OK",
                        ""
                    )
                    ShowNoBorders(this@SignupScreen, mBinding.edtFullname)
                    ShowNoBorders(this@SignupScreen, mBinding.edtEmail)
                    ShowNoBorders(this@SignupScreen, mBinding.edtSpecializations)
                    ShowNoBorders(this@SignupScreen, mBinding.edtYearsOfExp)
                    ShowNoBorders(this@SignupScreen, mBinding.edtOfficeNum)
                    ShowNoBorders(this@SignupScreen, mBinding.edtMobileNum)
                    ShowRedBorders(this@SignupScreen, mBinding.edtPass)
                }

                override fun con_password_empty() {
                    displayMessageDialog(
                        this@SignupScreen,
                        "",
                        resources.getString(R.string.empty_con_pass),
                        false,
                        "OK",
                        ""
                    )
                    ShowNoBorders(this@SignupScreen, mBinding.edtFullname)
                    ShowNoBorders(this@SignupScreen, mBinding.edtEmail)
                    ShowNoBorders(this@SignupScreen, mBinding.edtSpecializations)
                    ShowNoBorders(this@SignupScreen, mBinding.edtYearsOfExp)
                    ShowNoBorders(this@SignupScreen, mBinding.edtOfficeNum)
                    ShowNoBorders(this@SignupScreen, mBinding.edtMobileNum)
                    ShowNoBorders(this@SignupScreen, mBinding.edtPass)
                    ShowRedBorders(this@SignupScreen, mBinding.edtConPass)
                }

                override fun conpasswordMinValidation() {
                    displayMessageDialog(
                        this@SignupScreen,
                        "",
                        resources.getString(R.string.not_match_con_pass),
                        false,
                        "OK",
                        ""
                    )
                    ShowNoBorders(this@SignupScreen, mBinding.edtFullname)
                    ShowNoBorders(this@SignupScreen, mBinding.edtEmail)
                    ShowNoBorders(this@SignupScreen, mBinding.edtSpecializations)
                    ShowNoBorders(this@SignupScreen, mBinding.edtYearsOfExp)
                    ShowNoBorders(this@SignupScreen, mBinding.edtOfficeNum)
                    ShowNoBorders(this@SignupScreen, mBinding.edtMobileNum)
                    ShowNoBorders(this@SignupScreen, mBinding.edtPass)
                    ShowRedBorders(this@SignupScreen, mBinding.edtConPass)
                }

                override fun passwordSpecialValidation() {
                    displayMessageDialog(
                        this@SignupScreen,
                        "",
                        resources.getString(R.string.not_match_con_pass),
                        false,
                        "OK",
                        ""
                    )
                    ShowNoBorders(this@SignupScreen, mBinding.edtFullname)
                    ShowNoBorders(this@SignupScreen, mBinding.edtEmail)
                    ShowNoBorders(this@SignupScreen, mBinding.edtSpecializations)
                    ShowNoBorders(this@SignupScreen, mBinding.edtYearsOfExp)
                    ShowNoBorders(this@SignupScreen, mBinding.edtOfficeNum)
                    ShowNoBorders(this@SignupScreen, mBinding.edtMobileNum)
                    ShowNoBorders(this@SignupScreen, mBinding.edtConPass)
                    ShowRedBorders(this@SignupScreen, mBinding.edtPass)

                }

                override fun confpasswordSpecialValidation() {
                    displayMessageDialog(
                        this@SignupScreen,
                        "",
                        resources.getString(R.string.not_match_con_pass),
                        false,
                        "OK",
                        ""
                    )
                    ShowNoBorders(this@SignupScreen, mBinding.edtFullname)
                    ShowNoBorders(this@SignupScreen, mBinding.edtEmail)
                    ShowNoBorders(this@SignupScreen, mBinding.edtSpecializations)
                    ShowNoBorders(this@SignupScreen, mBinding.edtYearsOfExp)
                    ShowNoBorders(this@SignupScreen, mBinding.edtOfficeNum)
                    ShowNoBorders(this@SignupScreen, mBinding.edtMobileNum)
                    ShowNoBorders(this@SignupScreen, mBinding.edtPass)
                    ShowRedBorders(this@SignupScreen, mBinding.edtConPass)
                }

                override fun matchPassowrds() {
                    displayMessageDialog(
                        this@SignupScreen,
                        "",
                        resources.getString(R.string.not_match_con_pass),
                        false,
                        "OK",
                        ""
                    )
                    ShowNoBorders(this@SignupScreen, mBinding.edtFullname)
                    ShowNoBorders(this@SignupScreen, mBinding.edtEmail)
                    ShowNoBorders(this@SignupScreen, mBinding.edtSpecializations)
                    ShowNoBorders(this@SignupScreen, mBinding.edtYearsOfExp)
                    ShowNoBorders(this@SignupScreen, mBinding.edtOfficeNum)
                    ShowNoBorders(this@SignupScreen, mBinding.edtMobileNum)
                    ShowRedBorders(this@SignupScreen, mBinding.edtPass)
                    ShowRedBorders(this@SignupScreen, mBinding.edtConPass)
                }

                override fun empty_provience() {
                    displayMessageDialog(
                        this@SignupScreen,
                        "",
                        resources.getString(R.string.empty_state),
                        false,
                        "OK",
                        ""
                    )
                    ShowNoBorders(this@SignupScreen, mBinding.edtFullname)
                    ShowNoBorders(this@SignupScreen, mBinding.edtEmail)
                    ShowNoBorders(this@SignupScreen, mBinding.edtSpecializations)
                    ShowNoBorders(this@SignupScreen, mBinding.edtYearsOfExp)
                    ShowNoBorders(this@SignupScreen, mBinding.edtOfficeNum)
                    ShowNoBorders(this@SignupScreen, mBinding.edtMobileNum)
                    ShowNoBorders(this@SignupScreen, mBinding.edtPass)
                    ShowNoBorders(this@SignupScreen, mBinding.edtConPass)
                    ShowRedBorders(this@SignupScreen, mBinding.edtProvience)

                }

                override fun valid_state() {
                    displayMessageDialog(
                        this@SignupScreen,
                        "",
                        resources.getString(R.string.valid_state),
                        false,
                        "OK",
                        ""
                    )
                    ShowNoBorders(this@SignupScreen, mBinding.edtFullname)
                    ShowNoBorders(this@SignupScreen, mBinding.edtEmail)
                    ShowNoBorders(this@SignupScreen, mBinding.edtSpecializations)
                    ShowNoBorders(this@SignupScreen, mBinding.edtYearsOfExp)
                    ShowNoBorders(this@SignupScreen, mBinding.edtOfficeNum)
                    ShowNoBorders(this@SignupScreen, mBinding.edtMobileNum)
                    ShowNoBorders(this@SignupScreen, mBinding.edtPass)
                    ShowNoBorders(this@SignupScreen, mBinding.edtConPass)
                    ShowRedBorders(this@SignupScreen, mBinding.edtProvience)
                }

                override fun empty_postal_code() {
                    displayMessageDialog(
                        this@SignupScreen,
                        "",
                        resources.getString(R.string.valid_state),
                        false,
                        "OK",
                        ""
                    )
                    ShowNoBorders(this@SignupScreen, mBinding.edtFullname)
                    ShowNoBorders(this@SignupScreen, mBinding.edtEmail)
                    ShowNoBorders(this@SignupScreen, mBinding.edtSpecializations)
                    ShowNoBorders(this@SignupScreen, mBinding.edtYearsOfExp)
                    ShowNoBorders(this@SignupScreen, mBinding.edtOfficeNum)
                    ShowNoBorders(this@SignupScreen, mBinding.edtMobileNum)
                    ShowNoBorders(this@SignupScreen, mBinding.edtPass)
                    ShowNoBorders(this@SignupScreen, mBinding.edtConPass)
                    ShowNoBorders(this@SignupScreen, mBinding.edtProvience)
                    ShowRedBorders(this@SignupScreen, mBinding.edtProvience)
                }

                override fun valid_postal_code() {
                    displayMessageDialog(
                        this@SignupScreen,
                        "",
                        resources.getString(R.string.valid_state),
                        false,
                        "OK",
                        ""
                    )
                    ShowNoBorders(this@SignupScreen, mBinding.edtFullname)
                    ShowNoBorders(this@SignupScreen, mBinding.edtEmail)
                    ShowNoBorders(this@SignupScreen, mBinding.edtSpecializations)
                    ShowNoBorders(this@SignupScreen, mBinding.edtYearsOfExp)
                    ShowNoBorders(this@SignupScreen, mBinding.edtOfficeNum)
                    ShowNoBorders(this@SignupScreen, mBinding.edtMobileNum)
                    ShowNoBorders(this@SignupScreen, mBinding.edtPass)
                    ShowNoBorders(this@SignupScreen, mBinding.edtConPass)
                    ShowNoBorders(this@SignupScreen, mBinding.edtProvience)
                    ShowRedBorders(this@SignupScreen, mBinding.edtProvience)
                }

                override fun licencNum_empty() {
                    displayMessageDialog(
                        this@SignupScreen,
                        "",
                        resources.getString(R.string.empty_licence_num),
                        false,
                        "OK",
                        ""
                    )
                    ShowNoBorders(this@SignupScreen, mBinding.edtFullname)
                    ShowNoBorders(this@SignupScreen, mBinding.edtEmail)
                    ShowNoBorders(this@SignupScreen, mBinding.edtSpecializations)
                    ShowNoBorders(this@SignupScreen, mBinding.edtYearsOfExp)
                    ShowNoBorders(this@SignupScreen, mBinding.edtOfficeNum)
                    ShowNoBorders(this@SignupScreen, mBinding.edtMobileNum)
                    ShowNoBorders(this@SignupScreen, mBinding.edtPass)
                    ShowNoBorders(this@SignupScreen, mBinding.edtConPass)
                    ShowNoBorders(this@SignupScreen, mBinding.edtProvience)
                    ShowNoBorders(this@SignupScreen, mBinding.edtProvience)
                    ShowRedBorders(this@SignupScreen, mBinding.edtRegisteredLicenceNum)
                }

                override fun licencNumvalidations() {
                    displayMessageDialog(
                        this@SignupScreen,
                        "",
                        resources.getString(R.string.valid_licence_num),
                        false,
                        "OK",
                        ""
                    )
                    ShowNoBorders(this@SignupScreen, mBinding.edtFullname)
                    ShowNoBorders(this@SignupScreen, mBinding.edtEmail)
                    ShowNoBorders(this@SignupScreen, mBinding.edtSpecializations)
                    ShowNoBorders(this@SignupScreen, mBinding.edtYearsOfExp)
                    ShowNoBorders(this@SignupScreen, mBinding.edtOfficeNum)
                    ShowNoBorders(this@SignupScreen, mBinding.edtMobileNum)
                    ShowNoBorders(this@SignupScreen, mBinding.edtPass)
                    ShowNoBorders(this@SignupScreen, mBinding.edtConPass)
                    ShowNoBorders(this@SignupScreen, mBinding.edtProvience)
                    ShowNoBorders(this@SignupScreen, mBinding.edtProvience)
                    ShowRedBorders(this@SignupScreen, mBinding.edtRegisteredLicenceNum)

                }

//                override fun licencPlateLength() {
//                    displayMessageDialog(this@SignupScreen, "", resources.getString(R.string.empty_licence), false, "OK", "")
//                    ShowRedBorders(this@SignupScreen, mBinding.edtVehicalNum)
//
//                }

                override fun docValidations() {
                    displayMessageDialog(
                        this@SignupScreen,
                        "",
                        resources.getString(R.string.valid_doc),
                        false,
                        "OK",
                        ""
                    )
                }

                override fun success() {
                    ShowNoBorders(this@SignupScreen, mBinding.edtFullname)
                    ShowNoBorders(this@SignupScreen, mBinding.edtEmail)
                    ShowNoBorders(this@SignupScreen, mBinding.edtSpecializations)
                    ShowNoBorders(this@SignupScreen, mBinding.edtYearsOfExp)
                    ShowNoBorders(this@SignupScreen, mBinding.edtOfficeNum)
                    ShowNoBorders(this@SignupScreen, mBinding.edtMobileNum)
                    ShowNoBorders(this@SignupScreen, mBinding.edtPass)
                    ShowNoBorders(this@SignupScreen, mBinding.edtConPass)
                    ShowNoBorders(this@SignupScreen, mBinding.edtProvience)
                    ShowNoBorders(this@SignupScreen, mBinding.edtProvience)
                    ShowRedBorders(this@SignupScreen, mBinding.edtRegisteredLicenceNum)
//
//                    chatRegistration(
//                        mBinding.edtEmail.text?.trim().toString(),
//                        mBinding.edtPass.text?.trim().toString()
//                    )
                    callApi("ABEk231daswe5")
                }

            }
        )
    }

    private fun callSpecializationAPI() {
        if (isNetworkConnected(this)) {
            mViewModel.getSpecializationList(true, this)
        } else {
            mBinding.noInternetSignUp.llNointernet.visible()
            mBinding.nsSignUp.gone()
            mBinding.noDataSignup.gone()
        }
    }

    private fun callApi(firebaseUUID: String) {
        if (isNetworkConnected(this)) {
            mViewModel.signUp(
                true,
                this,
                is_lawyer,
                is_mediator,
                mBinding.edtFullname.text?.trim().toString(),
                mBinding.edtEmail.text?.trim().toString(),
                mBinding.edtSpecializations.text?.trim().toString(),
                mBinding.edtYearsOfExp.text?.trim().toString(),
                mBinding.ccpOffice.selectedCountryCode.toString(),
                mBinding.edtOfficeNum.text?.trim()
                    .toString(),
                mBinding.edtPass.text?.trim().toString(),
                mBinding.edtConPass.text?.trim().toString(),
                mBinding.ccp.selectedCountryCode.toString(),
                mBinding.edtMobileNum.text?.trim()
                    .toString(),
                mBinding.edtProvience.text?.trim().toString(),
                mBinding.edtPostalCode.text?.trim().toString(),
                mBinding.edtRegisteredLicenceNum.text?.trim().toString(),
                profile_img,
                images,
                DEVICE_TOKEN.toString(),
                firebaseUUID
            )
        } else {
            mBinding.noInternetSignUp.llNointernet.visible()
            mBinding.nsSignUp.gone()
            mBinding.noDataSignup.gone()
            showLoadingIndicator(false)
        }
    }

//    private fun initializeAutocompleteTextView() {
//        val apiKey = getString(R.string.map_api_key)
//        if (apiKey.isEmpty()) {
//            responseView!!.text = "error"
//            return
//        }
//
//        // Setup Places Client
//
//        // Setup Places Client
//        if (!Places.isInitialized()) {
//            Places.initialize(applicationContext, apiKey)
//        }
//
//        placesClient = Places.createClient(this)
//
//        mBinding.edtProvience.threshold = 1
//        mBinding.edtPostalCode.threshold = 1
//        mBinding.edtProvience.onItemClickListener = autocompleteClickListener
//        mBinding.edtPostalCode.onItemClickListener = autocompleteClickListener
//        adapter = AutoCompleteAdapter(this, placesClient)
//        mBinding.edtProvience.setAdapter(adapter)
//        mBinding.edtPostalCode.setAdapter(adapter)
//
//
//    }

//    private val autocompleteClickListener =
//        AdapterView.OnItemClickListener { adapterView, view, i, l ->
//            try {
//                val item: AutocompletePrediction? = adapter!!.getItem(i)
//                var placeID: String? = null
//                if (item != null) {
//                    placeID = item.placeId
//                }
//                val placeFields: List<Place.Field> =
//                    listOf(
//                        Place.Field.ID,
//                        Place.Field.NAME,
//                        Place.Field.ADDRESS,
//                        Place.Field.LAT_LNG,
//
//                        )
//                var request: FetchPlaceRequest? = null
//                if (placeID != null) {
//                    request = FetchPlaceRequest.builder(placeID, placeFields).build()
//                }
//                if (request != null) {
//                    placesClient!!.fetchPlace(request)
//                        .addOnSuccessListener { task ->
//                            responseView!!.text =
//                                """${task.place.name}""".trimIndent() + task.place.address
//                        }.addOnFailureListener { e ->
//                            e.printStackTrace()
//                            responseView!!.text = e.message
//                        }
//                }
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            AppConstants.EXTRA_FINE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] === PermissionChecker.PERMISSION_GRANTED) {
                    getLatLong()

                } else {
//                    displayMessage(this, "Please accept permission")
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {

                PROFILE_IMG_CODE -> {
                    val uri: Uri = data?.data!!
                    mBinding.ivProfileImg.setImageURI(uri)
                    profile_img = ImagePicker.getFilePath(data).toString()

                }
                DOCUMENT_CODE -> {
                    images.add(ImagePicker.getFilePath(data).toString())
                    imageAdapter?.notifyDataSetChanged()

                }
            }

//            if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
//
//                when (resultCode) {
//                    Activity.RESULT_OK -> {
//                        data?.let {
//                            val places = Autocomplete.getPlaceFromIntent(data)
//                            val lat_long = places.latLng
//                            getLOC(lat_long?.latitude!!, lat_long?.longitude!!)
//                        }
//                    }
//                    AutocompleteActivity.RESULT_ERROR -> {
//                        // TODO: Handle the error.
//                        data?.let {
//                            val status = Autocomplete.getStatusFromIntent(data)
//                            Log.i("TAG_Place_Error", status.statusMessage.toString())
//                        }
//                    }
//                    Activity.RESULT_CANCELED -> {
//                        // The user canceled the operation.
//                    }
//                }
//                return
//            }
        }
    }

    private fun getLOC(MyLat: Double, MyLong: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(MyLat, MyLong, 100)
//
//        Log.i("THIS_APP",addresses[0].adminArea)
//        Log.i("THIS_APP",addresses[0].countryCode)
//        Log.i("THIS_APP",addresses[0].countryName)
//        Log.i("THIS_APP",addresses[0].featureName)
//        Log.i("THIS_APP",addresses[0].locality)
//        Log.i("THIS_APP",addresses[0].postalCode)
        mBinding.edtPostalCode.setText(addresses[0].postalCode)
        mBinding.edtProvience.setText(addresses[0].locality + "/" + addresses[0].adminArea)


    }

    private fun getLatLong() {

        if (checkLoationPermission(this)) {

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0!!)

                    if (p0.equals(null)) {

                        return
                    }
                    for (location in p0.locations) {
                        if (location != null) {

                            Log.i("THIS_APP", location.latitude.toString())
                            Log.i("THIS_APP", location.longitude.toString())
                            getLOC(location.latitude, location.longitude)

                            if (mFusedLocationClient != null) {
                                mFusedLocationClient?.removeLocationUpdates(locationCallback!!)
                            }
                        }
                    }
                }
            }
        }
    }
}