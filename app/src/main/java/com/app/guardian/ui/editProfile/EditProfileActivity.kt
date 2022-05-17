package com.app.guardian.ui.editProfile

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.core.content.PermissionChecker
import com.app.guardian.R
import com.app.guardian.common.*
import com.app.guardian.common.extentions.*
import com.app.guardian.databinding.ActivityEditProfileBinding
import com.app.guardian.model.Editprofile.UserDetailsResp
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.ui.signup.adapter.ImageAdapter
import com.app.guardian.utils.Config
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.*
import com.google.android.material.textview.MaterialTextView
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*


class EditProfileActivity : BaseActivity(), View.OnClickListener {
    private val mViewModel: CommonScreensViewModel by viewModel()
    lateinit var mBinding: ActivityEditProfileBinding
    var imageAdapter: ImageAdapter? = null
    var images = ArrayList<String>()
    var PROFILE_IMG_CODE = 101
    var DOCUMENT_CODE = 102
    var profile_img = ""
    var is_lawyer = false
    var is_mediator = false
    var is_user = false

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
        mBinding.ccp.setCountryForPhoneCode(1)
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
            Manifest.permission.CAMERA
        )
        checkPermissions(
            this,
            AppConstants.EXTRA_READ_PERMISSION,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        checkPermissions(
            this,
            AppConstants.EXTRA_WRITE_PERMISSION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
//        when {
//            SharedPreferenceManager.getString(
//                AppConstants.USER_ROLE,
//                AppConstants.APP_ROLE_USER
//            ) == AppConstants.APP_ROLE_USER -> {
//                is_user = true
//
//            }
//            SharedPreferenceManager.getString(
//                AppConstants.USER_ROLE,
//                AppConstants.APP_ROLE_USER
//            ) == AppConstants.APP_ROLE_LAWYER -> {
//                is_lawyer = true
//                mBinding.edtSpecializations.visible()
//                mBinding.fmOficeNum.visible()
//                mBinding.edtYearsOfExp.visible()
//                mBinding.edtVehicalNum.gone()
//            }
//            SharedPreferenceManager.getString(
//                AppConstants.USER_ROLE,
//                AppConstants.APP_ROLE_USER
//            ) == AppConstants.APP_ROLE_MEDIATOR -> {
//                is_mediator = true
//                mBinding.edtSpecializations.visible()
//                mBinding.edtYearsOfExp.visible()
//                mBinding.edtVehicalNum.gone()
//            }
//        }
//        //Check ROle
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

    private fun getLOC(MyLat: Double, MyLong: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(MyLat, MyLong, 100)

        mBinding.edtPostalCode.setText(addresses[0].postalCode)
        mBinding.edtProvience.setText(addresses[0].locality + "/" + addresses[0].adminArea)


    }

    override fun initObserver() {
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
                                ?.let { }
                    }
                }
            }
        }
    }

    private fun setApiData(data: UserDetailsResp) {
        mBinding.edtFullname.setText(data.full_name)
        mBinding.edtEmail.setText(data.email)
        mBinding.edtPhone.setText(data.phone)
        mBinding.edtProvience.setText(data.state)
        mBinding.edtPostalCode.setText(data.postal_code)
        mBinding.edtVehicalNum.setText(data.licence_no)

        if(data.profile_avatar != "null"|| data.profile_avatar!!.isNotEmpty()){
            profile_img = data.profile_avatar.toString()
            mBinding.ivProfileImg.loadImage(data.profile_avatar)
        }


        if(data.user_doc.isNotEmpty()){
            for(i in data.user_doc.indices){
                images.add(data.user_doc[i].document.toString())
            }

        }
    }

    override fun handleListener() {
        mBinding.ll1.setOnClickListener(this)
        mBinding.ivAddImage.setOnClickListener(this)
        mBinding.btnSubmit.setOnClickListener(this)

        mBinding.headderEdit.ivBack.setOnClickListener(this)
        mBinding.noInternetEdit.btnTryAgain.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnTryAgain -> {
//                callApi()
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

    private fun validations() {
        IntegratorImpl.isValidEdit(
            is_lawyer, is_mediator,
            profile_img, mBinding.edtFullname.text?.trim().toString(),

            mBinding.ccp.selectedCountryCode.toString() + mBinding.edtPhone.text?.trim()
                .toString(),
            mBinding.edtProvience.text?.trim().toString(),
            mBinding.edtPostalCode.text?.trim().toString(),
            mBinding.edtVehicalNum.text?.trim().toString(),
            images,
            object : ValidationView.EditProfile {
                override fun empty_profilePic() {
                    ReusedMethod.displayMessageDialog(
                        this@EditProfileActivity,
                        "",
                        resources.getString(R.string.empty_profile_pic),
                        false,
                        "Cancel",
                        ""
                    )
                }

                override fun fullname_empty() {
                    ReusedMethod.displayMessageDialog(
                        this@EditProfileActivity,
                        "",
                        resources.getString(R.string.empty_name),
                        false,
                        "Cancel",
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
                        "Cancel",
                        ""
                    )
                    ReusedMethod.ShowRedBorders(this@EditProfileActivity, mBinding.edtFullname)
                }

//                override fun email_empty() {
//                    ReusedMethod.displayMessageDialog(
//                        this@EditProfileActivity,
//                        "",
//                        resources.getString(R.string.empty_email),
//                        false,
//                        "Cancel",
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
                        "Cancel",
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
                        "Cancel",
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
                        "Cancel",
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
                        "Cancel",
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
                        "Cancel",
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
                        "Cancel",
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
                        "Cancel",
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
                        "Cancel",
                        ""
                    )
                }


                override fun success() {
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtFullname)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtEmail)
                    ReusedMethod.ShowNoBorders(this@EditProfileActivity, mBinding.edtVehicalNum)
//                    callApi()
                }

            }
        )
    }

    private fun setDialog() {
        val dialog = Dialog(
            this@EditProfileActivity,
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