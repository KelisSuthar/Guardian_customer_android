package com.app.guardian.ui.editProfile

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Looper
import android.os.NetworkOnMainThreadException
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.ClientConfiguration
import com.amazonaws.Protocol
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.options.StorageUploadFileOptions
import com.app.guardian.R
import com.app.guardian.common.*
import com.app.guardian.common.ReusedMethod.Companion.displayMessageDialog
import com.app.guardian.common.ReusedMethod.Companion.getAddress
import com.app.guardian.common.extentions.checkLoationPermission
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.loadImage
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.ActivityEditProfileBinding
import com.app.guardian.model.Editprofile.UserDetailsResp
import com.app.guardian.model.specializationList.SpecializationListResp
import com.app.guardian.model.viewModels.AuthenticationViewModel
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.ui.Login.LoginActivity
import com.app.guardian.ui.signup.adapter.ImageAdapter
import com.app.guardian.ui.signup.adapter.SpecializationAdapter
import com.app.guardian.utils.ApiConstant
import com.app.guardian.utils.Config
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.*
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs


class EditProfileActivity : BaseActivity(), View.OnClickListener {
    private val mViewModel: CommonScreensViewModel by viewModel()
    private val authenticationViewModel: AuthenticationViewModel by viewModel()

    lateinit var mBinding: ActivityEditProfileBinding
    var imageAdapter: ImageAdapter? = null
    var images = ArrayList<String>()
    var upload_img_array = ArrayList<String>()
    var specializationList = ArrayList<SpecializationListResp>()
    var PROFILE_IMG_CODE = 101
    var DOCUMENT_CODE = 102
    var profile_img = ""
    var is_lawyer = false

    var is_mediator = false
    var is_user = false
    var selectedid = -1
    var selectedFile: File? = null
    var attachmentUrl = ""

    private var uploadedImageList: ArrayList<String>? = arrayListOf()

