package com.app.guardian.ui.signup

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import com.app.guardian.R
import com.app.guardian.common.IntegratorImpl
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.ReusedMethod.Companion.checkInputs
import com.app.guardian.common.ReusedMethod.Companion.displayMessage
import com.app.guardian.common.ReusedMethod.Companion.displayMessageDialog
import com.app.guardian.common.extentions.gone
import com.app.guardian.databinding.ActivitySignupScreenBinding
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.ui.AutoCompleteAdapter
import com.app.guardian.ui.signup.adapter.ImageAdapter
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class SignupScreen : BaseActivity(), View.OnClickListener {
    lateinit var mBinding: ActivitySignupScreenBinding
    var imageAdapter: ImageAdapter? = null
    var images = ArrayList<Bitmap>()
    var PROFILE_IMG_CODE = 101
    var DOCUMENT_CODE = 102
    var profile_img = ""
    var adapter: AutoCompleteAdapter? = null
    var responseView: TextView? = null
    var placesClient: PlacesClient? = null
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    var isAllEmpty = true
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return R.layout.activity_signup_screen
    }

    override fun initView() {
        mBinding = getBinding()
        setAdaper()
        initializeAutocompleteTextView()
        checkInputs(mBinding.edtFullname)
        checkInputs(mBinding.edtEmail)
        checkInputs(mBinding.edtMobileNum)


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
}

override fun onClick(p0: View?) {
    when (p0?.id) {
        R.id.edtProvience -> {
            val fields =
                listOf(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.ADDRESS,
                    Place.Field.LAT_LNG
                )
            val intent =
                Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }
        R.id.edtPostalCode -> {
            val fields =
                listOf(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.ADDRESS,
                    Place.Field.LAT_LNG
                )
            val intent =
                Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }
        R.id.ll1 -> {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, PROFILE_IMG_CODE)
        }

        R.id.ivAddImage -> {

            if (images.size < 5) {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(takePictureIntent, DOCUMENT_CODE)
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
//    IntegratorImpl.isValidSignUp()
}

private fun initializeAutocompleteTextView() {
    val apiKey = getString(R.string.map_api_key)
    if (apiKey.isEmpty()) {
        responseView!!.text = "error"
        return
    }

    // Setup Places Client

    // Setup Places Client
    if (!Places.isInitialized()) {
        Places.initialize(applicationContext, apiKey)
    }

    placesClient = Places.createClient(this)

    mBinding.edtProvience.threshold = 1
    mBinding.edtPostalCode.threshold = 1
    mBinding.edtProvience.onItemClickListener = autocompleteClickListener
    mBinding.edtPostalCode.onItemClickListener = autocompleteClickListener
    adapter = AutoCompleteAdapter(this, placesClient)
    mBinding.edtProvience.setAdapter(adapter)
    mBinding.edtPostalCode.setAdapter(adapter)


}

private val autocompleteClickListener =
    AdapterView.OnItemClickListener { adapterView, view, i, l ->
        try {
            val item: AutocompletePrediction? = adapter!!.getItem(i)
            var placeID: String? = null
            if (item != null) {
                placeID = item.placeId
            }

            //                To specify which data types to return, pass an array of Place.Fields in your FetchPlaceRequest
            //                Use only those fields which are required.
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


override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (resultCode == RESULT_OK) {
        if (requestCode == PROFILE_IMG_CODE) {
            val uri: Uri = data?.data!!
            mBinding.ivProfileImg.setImageURI(uri)


        } else if (requestCode == DOCUMENT_CODE) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val tempUri: Uri = getImageUri(applicationContext, imageBitmap)

            images.add(imageBitmap)
            imageAdapter?.notifyDataSetChanged()
//                imageAdapter?.notifyDataSetChanged()
            val finalFile = File(getRealPathFromURI(tempUri))


            if (images.size == 5) {
                mBinding.ivAddImage.gone()
            }
            Log.i(
                "THIS_APP",
                tempUri.toString() + "  " + images.size.toString() + "  " + finalFile.toString()
            )

        }
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {

            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val places = Autocomplete.getPlaceFromIntent(data)
                        val lat_long = places.latLng
                        getLOC(lat_long?.latitude!!, lat_long?.longitude!!)
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i("TAG_Place_Error", status.statusMessage.toString())
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
    }
}

private fun getLOC(MyLat: Double, MyLong: Double) {
    val geocoder = Geocoder(this, Locale.getDefault())
    val addresses: List<Address> = geocoder.getFromLocation(MyLat, MyLong, 100)

    mBinding.edtPostalCode.setText(addresses[0].subLocality)
    mBinding.edtProvience.setText(addresses[0].locality)

}

fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
    val bytes = ByteArrayOutputStream()
    inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
    val path: String =
        MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
    return Uri.parse(path)
}

fun getRealPathFromURI(uri: Uri?): String {
    var path = ""
    if (contentResolver != null) {
        val cursor: Cursor? = contentResolver.query(uri!!, null, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            path = cursor.getString(idx)
            cursor.close()
        }
    }
    return path
}
}