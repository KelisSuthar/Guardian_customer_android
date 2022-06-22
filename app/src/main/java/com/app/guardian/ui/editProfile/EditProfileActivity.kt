package com.app.guardian.ui.editProfile

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.core.content.PermissionChecker
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.common.*
import com.app.guardian.common.ReusedMethod.Companion.getAddress
import com.app.guardian.common.extentions.*
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
import java.util.*


class EditProfileActivity : BaseActivity(), View.OnClickListener {
    private val mViewModel: CommonScreensViewModel by viewModel()
    private val authenticationViewModel: AuthenticationViewModel by viewModel()

    lateinit var mBinding: ActivityEditProfileBinding
    var imageAdapter: ImageAdapter? = null
    var images = ArrayList<String>()
    var specializationList = ArrayList<SpecializationListResp>()
    var PROFILE_IMG_CODE = 101
    var DOCUMENT_CODE = 102
    var profile_img = ""
    var is_lawyer = false
    var is_mediator = false
    var is_user = false
    var selectedid = -1

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
                mBinding.edtRegisteredLicenceNum.hint = "Registered Licence No"
            }
            SharedPreferenceManager.getString(
                AppConstants.USER_ROLE,
                AppConstants.APP_ROLE_USER
            ) == AppConstants.APP_ROLE_MEDIATOR -> {
                is_mediator = true
                mBinding.edtSpecializations.visible()
                mBinding.edtYearsOfExp.visible()
            }
        }
        //Check ROle


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
        } else if (is_lawyer) {
            mBinding.edtSpecializations.setText(data.specialization)
            mBinding.edtYearsOfExp.setText(data.years_of_experience)
            mBinding.edtOfficeNum.setText(data.office_phone)
            mBinding.ccp1.setCountryForPhoneCode(data.office_dialing_code!!)
        }
        mBinding.ccp2.setCountryForPhoneCode(data.dialing_code!!)
        mBinding.edtPhone.setText(data.phone)
        mBinding.edtProvience.setText(data.state)
        mBinding.edtPostalCode.setText(data.postal_code)
        mBinding.edtRegisteredLicenceNum.setText(data.licence_no)

        if (data.profile_avatar != "null" || data.profile_avatar!!.isNotEmpty()) {
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
                imageAdapter?.notifyDataSetChanged()
            }

        }
    }

    override fun handleListener() {
        mBinding.ll1.setOnClickListener(this)
        mBinding.ivAddImage.setOnClickListener(this)
        mBinding.btnSubmit.setOnClickListener(this)
        mBinding.edtSpecializations.setOnClickListener(this)

        mBinding.headderEdit.ivBack.setOnClickListener(this)
        mBinding.noInternetEdit.btnTryAgain.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
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
                    ReusedMethod.displayMessageDialog(
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
            is_lawyer, is_mediator,
            profile_img, mBinding.edtFullname.text?.trim().toString(),
            mBinding.ccp1.selectedCountryCode.toString() + mBinding.edtPhone.text?.trim()
                .toString(),
            mBinding.edtProvience.text?.trim().toString(),
            mBinding.edtPostalCode.text?.trim().toString(),
            images,
            object : ValidationView.EditProfile {
                override fun empty_profilePic() {
                    ReusedMethod.displayMessageDialog(
                        this@EditProfileActivity,
                        "",
                        resources.getString(R.string.empty_profile_pic),
                        false,
                        "OK",
                        ""
                    )
                }

                override fun fullname_empty() {
                    ReusedMethod.displayMessageDialog(
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
                    ReusedMethod.displayMessageDialog(
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
                    TODO("Not yet implemented")
                }

//                override fun email_empty() {
//                    ReusedMethod.displayMessageDialog(
//                        this@EditProfileActivity,
//                        "",
//                        resources.getString(R.string.empty_email),
//                        false,
//                        "OK",
//                        ""
//                    )
//                    ReusedMethod.ShowRedBorders(this@EditProfileActivity, mBinding.edtEmail)
//                }

                override fun emailValidation() {
                    ReusedMethod.displayMessageDialog(
                        this@EditProfileActivity,
                        "",
                        resources.getString(R.string.valid_email),
                        false,
                        "OK",
                        ""
                    )
                    ReusedMethod.ShowRedBorders(this@EditProfileActivity, mBinding.edtEmail)
                }

                override fun moNumber_empty() {
                    ReusedMethod.displayMessageDialog(
                        this@EditProfileActivity,
                        "",
                        resources.getString(R.string.empty_number),
                        false,
                        "OK",
                        ""
                    )
                    ReusedMethod.ShowRedBorders(this@EditProfileActivity, mBinding.edtPhone)
                }

                override fun moNumberValidation() {
                    ReusedMethod.displayMessageDialog(
                        this@EditProfileActivity,
                        "",
                        resources.getString(R.string.valid_number),
                        false,
                        "OK",
                        ""
                    )
                    ReusedMethod.ShowRedBorders(this@EditProfileActivity, mBinding.edtPhone)
                }


                override fun empty_provience() {
                    ReusedMethod.displayMessageDialog(
                        this@EditProfileActivity,
                        "",
                        resources.getString(R.string.empty_state),
                        false,
                        "OK",
                        ""
                    )
                    ReusedMethod.ShowRedBorders(this@EditProfileActivity, mBinding.edtProvience)

                }

                override fun valid_state() {
                    ReusedMethod.displayMessageDialog(
                        this@EditProfileActivity,
                        "",
                        resources.getString(R.string.valid_state),
                        false,
                        "OK",
                        ""
                    )
                    ReusedMethod.ShowRedBorders(this@EditProfileActivity, mBinding.edtProvience)
                }

                override fun empty_postal_code() {
                    ReusedMethod.displayMessageDialog(
                        this@EditProfileActivity,
                        "",
                        resources.getString(R.string.valid_state),
                        false,
                        "OK",
                        ""
                    )
                    ReusedMethod.ShowRedBorders(this@EditProfileActivity, mBinding.edtProvience)
                }

                override fun valid_postal_code() {
                    ReusedMethod.displayMessageDialog(
                        this@EditProfileActivity,
                        "",
                        resources.getString(R.string.valid_state),
                        false,
                        "OK",
                        ""
                    )
                    ReusedMethod.ShowRedBorders(this@EditProfileActivity, mBinding.edtProvience)
                }


                override fun docValidations() {
                    ReusedMethod.displayMessageDialog(
                        this@EditProfileActivity,
                        "",
                        resources.getString(R.string.valid_doc),
                        false,
                        "OK",
                        ""
                    )
                }


                override fun success() {
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtFullname)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtEmail)
//                    ReusedMethod.displayMessage(
//                        this@EditProfileActivity,
//                        resources.getString(R.string.come_soon)
//                    )
                    callEditProfileApi()
                }

            }
        )
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
                profile_img,
                images,

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

}