    private var locationManager: LocationManager? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return R.layout.activity_edit_profile
    }

    override fun initView() {
        mBinding = getBinding()
        mBinding.noInternetEdit.llNointernet.gone()
        mBinding.noDataEdit.gone()
        mBinding.ns.visible()
        setAdaper()
        callGetuserDetailsApi()
        mBinding.ccp1.setCountryForPhoneCode(1)
        mBinding.ccp2.setCountryForPhoneCode(1)

        locationManager = getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest?.interval = 20 * 1000

        mBinding.headderEdit.tvHeaderText.text = resources.getString(R.string.edit_profile)
        when {
            SharedPreferenceManager.getString(
                AppConstants.USER_ROLE,
                AppConstants.APP_ROLE_USER
            ) == AppConstants.APP_ROLE_USER -> {
                is_user = true

            }
            SharedPreferenceManager.getString(
                AppConstants.USER_ROLE,
                AppConstants.APP_ROLE_USER
            ) == AppConstants.APP_ROLE_LAWYER -> {
                is_lawyer = true
                mBinding.edtSpecializations.visible()
                mBinding.clayout1.visible()
                mBinding.edtYearsOfExp.visible()
                mBinding.edtRegisteredLicenceNum.visible()
                mBinding.llAvaibilityTime.visible()
                mBinding.edtRegisteredLicenceNum.hint = "Registered Licence No"
                mBinding.edtDesc.visible()
            }
            SharedPreferenceManager.getString(
                AppConstants.USER_ROLE,
                AppConstants.APP_ROLE_USER
            ) == AppConstants.APP_ROLE_MEDIATOR -> {
                is_mediator = true
                mBinding.edtSpecializations.visible()
                mBinding.edtYearsOfExp.visible()
                mBinding.llAvaibilityTime.visible()
                mBinding.edtDesc.visible()
            }
        }
        //Check ROle

        setFocus()
    }

    private fun setFocus() {
        mBinding.edtFullname.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                val value = mBinding.edtFullname.text?.trim().toString()

                if (!hasFocus) {
                    if (value.length > 3 && !TextUtils.isEmpty(value)) {
                        ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtFullname)
                    }
                }
            }

        mBinding.edtEmail.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                val value = mBinding.edtEmail.text?.trim().toString()

                if (!hasFocus) {
                    if (SmartUtils.emailValidator(value) && !TextUtils.isEmpty(value)) {
                        ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtEmail)
                    }
                }
            }

        mBinding.edtPhone.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                val value = mBinding.edtPhone.text?.trim().toString()

                if (!hasFocus) {
                    if (value.length >= 10 && !TextUtils.isEmpty(value)) {
                        ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtPhone)
                    }
                }
            }


        mBinding.edtProvience.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                val value = mBinding.edtProvience.text?.trim().toString()
                if (!hasFocus) {

                    if (!TextUtils.isEmpty(value)) {
                        ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtProvience)
                    }
                }
            }

        mBinding.edtPostalCode.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                val value = mBinding.edtPostalCode.text?.trim().toString()
                if (!hasFocus) {

                    if (!TextUtils.isEmpty(value) && (value.length > 3 || value.length < 9)) {
                        ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtPostalCode)
                    }
                }
            }
        mBinding.edtRegisteredLicenceNum.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                val value = mBinding.edtRegisteredLicenceNum.text?.trim().toString()
                if (!hasFocus) {

                    if (!TextUtils.isEmpty(value) || value.length < 5) {
                        ReusedMethod.ShowNoBorders(
                            this@EditProfileActivity,
                            mBinding.edtRegisteredLicenceNum
                        )
                    }
                }
            }

        mBinding.edtSpecializations.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                val value = mBinding.edtSpecializations.text?.trim().toString()
                if (!hasFocus) {
                    if (!TextUtils.isEmpty(value)) {
                        ReusedMethod.ShowNoBorders(
                            this@EditProfileActivity,
                            mBinding.edtSpecializations
                        )
                    }
                }
            }

        mBinding.edtYearsOfExp.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                val value = mBinding.edtYearsOfExp.text?.trim().toString()
                if (!hasFocus) {
                    if (!TextUtils.isEmpty(value) && value != "0") {
                        ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtYearsOfExp)
                    }
                }
            }

        mBinding.edtOfficeNum.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                val value = mBinding.edtOfficeNum.text?.trim().toString()
                if (!hasFocus) {
                    if (!TextUtils.isEmpty(value) && value.length == 10) {
                        ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtOfficeNum)
                    }
                }
            }
        mBinding.edtFromTime.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                val value = mBinding.edtFromTime.text?.trim().toString()
                if (!hasFocus) {
                    if (!TextUtils.isEmpty(value)) {
                        ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtFromTime)
                    }
                }
            }
        mBinding.edtToTime.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                val value = mBinding.edtToTime.text?.trim().toString()
                if (!hasFocus) {
                    if (!TextUtils.isEmpty(value)) {
                        ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtToTime)
                    }
                }
            }
        mBinding.edtDesc.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                val value = mBinding.edtDesc.text?.trim().toString()
                if (!hasFocus) {
                    if (!TextUtils.isEmpty(value) || value.length > 50) {
                        ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtDesc)
                    }
                }
            }
    }

    private fun callGetuserDetailsApi() {
        if (ReusedMethod.isNetworkConnected(this)) {
            mViewModel.getUserDetials(true, this)
        } else {
            mBinding.noDataEdit.gone()
            mBinding.ns.gone()
            mBinding.noInternetEdit.llNointernet.visible()
        }
    }

    override fun onResume() {
        super.onResume()
        getLatLong()
        if (SharedPreferenceManager.getString(
                AppConstants.USER_ROLE,
                ""
            ) != AppConstants.APP_ROLE_USER
        ) {
            callSpecializationAPI()
        }
        if (checkLoationPermission(this)) {


            if (locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!) {

                mFusedLocationClient?.requestLocationUpdates(
                    locationRequest!!,
                    locationCallback!!,
                    Looper.getMainLooper()
                )
            } else {

                setDialog()
            }
        }


    }


    private fun callSpecializationAPI() {
        if (ReusedMethod.isNetworkConnected(this)) {
            authenticationViewModel.getSpecializationList(true, this)
        } else {
            ReusedMethod.displayMessage(this, resources.getString(R.string.text_error_network))
        }
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

                            val data = getAddress(
                                this@EditProfileActivity,
                                location.latitude,
                                location.longitude
                            )
                            mBinding.edtPostalCode.setText(data[0].postalCode)
                            mBinding.edtProvience.setText(data[0].locality + "/" + data[0].adminArea)
                            if (mFusedLocationClient != null) {
                                mFusedLocationClient?.removeLocationUpdates(locationCallback!!)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.leftto, R.anim.right)
    }

    override fun initObserver() {
        authenticationViewModel.getEditProfileResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            ReusedMethod.displayMessage(this, it.message.toString())
                            val gson = Gson()
                            val json = gson.toJson(data)
                            SharedPreferenceManager.putString(
                                AppConstants.USER_DETAIL_LOGIN,
                                json
                            )
                            Log.e("EDIT_APP", SharedPreferenceManager.getUser().toString())
                            onBackPressed()
                        } else {
                            ReusedMethod.displayMessage(this, it.message.toString())
                        }
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            ReusedMethod.displayMessage(
                                this,
                                getString(R.string.text_error_network)
                            )

                        Config.CUSTOM_ERROR ->
                            errorObj.customMessage
                                ?.let {
                                    if (errorObj.code == ApiConstant.API_401) {
                                        startActivity(
                                            Intent(
                                                this@EditProfileActivity,
                                                LoginActivity::class.java
                                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        )
                                        overridePendingTransition(R.anim.rightto, R.anim.left)
                                    } else {
                                        ReusedMethod.displayMessage(this as Activity, it)
                                    }
                                }
                    }
                }
            }
        }
        mViewModel.getuserDetailsResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            setApiData(data)
                        } else {
                            ReusedMethod.displayMessage(this, it.message.toString())
                        }
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            ReusedMethod.displayMessage(
                                this,
                                getString(R.string.text_error_network)
                            )

                        Config.CUSTOM_ERROR ->
                            errorObj.customMessage
                                ?.let {
                                    if (errorObj.code == ApiConstant.API_401) {
                                        startActivity(
                                            Intent(
                                                this@EditProfileActivity,
                                                LoginActivity::class.java
                                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        )
                                        overridePendingTransition(R.anim.rightto, R.anim.left)
                                    } else {
                                        ReusedMethod.displayMessage(this as Activity, it)
                                    }
                                }
                    }
                }
            }
        }
        //SPECIALIZATION LIST RESP
        authenticationViewModel.getSpecializationListResp().observe(this) { response ->
            response?.let { requestState ->

                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        specializationList.clear()
                        if (it.status) {
                            specializationList.addAll(data)
                            if (specializationList.isNotEmpty()) {
                                for (i in specializationList.indices) {
                                    if (specializationList[i].title == mBinding.edtSpecializations.text.toString()) {
                                        selectedid == specializationList[i].id
                                    }
                                }
                            }
                        } else {
                            ReusedMethod.displayMessage(this, it.message.toString())
                        }
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            ReusedMethod.displayMessage(
                                this,
                                getString(R.string.text_error_network)
                            )

                        Config.CUSTOM_ERROR ->
                            errorObj.customMessage
                                ?.let {
                                    if (errorObj.code == ApiConstant.API_401) {

                                        startActivity(
                                            Intent(
                                                this@EditProfileActivity,
                                                LoginActivity::class.java
                                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        )
                                        overridePendingTransition(R.anim.rightto, R.anim.left)
                                    } else {
                                        ReusedMethod.displayMessage(this as Activity, it)
                                    }
                                }
                    }
                }
            }
        }
    }

    private fun setApiData(data: UserDetailsResp) {
        mBinding.edtFullname.setText(data.full_name)
        mBinding.edtEmail.setText(data.email)
        if (is_mediator) {
            mBinding.edtSpecializations.setText(data.specialization)
            mBinding.edtYearsOfExp.setText(data.years_of_experience)
            if (!data.availability_time.isNullOrEmpty()) {
                mBinding.edtFromTime.setText(data.availability_time.substringBefore("to"))
                mBinding.edtToTime.setText(data.availability_time.substringAfter("to"))
            }
            if (!data.description.isNullOrEmpty()) {
                mBinding.edtDesc.setText(data.description)
            }


        } else if (is_lawyer) {
            mBinding.edtSpecializations.setText(data.specialization)
            mBinding.edtYearsOfExp.setText(data.years_of_experience)
            mBinding.edtOfficeNum.setText(data.office_phone)
            data.office_dialing_code?.toInt()?.let { mBinding.ccp1.setCountryForPhoneCode(it) }
            if (!data.availability_time.isNullOrEmpty()) {
                mBinding.edtFromTime.setText(data.availability_time.substringBefore("to"))
                mBinding.edtToTime.setText(data.availability_time.substringAfter("to"))
            }
            if (!data.description.isNullOrEmpty()) {
                mBinding.edtDesc.setText(data.description)
            }
        }
        data.dialing_code?.toInt()?.let { mBinding.ccp2.setCountryForPhoneCode(it) }
        mBinding.edtPhone.setText(data.phone)
        mBinding.edtProvience.setText(data.state)
        mBinding.edtPostalCode.setText(data.postal_code)
        mBinding.edtRegisteredLicenceNum.setText(data.licence_no)

        if (data.profile_avatar != "null" || data.profile_avatar.isNotEmpty()) {
            profile_img = data.profile_avatar.toString()
            mBinding.ivProfileImg.loadImage(data.profile_avatar)
            Glide.with(this)
                .load(data.profile_avatar).placeholder(R.drawable.profile)
                .into(mBinding.ivProfileImg)
//            mBinding.ivProfileImg.loadImage("https://wallpaperaccess.com/full/251618.jpg")
        }


        if (data.user_doc.isNotEmpty()) {
            for (i in data.user_doc.indices) {
                images.add(data.user_doc[i].document.toString())
                upload_img_array?.add(data.user_doc[i].document.toString())
//                upload_img_array.add(data.user_doc[i].document.toString())
                imageAdapter?.notifyDataSetChanged()
            }

        }
    }

    override fun handleListener() {
        mBinding.ll1.setOnClickListener(this)
        mBinding.ivAddImage.setOnClickListener(this)
        mBinding.btnSubmit.setOnClickListener(this)
        mBinding.edtSpecializations.setOnClickListener(this)
        mBinding.edtFromTime.setOnClickListener(this)
        mBinding.edtToTime.setOnClickListener(this)
        mBinding.headderEdit.ivBack.setOnClickListener(this)
        mBinding.noInternetEdit.btnTryAgain.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.edtFromTime -> {
                selectTime(mBinding.edtFromTime)
            }
            R.id.edtToTime -> {
                selectTime(mBinding.edtToTime)
            }
            R.id.btnTryAgain -> {
//                callApi()
            }
            R.id.edtSpecializations -> {
                specializationDialog()
            }
            R.id.ivBack -> {
                onBackPressed()
                overridePendingTransition(R.anim.leftto, R.anim.right)
            }
            R.id.ll1 -> {
                ImagePicker.with(this)
                    .compress(1024)
                    .maxResultSize(
                        1080,
                        1080
                    )
                    .start(PROFILE_IMG_CODE)

            }

            R.id.ivAddImage -> {

                if (images.size < 5) {
                    ImagePicker.with(this)
                        .compress(1024)
                        .maxResultSize(
                            1080,
                            1080
                        )
                        .start(DOCUMENT_CODE)

                } else {
                    displayMessageDialog(
                        this,
                        "",
                        "Maximum Image Limit Reached",
                        false,
                        "Ok",
                        ""
                    )
                }

            }
            R.id.btnSubmit -> {

                validations()
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
        IntegratorImpl.isValidEdit(
            is_lawyer,
            is_mediator,
            profile_img,
            mBinding.edtFullname.text?.trim().toString(),
            mBinding.edtEmail.text?.trim().toString(),
            mBinding.edtPhone.text?.trim().toString(),
            mBinding.edtSpecializations.text?.trim().toString(),
            mBinding.edtYearsOfExp.text?.trim().toString(),
            mBinding.edtProvience.text?.trim().toString(),
            mBinding.edtPostalCode.text?.trim().toString(),
            mBinding.edtFromTime.text?.trim().toString(),
            mBinding.edtToTime.text?.trim().toString(),
            mBinding.edtDesc.text?.trim().toString(),
            upload_img_array,
            object : ValidationView.EditProfile {
                override fun empty_profilePic() {
                    displayMessageDialog(
                        this@EditProfileActivity,
                        "",
                        resources.getString(R.string.empty_profile_pic),
                        false,
                        "OK",
                        ""
                    )
                }

                override fun fullname_empty() {
                    displayMessageDialog(
                        this@EditProfileActivity,
                        "",
                        resources.getString(R.string.empty_name),
                        false,
                        "OK",
                        ""
                    )
                    ReusedMethod.ShowRedBorders(this@EditProfileActivity, mBinding.edtFullname)
                }

                override fun fulllNameValidation() {
                    displayMessageDialog(
                        this@EditProfileActivity,
                        "",
                        resources.getString(R.string.valid_name),
                        false,
                        "OK",
                        ""
                    )
                    ReusedMethod.ShowRedBorders(this@EditProfileActivity, mBinding.edtFullname)
                }

                override fun email_empty() {
                    displayMessageDialog(
                        this@EditProfileActivity,
                        "",
                        resources.getString(R.string.empty_email),
                        false,
                        "OK",
                        ""
                    )
                    ReusedMethod.ShowRedBorders(this@EditProfileActivity, mBinding.edtEmail)
                }

                override fun emailValidation() {
                    displayMessageDialog(
                        this@EditProfileActivity,
                        "",
                        resources.getString(R.string.valid_email),
                        false,
                        "OK",
                        ""
                    )
                    ReusedMethod.ShowRedBorders(this@EditProfileActivity, mBinding.edtEmail)
                }

                override fun empty_specialization() {
                    displayMessageDialog(
                        this@EditProfileActivity,
                        "",
                        resources.getString(R.string.empty_specialization),
                        false,
                        "OK",
                        ""
                    )
                    ReusedMethod.ShowRedBorders(
                        this@EditProfileActivity,
                        mBinding.edtSpecializations
                    )
                }

                override fun empty_years_exp() {
                    displayMessageDialog(
                        this@EditProfileActivity,
                        "",
                        resources.getString(R.string.empty_exp),
                        false,
                        "OK",
                        ""
                    )
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtFullname)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtEmail)
                    ReusedMethod.ShowNoBorders(
                        this@EditProfileActivity,
                        mBinding.edtSpecializations
                    )
                    ReusedMethod.ShowRedBorders(this@EditProfileActivity, mBinding.edtYearsOfExp)
                }

                override fun valid_years_exp() {
                    displayMessageDialog(
                        this@EditProfileActivity,
                        "",
                        resources.getString(R.string.valid_exp),
                        false,
                        "OK",
                        ""
                    )
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtFullname)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtEmail)
                    ReusedMethod.ShowNoBorders(
                        this@EditProfileActivity,
                        mBinding.edtSpecializations
                    )
                    ReusedMethod.ShowRedBorders(this@EditProfileActivity, mBinding.edtYearsOfExp)
                }

                override fun moNumber_empty() {
                    displayMessageDialog(
                        this@EditProfileActivity,
                        "",
                        resources.getString(R.string.empty_number),
                        false,
                        "OK",
                        ""
                    )
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtFullname)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtEmail)
                    ReusedMethod.ShowNoBorders(
                        this@EditProfileActivity,
                        mBinding.edtSpecializations
                    )
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtYearsOfExp)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtOfficeNum)
                    ReusedMethod.ShowRedBorders(this@EditProfileActivity, mBinding.edtPhone)
                }

                override fun moNumberValidation() {
                    displayMessageDialog(
                        this@EditProfileActivity,
                        "",
                        resources.getString(R.string.valid_pass),
                        false,
                        "OK",
                        ""
                    )
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtFullname)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtEmail)
                    ReusedMethod.ShowNoBorders(
                        this@EditProfileActivity,
                        mBinding.edtSpecializations
                    )
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtYearsOfExp)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtOfficeNum)
                    ReusedMethod.ShowRedBorders(this@EditProfileActivity, mBinding.edtPhone)

                }


                override fun empty_provience() {
                    displayMessageDialog(
                        this@EditProfileActivity,
                        "",
                        resources.getString(R.string.empty_state),
                        false,
                        "OK",
                        ""
                    )
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtFullname)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtEmail)
                    ReusedMethod.ShowNoBorders(
                        this@EditProfileActivity,
                        mBinding.edtSpecializations
                    )
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtYearsOfExp)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtOfficeNum)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtPhone)
                    ReusedMethod.ShowRedBorders(this@EditProfileActivity, mBinding.edtProvience)

                }

                override fun valid_state() {
                    displayMessageDialog(
                        this@EditProfileActivity,
                        "",
                        resources.getString(R.string.valid_state),
                        false,
                        "OK",
                        ""
                    )
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtFullname)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtEmail)
                    ReusedMethod.ShowNoBorders(
                        this@EditProfileActivity,
                        mBinding.edtSpecializations
                    )
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtYearsOfExp)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtOfficeNum)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtPhone)
                    ReusedMethod.ShowRedBorders(this@EditProfileActivity, mBinding.edtProvience)
                }

                override fun empty_postal_code() {
                    displayMessageDialog(
                        this@EditProfileActivity,
                        "",
                        resources.getString(R.string.valid_state),
                        false,
                        "OK",
                        ""
                    )
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtFullname)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtEmail)
                    ReusedMethod.ShowNoBorders(
                        this@EditProfileActivity,
                        mBinding.edtSpecializations
                    )
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtYearsOfExp)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtOfficeNum)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtPhone)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtProvience)
                    ReusedMethod.ShowRedBorders(this@EditProfileActivity, mBinding.edtProvience)
                }

                override fun valid_postal_code() {
                    displayMessageDialog(
                        this@EditProfileActivity,
                        "",
                        resources.getString(R.string.valid_state),
                        false,
                        "OK",
                        ""
                    )
                    ReusedMethod.ShowRedBorders(this@EditProfileActivity, mBinding.edtProvience)
                }

                override fun empty_from_time() {
                    displayMessageDialog(
                        this@EditProfileActivity,
                        "",
                        resources.getString(R.string.valid_licence_num),
                        false,
                        "OK",
                        ""
                    )
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtFullname)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtEmail)
                    ReusedMethod.ShowNoBorders(
                        this@EditProfileActivity,
                        mBinding.edtSpecializations
                    )
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtYearsOfExp)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtOfficeNum)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtPhone)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtProvience)
                    ReusedMethod.ShowNoBorders(
                        this@EditProfileActivity,
                        mBinding.edtRegisteredLicenceNum
                    )
                    ReusedMethod.ShowRedBorders(this@EditProfileActivity, mBinding.edtFromTime)
                }

                override fun empty_to_time() {
                    displayMessageDialog(
                        this@EditProfileActivity,
                        "",
                        resources.getString(R.string.empty_to_time),
                        false,
                        "OK",
                        ""
                    )
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtFullname)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtEmail)
                    ReusedMethod.ShowNoBorders(
                        this@EditProfileActivity,
                        mBinding.edtSpecializations
                    )
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtYearsOfExp)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtOfficeNum)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtPhone)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtProvience)
                    ReusedMethod.ShowNoBorders(
                        this@EditProfileActivity,
                        mBinding.edtRegisteredLicenceNum
                    )
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtFromTime)
                    ReusedMethod.ShowRedBorders(this@EditProfileActivity, mBinding.edtToTime)
                }

                override fun empty_desc() {
                    displayMessageDialog(
                        this@EditProfileActivity,
                        "",
                        resources.getString(R.string.empty_desc),
                        false,
                        "OK",
                        ""
                    )
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtFullname)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtEmail)
                    ReusedMethod.ShowNoBorders(
                        this@EditProfileActivity,
                        mBinding.edtSpecializations
                    )
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtYearsOfExp)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtOfficeNum)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtProvience)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtProvience)
                    ReusedMethod.ShowNoBorders(
                        this@EditProfileActivity,
                        mBinding.edtRegisteredLicenceNum
                    )
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtFromTime)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtToTime)
                    ReusedMethod.ShowRedBorders(this@EditProfileActivity, mBinding.edtDesc)
                }

                override fun valid_desc() {
                    displayMessageDialog(
                        this@EditProfileActivity,
                        "",
                        resources.getString(R.string.valid_desc),
                        false,
                        "OK",
                        ""
                    )
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtFullname)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtEmail)
                    ReusedMethod.ShowNoBorders(
                        this@EditProfileActivity,
                        mBinding.edtSpecializations
                    )
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtYearsOfExp)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtOfficeNum)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtProvience)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtProvience)
                    ReusedMethod.ShowNoBorders(
                        this@EditProfileActivity,
                        mBinding.edtRegisteredLicenceNum
                    )
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtFromTime)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtToTime)
                    ReusedMethod.ShowRedBorders(this@EditProfileActivity, mBinding.edtDesc)
                }


                override fun docValidations() {
                    displayMessageDialog(
                        this@EditProfileActivity,
                        "",
                        resources.getString(R.string.valid_doc),
                        false,
                        "OK",
                        ""
                    )
                }


                override fun success() {

                    if (SharedPreferenceManager.getLoginUserRole() != AppConstants.APP_ROLE_USER) {
                        if (isValidTime()) {
                            ReusedMethod.ShowNoBorders(
                                this@EditProfileActivity,
                                mBinding.edtFullname
                            )
                            ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtEmail)
                            ReusedMethod.ShowNoBorders(
                                this@EditProfileActivity,
                                mBinding.edtSpecializations
                            )
                            ReusedMethod.ShowNoBorders(
                                this@EditProfileActivity,
                                mBinding.edtYearsOfExp
                            )
                            ReusedMethod.ShowNoBorders(
                                this@EditProfileActivity,
                                mBinding.edtOfficeNum
                            )
                            ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtPhone)
                            ReusedMethod.ShowNoBorders(
                                this@EditProfileActivity,
                                mBinding.edtProvience
                            )
                            ReusedMethod.ShowNoBorders(
                                this@EditProfileActivity,
                                mBinding.edtProvience
                            )
                            ReusedMethod.ShowNoBorders(
                                this@EditProfileActivity,
                                mBinding.edtRegisteredLicenceNum
                            )
                            ReusedMethod.ShowNoBorders(
                                this@EditProfileActivity,
                                mBinding.edtFromTime
                            )
                            ReusedMethod.ShowNoBorders(
                                this@EditProfileActivity,
                                mBinding.edtToTime
                            )
                            ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtDesc)
                            var isStorage = false
                            images.forEachIndexed { index, s ->
                                if (images[index].startsWith("/storage/")) {
                                    isStorage = true
                                }
                            }
                            if (!selectedFile?.absolutePath.isNullOrEmpty() && isStorage) {
                                uploadFile(selectedFile, false)
                                uploadMultipleImageFile(images)
                            } else
                                if (!selectedFile?.absolutePath.isNullOrEmpty()) {
                                    uploadFile(selectedFile, true)
                                } else if (isStorage) {
                                    uploadMultipleImageFile(images)
                                } else {
                                    callEditProfileApi()
                                }
                        } else {
                            displayMessageDialog(
                                this@EditProfileActivity,
                                "",
                                "Please enter attest 30min difference time",
                                false,
                                "OK",
                                ""
                            )
                        }
                    } else {
                        ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtFullname)
                        ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtEmail)
                        ReusedMethod.ShowNoBorders(
                            this@EditProfileActivity,
                            mBinding.edtSpecializations
                        )
                        ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtYearsOfExp)
                        ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtOfficeNum)
                        ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtPhone)
                        ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtProvience)
                        ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtProvience)
                        ReusedMethod.ShowNoBorders(
                            this@EditProfileActivity,
                            mBinding.edtRegisteredLicenceNum
                        )
                        ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtFromTime)
                        ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtToTime)
                        ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtDesc)
                        Log.e("IMG_ARRAY", upload_img_array.toString())
                        var isStorage = false
                        images.forEachIndexed { index, s ->
                            if (images[index].startsWith("/storage/")) {
                                isStorage = true
                            }
                        }
                        if (!selectedFile?.absolutePath.isNullOrEmpty()) {
                            uploadFile(selectedFile, true)
                        } else if (isStorage) {
                            uploadMultipleImageFile(images)
                        } else {
                            callEditProfileApi()
                        }

                    }

                }

            }
        )
    }

    private fun isValidTime(): Boolean {
        var days = 0
        var hrs = 0
        var min = 0

        val date2: Date =
            SimpleDateFormat("HH:mm a").parse(mBinding.edtToTime.text?.trim().toString())
        val date1: Date =
            SimpleDateFormat("HH:mm a").parse(mBinding.edtFromTime.text?.trim().toString())
        val difference: Long = date2.time - date1.time

        days = ((difference / (1000 * 60 * 60 * 24)).toInt());
        hrs = (((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60)).toInt());
        min =
            ((difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hrs)) / (1000 * 60)).toInt();
        return !(abs(hrs) < 0 && abs(min) < 30)
    }

    private fun callEditProfileApi() {
        if (ReusedMethod.isNetworkConnected(this)) {
            authenticationViewModel.EditProfile(
                true,
                this,
                is_lawyer,
                is_mediator,
                mBinding.edtFullname.text?.trim().toString(),
                mBinding.edtEmail.text?.trim().toString(),
                mBinding.edtSpecializations.text?.trim().toString(),
                mBinding.edtYearsOfExp.text?.trim().toString(),
                mBinding.ccp1.selectedCountryCode.toString(),
                mBinding.edtOfficeNum.text?.trim()
                    .toString(),
                mBinding.ccp2.selectedCountryCode.toString(),
                mBinding.edtPhone.text?.trim()
                    .toString(),
                mBinding.edtProvience.text?.trim().toString(),
                mBinding.edtPostalCode.text?.trim().toString(),
                mBinding.edtRegisteredLicenceNum.text?.trim().toString(),
                mBinding.edtFromTime.text?.trim()
                    .toString() + " to " + mBinding.edtToTime.text?.trim().toString(),
                mBinding.edtDesc.text?.trim().toString(),
                profile_img,
                upload_img_array,
            )
        } else {
            mBinding.noInternetEdit.llNointernet.visible()
            mBinding.ns.gone()
            mBinding.noDataEdit.gone()
            showLoadingIndicator(false)
        }
    }

    private fun setDialog() {
        val dialog = Dialog(
            this@EditProfileActivity,
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
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun setAdaper() {
        mBinding.rvImage.adapter = null
        imageAdapter = ImageAdapter(this, images, object : ImageAdapter.onItemClicklisteners {
            override fun onCancelCick(position: Int) {
                for (i in upload_img_array.indices) {
                    if (upload_img_array[i] == images[position]) {
                        upload_img_array.removeAt(i)
                    }
                }
                images.removeAt(position)
                imageAdapter?.notifyDataSetChanged()
            }

        })
        mBinding.rvImage.adapter = imageAdapter
    }

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
                    ReusedMethod.displayMessage(this, "Please accept permission")
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
                    selectedFile = ImagePicker.getFile(data)!!
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

    private fun uploadFile(selectedFile: File?, b: Boolean) {
        showLoadingIndicator(true)
        try {
            val options = StorageUploadFileOptions.defaultInstance()
            val clientConfig = ClientConfiguration()
            clientConfig.socketTimeout = 120000
            clientConfig.connectionTimeout = 10000
            clientConfig.maxErrorRetry = 2
            clientConfig.protocol = Protocol.HTTP


            val credentials = BasicAWSCredentials(

                resources.getString(R.string.aws_access_1) + resources.getString(R.string.aws_access_2),
                resources.getString(R.string.aws_sec_1) + resources.getString(R.string.aws_sec_2) + resources.getString(
                    R.string.aws_sec_3
                )
            )
            val s3 = AmazonS3Client(credentials, clientConfig)
            s3.setRegion(Region.getRegion(Regions.US_EAST_2))
            if (!selectedFile?.absolutePath.isNullOrEmpty()) {
                Amplify.Storage.uploadFile(selectedFile?.name.toString(), selectedFile!!, options, {
                    Log.i("MyAmplifyApp", "Fraction completed: ${it.fractionCompleted}")
                },
                    {
                        Log.i("MyAmplifyApp", "Successfully uploaded Single Image: ${it.key}")
                        attachmentUrl =
                            "${resources.getString(R.string.aws_base_url)}${selectedFile?.name}"
                        profile_img =
                            "${resources.getString(R.string.aws_base_url)}${selectedFile?.name}"
                        Log.i("attachmentUrl", attachmentUrl)
                        if (b) {
                            callEditProfileApi()
                        }
                    },
                    {
                        showLoadingIndicator(false)
                        Log.i("MyAmplifyApp", "Upload failed Single Image", it)
                    }
                )
            } else {
                showLoadingIndicator(false)
            }

//            if (imageList.size > 0) {
//                uploadMultipleImageFile(imageList)
//            } else {
//                callEditProfileApi()
//            }


        } catch (exception: Exception) {
            showLoadingIndicator(false)
            Log.i("MyAmplifyApp", "Upload failed", exception)
        } catch (e: NetworkOnMainThreadException) {
            Log.i("MyAmplifyApp", "Upload failed$e.message")
        }
    }

    private fun uploadMultipleImageFile(imageList: ArrayList<String>) {
        showLoadingIndicator(true)
        val clientConfig = ClientConfiguration()
        clientConfig.socketTimeout = 120000
        clientConfig.connectionTimeout = 10000
        clientConfig.maxErrorRetry = 2
        clientConfig.protocol = Protocol.HTTP

        val credentials = BasicAWSCredentials(

            resources.getString(R.string.aws_access_1) + resources.getString(R.string.aws_access_2),
            resources.getString(R.string.aws_sec_1) + resources.getString(R.string.aws_sec_2) + resources.getString(
                R.string.aws_sec_3
            )
        )
        val s3 = AmazonS3Client(credentials, clientConfig)
        s3.setRegion(Region.getRegion(Regions.US_EAST_2))
        var counter = 0
        val imageListSize = imageList.size
        uploadedImageList?.clear()

        imageList.forEachIndexed { index, s ->
            if (imageList[index].startsWith("/storage/")) {

                val image1 = File(s)
                try {

                    val options = StorageUploadFileOptions.defaultInstance()
                    Amplify.Storage.uploadFile(image1.name.toString(), image1, options,
                        {
                            Log.i("MyAmplifyApp", "Fraction completed: ${it.fractionCompleted}")
                        },
                        {
                            counter += 1
                            Log.i("MyAmplifyApp", "Successfully uploaded: ${it.key}")
                            upload_img_array?.add("${resources.getString(R.string.aws_base_url)}${image1.name}")
                            if (imageListSize == (index + 1)) {
                                Log.i("uploadedImageList", uploadedImageList.toString())
                                callEditProfileApi()
                            }
                        },
                        {
                            showLoadingIndicator(false)
                            Log.i("MyAmplifyApp", "Upload failed", it)
                        }
                    )
                } catch (exception: java.lang.Exception) {
                    showLoadingIndicator(false)
                    Log.i("MyAmplifyApp", "Upload failed", exception)
                } catch (e: NetworkOnMainThreadException) {
                    showLoadingIndicator(false)
                    Log.i("MyAmplifyApp", "Upload failed$e.message")
                }
            }
        }


    }

    fun selectTime(txtTime: TextView) {

        var hrsFormatter = 0
        var minFormatter = 0
        if (!txtTime.text.toString().isNullOrEmpty()) {
            hrsFormatter = txtTime.text.toString().substringBefore(":").trim().toInt()
            minFormatter = txtTime.text.toString().substringAfter(":").substring(0, 2).toInt()
        }

        val timePicker = TimePickerDialog(
            this@EditProfileActivity, R.style.DialogTheme,
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
            hrsFormatter,
            // default minute when the time picker
            // dialog is opened
            minFormatter,
            false
        )

        // then after building the timepicker
        // dialog show the dialog to user
        timePicker.show()
        timePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            .setTextColor(
                ContextCompat.getColor(
                    this@EditProfileActivity,
                    R.color.colorPrimaryDark
                )
            )
        timePicker.getButton(DatePickerDialog.BUTTON_NEUTRAL)
            .setTextColor(
                ContextCompat.getColor(
                    this@EditProfileActivity,
                    R.color.colorPrimaryDark
                )
            )
        timePicker.getButton(DatePickerDialog.BUTTON_POSITIVE)
            .setTextColor(
                ContextCompat.getColor(
                    this@EditProfileActivity,
                    R.color.colorPrimaryDark
                )
            )
    }

}