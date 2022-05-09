package com.app.guardian.ui.signup

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import com.app.guardian.R
import com.app.guardian.common.*
import com.app.guardian.common.ReusedMethod.Companion.ShowNoBorders
import com.app.guardian.common.ReusedMethod.Companion.ShowRedBorders
import com.app.guardian.common.ReusedMethod.Companion.checkInputs
import com.app.guardian.common.ReusedMethod.Companion.displayMessageDialog
import com.app.guardian.common.ReusedMethod.Companion.isNetworkConnected
import com.app.guardian.common.extentions.checkLoationPermission
import com.app.guardian.common.extentions.checkPermissions
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.ActivitySignupScreenBinding
import com.app.guardian.model.viewModels.AuthenticationViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.ui.AutoCompleteAdapter
import com.app.guardian.ui.Login.LoginActivity
import com.app.guardian.ui.signup.adapter.ImageAdapter
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.ByteArrayOutputStream
import java.util.*


class SignupScreen : BaseActivity(), View.OnClickListener {
    private val mViewModel: AuthenticationViewModel by viewModel()
    lateinit var mBinding: ActivitySignupScreenBinding
    var imageAdapter: ImageAdapter? = null
    var images = ArrayList<String>()
    var PROFILE_IMG_CODE = 101
    var DOCUMENT_CODE = 102
    var profile_img = ""
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
        checkLoationPermission(this)

    }

    override fun onResume() {
        super.onResume()
        getLatLong()
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

    }

    override fun handleListener() {
        mBinding.ll1.setOnClickListener(this)
        mBinding.ivAddImage.setOnClickListener(this)
        mBinding.txtTermsAndConditions.setOnClickListener(this)
        mBinding.txtDoNotHaveAccount.setOnClickListener(this)
        mBinding.edtProvience.setOnClickListener(this)
        mBinding.edtPostalCode.setOnClickListener(this)
        mBinding.btnSigUp.setOnClickListener(this)
        mBinding.txtTermsAndConditions.setOnClickListener(this)
        mBinding.headderSignUp.ivBack.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
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
//                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                    startActivityForResult(takePictureIntent, DOCUMENT_CODE)
                } else {
                    displayMessageDialog(this, "", "Maximum Image Limit Reached", false, "Ok", "")
                }

            }
            R.id.txtTermsAndConditions -> {
                displayMessageDialog(this, "", "Coming Soon", false, "Ok", "")
            }
            R.id.btnSigUp -> {
                validations()
            }

        }
    }

    private fun validations() {
        IntegratorImpl.isValidSignUp(
            profile_img, mBinding.edtFullname.text?.trim().toString(),
            mBinding.edtEmail.text?.trim().toString(),
            mBinding.edtMobileNum.text?.trim().toString(),
            mBinding.edtPass.text?.trim().toString(),
            mBinding.edtConPass.text?.trim().toString(),
            mBinding.edtProvience.text?.trim().toString(),
            mBinding.edtPostalCode.text?.trim().toString(),
            mBinding.edtVehicalNum.text?.trim().toString(),
            images,
            object : ValidationView.SignUp {
                override fun profileImgValidations() {
                    displayMessageDialog(this@SignupScreen, "", resources.getString(R.string.profile_pic), false, "Cancel", "")
                }

                override fun fullname_empty() {
                    displayMessageDialog(this@SignupScreen, "", resources.getString(R.string.empty_name), false, "Cancel", "")
                    ShowRedBorders(this@SignupScreen, mBinding.edtFullname)
                }

                override fun fulllNameValidation() {
                    displayMessageDialog(this@SignupScreen, "", resources.getString(R.string.valid_name), false, "Cancel", "")
                    ShowRedBorders(this@SignupScreen, mBinding.edtFullname)
                }

                override fun email_empty() {
                    displayMessageDialog(this@SignupScreen, "", resources.getString(R.string.empty_email), false, "Cancel", "")
                    ShowRedBorders(this@SignupScreen, mBinding.edtEmail)
                }

                override fun emailValidation() {
                    displayMessageDialog(this@SignupScreen, "", resources.getString(R.string.valid_email), false, "Cancel", "")
                    ShowRedBorders(this@SignupScreen, mBinding.edtEmail)
                }

                override fun moNumber_empty() {
                    displayMessageDialog(this@SignupScreen, "", resources.getString(R.string.empty_number), false, "Cancel", "")
                    ShowRedBorders(this@SignupScreen, mBinding.edtMobileNum)
                }

                override fun moNumberValidation() {
                    displayMessageDialog(this@SignupScreen, "", resources.getString(R.string.valid_number), false, "Cancel", "")
                    ShowRedBorders(this@SignupScreen, mBinding.edtMobileNum)
                }

                override fun password_empty() {
                    displayMessageDialog(this@SignupScreen, "", resources.getString(R.string.empty_pass), false, "Cancel", "")
                    ShowRedBorders(this@SignupScreen, mBinding.edtPass)

                }

                override fun newpasswordMinValidation() {
                    displayMessageDialog(this@SignupScreen, "", resources.getString(R.string.valid_pass), false, "Cancel", "")
                    ShowRedBorders(this@SignupScreen, mBinding.edtPass)
                }

                override fun con_password_empty() {
                    displayMessageDialog(this@SignupScreen, "", resources.getString(R.string.empty_con_pass), false, "Cancel", "")
                    ShowRedBorders(this@SignupScreen, mBinding.edtConPass)
                }

                override fun conpasswordMinValidation() {
                    displayMessageDialog(this@SignupScreen, "", resources.getString(R.string.same_old_new_pass), false, "Cancel", "")
                    ShowRedBorders(this@SignupScreen, mBinding.edtConPass)
                }

                override fun passwordSpecialValidation() {
                    displayMessageDialog(this@SignupScreen, "", resources.getString(R.string.valid_pass), false, "Cancel", "")
                    ShowRedBorders(this@SignupScreen, mBinding.edtPass)

                }

                override fun confpasswordSpecialValidation() {
                    displayMessageDialog(this@SignupScreen, "", resources.getString(R.string.same_old_new_pass), false, "Cancel", "")
                    ShowRedBorders(this@SignupScreen, mBinding.edtConPass)
                }

                override fun matchPassowrds() {
                    displayMessageDialog(this@SignupScreen, "", resources.getString(R.string.same_old_new_pass), false, "Cancel", "")
                    ShowRedBorders(this@SignupScreen, mBinding.edtPass)
                    ShowRedBorders(this@SignupScreen, mBinding.edtConPass)
                }

                override fun empty_provience() {
                    TODO("Not yet implemented")
                }

                override fun valid_state() {
                    TODO("Not yet implemented")
                }

                override fun empty_postal_code() {
                    TODO("Not yet implemented")
                }

                override fun valid_postal_code() {
                    TODO("Not yet implemented")
                }

                override fun licencPlate_empty() {

                }

                override fun licencPlatevalidations() {
                    ShowRedBorders(this@SignupScreen, mBinding.edtVehicalNum)
                }

                override fun licencPlateLength() {
                    ShowRedBorders(this@SignupScreen, mBinding.edtVehicalNum)
                }

                override fun docValidations() {
                    //                    displayMessageDialog(this,"","","","","")
                }

                override fun success() {
                    ShowNoBorders(this@SignupScreen, mBinding.edtFullname)
                    ShowNoBorders(this@SignupScreen, mBinding.edtEmail)
                    ShowNoBorders(this@SignupScreen, mBinding.edtMobileNum)
                    ShowNoBorders(this@SignupScreen, mBinding.edtPass)
                    ShowNoBorders(this@SignupScreen, mBinding.edtConPass)
                    ShowNoBorders(this@SignupScreen, mBinding.edtVehicalNum)
                    callApi()
                }

            }
        )
    }

    private fun callApi() {
        if (isNetworkConnected(this)) {
            mViewModel.signUp(
                true,
                this,
                mBinding.edtFullname.text?.trim().toString(),
                mBinding.edtEmail.text?.trim().toString(),
                mBinding.edtPass.text?.trim().toString(),
                mBinding.edtConPass.text?.trim().toString(),
                mBinding.edtMobileNum.text?.trim().toString(),
//                mBinding.edtProvience.text?.trim().toString(),
                "CANADA",
                "123456",
//                mBinding.edtPostalCode.text?.trim().toString(),
                profile_img,
                images,
                "DEVICE@123"
            )
        } else {
            mBinding.noInternetSignUp.llNointernet.visible()
            mBinding.nsSignUp.gone()
            mBinding.noDataSignup.gone()
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

        mBinding.edtPostalCode.setText(addresses[0].subLocality)
        mBinding.edtProvience.setText(addresses[0].locality)

    }

    private fun getLatLong() {
        locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest?.interval = 20 * 1000
        if (checkLoationPermission(this)) {
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0!!)
                    if (p0.equals(null)) {
                        return
                    }
                    for (location in p0.locations) {
                        if (location != null) {
//                            SharedPreferenceManager.putString(
//                                AppConstants.EXTRA_LAT,
//                                location.latitude.toString()
//                            )
//                            SharedPreferenceManager.putString(
//                                AppConstants.EXTRA_LONG,
//                                location.longitude.toString()
//                            )

    getLOC(location.latitude,location.longitude)

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