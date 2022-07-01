package com.app.guardian.ui.KnowRight

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.content.PermissionChecker
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.checkLoationPermission
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentKnowRightBinding
import com.app.guardian.model.KnowYourRights.KnowYourRightsResp
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.KnowRight.Adapter.KnowYourRightsAdapter
import com.app.guardian.utils.ApiConstant
import com.app.guardian.utils.Config
import com.google.android.gms.location.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*


class KnowRightFragment : BaseFragment(), View.OnClickListener {
    private val mViewModel: CommonScreensViewModel by viewModel()
    lateinit var mBinding: FragmentKnowRightBinding
    var knowYourRightsAdapter: KnowYourRightsAdapter? = null
    var array = ArrayList<KnowYourRightsResp>()

    private var locationManager: LocationManager? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    var CITY = ""
    var STATE = ""
    var COUNTRY = ""
    var LAT = ""
    var LONG = ""


    private val AUTOCOMPLETE_REQUEST_CODE = 1
    override fun getInflateResource(): Int {
        return R.layout.fragment_know_right
    }

    override fun initView() {
        mBinding = getBinding()
        setAdapter()
        setFusedLoc()

//        if (!Places.isInitialized()) {
//            Places.initialize(requireContext(), getString(R.string.map_api_key))
//        }
        (activity as HomeActivity).bottomTabVisibility(false)
        (activity as HomeActivity).headerTextVisible(
            requireActivity().resources.getString(R.string.know_your_basic_rights),
            true,
            true
        )

    }

    private fun setFusedLoc() {
        locationManager = requireActivity().getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationRequest = LocationRequest.create()
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest?.interval = 20 * 1000


    }

    override fun onResume() {
        super.onResume()
        if (!SharedPreferenceManager.getString(AppConstants.STATE, "").isNullOrEmpty()) {
            updatePlaceHolder()
        } else {
            getLatLong()
            if (checkLoationPermission(requireActivity())) {
                if (locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!) {

                    mFusedLocationClient?.requestLocationUpdates(
                        locationRequest!!,
                        locationCallback!!,
                        Looper.getMainLooper()
                    )
                } else {
                    ReusedMethod.setLocationDialog(requireActivity())
                }
            }
        }
        setAdapter()
        mBinding.NoInternetKnowYourRight.llNointernet.gone()
        mBinding.noDataKnowYourright.gone()
        mBinding.rvLawyerList.visible()
        mBinding.cl2.visible()

    }

    private fun getLatLong() {
        if (checkLoationPermission(requireActivity())) {
            showLoadingIndicator(true)
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

                            SharedPreferenceManager.putString(
                                AppConstants.EXTRA_LAT,
                                location.latitude.toString()
                            )
                            SharedPreferenceManager.putString(
                                AppConstants.EXTRA_LONG,
                                location.longitude.toString()
                            )
                            LAT = location.latitude.toString()
                            LONG = location.longitude.toString()
                            CITY = ReusedMethod.getAddress(
                                requireActivity(),
                                LAT!!.toDouble(),
                                LONG!!.toDouble()
                            )[0].locality.toString()
                            COUNTRY = ReusedMethod.getAddress(
                                requireActivity(),
                                LAT!!.toDouble(),
                                LONG!!.toDouble()
                            )[0].countryName.toString()
                            STATE = ReusedMethod.getAddress(
                                requireActivity(),
                                LAT!!.toDouble(),
                                LONG!!.toDouble()
                            )[0].adminArea.toString()
                            mBinding.txtLocation.text = "Location :$CITY,$STATE"
                            callApi(CITY, STATE)
                            if (mFusedLocationClient != null) {
                                mFusedLocationClient?.removeLocationUpdates(locationCallback!!)
                            }
                        }
                    }
                }
            }
        }
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
//                    displayMessage(this, "Please accept permission")
                }
            }
        }

    }

    private fun callApi(city: String, country: String) {
        if (ReusedMethod.isNetworkConnected(requireActivity())) {
//            mViewModel.getKnowRights(true, context as BaseActivity, CITY, COUNTRY)
            mViewModel.getKnowRights(true, context as BaseActivity, city, country)
        } else {
            mBinding.NoInternetKnowYourRight.llNointernet.visible()
            mBinding.noDataKnowYourright.gone()
            mBinding.rvLawyerList.gone()
            mBinding.cl2.visible()
            mBinding.edtSearch.setText("")
        }
    }

    private fun setAdapter() {
        mBinding.rvLawyerList.adapter = null
        knowYourRightsAdapter = KnowYourRightsAdapter(
            requireActivity(),
            array,
            object : KnowYourRightsAdapter.onItemClicklisteners {
                override fun onItemClick(position: Int) {
                    ReusedMethod.showKnowRightDialog(
                        requireContext(),
                        array[position].title,
                        array[position].code,
                        "Location: " + array[position].city + "," + array[position].state,
                        array[position].description
                    )

                    for (i in array.indices) {
                        array[i].is_selected = i == position
                    }
                    knowYourRightsAdapter?.notifyDataSetChanged()
                }
            })
        mBinding.rvLawyerList.adapter = knowYourRightsAdapter
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.edtSearch.setOnClickListener(this)
        mBinding.ivCancel.setOnClickListener(this)
    }

    override fun initObserver() {
        mViewModel.getKnowYourRightsResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            array.clear()
                            array.addAll(data)
                            knowYourRightsAdapter?.notifyDataSetChanged()
                            if (array.isNullOrEmpty()) {
                                mBinding.noDataKnowYourright.visible()
                                mBinding.NoInternetKnowYourRight.llNointernet.gone()
                                mBinding.rvLawyerList.gone()
                                mBinding.cl2.visible()
                            }
                        } else {
                            ReusedMethod.displayMessage(requireActivity(), it.message.toString())
                        }
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            ReusedMethod.displayMessage(
                                requireActivity(),
                                getString(R.string.text_error_network)
                            )

                        Config.CUSTOM_ERROR ->
                            errorObj.customMessage
                                ?.let {
                                    if (errorObj.code == ApiConstant.API_401) {
                                        ReusedMethod.displayMessage(requireActivity(), it)
                                        (activity as HomeActivity).unAuthorizedNavigation()
                                    } else {
                                        ReusedMethod.displayMessage(context as Activity, it)
                                    }
                                }
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.edtSearch -> {
                setGooglePlaceFragemnt()
            }
            R.id.ivCancel -> {
                setGooglePlaceFragemnt()
                mBinding.ivCancel.gone()
                mBinding.edtSearch.setText("")
                SharedPreferenceManager.clearCityState()
            }
        }
    }


    fun setGooglePlaceFragemnt() {
        val apiKey =
            getString(R.string.g_map_api_key_1) + getString(R.string.g_map_api_key_2) + getString(R.string.g_map_api_key_3)

        if (!Places.isInitialized()) {
            Places.initialize(requireActivity(), apiKey)
        }
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        val fields =
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(requireActivity())
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)

    }

    fun updatePlaceHolder() {
        callApi(
            SharedPreferenceManager.getString(AppConstants.CITY, "").toString(),
            SharedPreferenceManager.getString(AppConstants.STATE, "").toString()
        )
        mBinding.edtSearch.setText(
            SharedPreferenceManager.getString(AppConstants.CITY, "").toString() + " , " +
                    SharedPreferenceManager.getString(AppConstants.STATE, "").toString()
        )
        mBinding.txtLocation.text = "Location : " + mBinding.edtSearch.text
        Log.i("GUARDIAN", "SUCCESSFULL_CALL")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val places = Autocomplete.getPlaceFromIntent(data)
                        val lat_long = places.latLng


                        Log.i("ADDRESS", lat_long.toString())
                        getLOC(lat_long?.latitude!!, lat_long?.longitude)
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

                }
            }
            return
        }

    }

    private fun getLOC(MyLat: Double, MyLong: Double) {

        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(MyLat, MyLong, 10)
        Log.i("ADDRESS", addresses[0].toString())
        if (!addresses.isNullOrEmpty()) {
            if (addresses[0].locality == null) {
                SharedPreferenceManager.putString(
                    AppConstants.CITY,
                    addresses[0].adminArea.toString()
                )
                SharedPreferenceManager.putString(
                    AppConstants.STATE, addresses[0].adminArea.toString()
                )
//                mBinding.edtSearch.setText(addresses[0].adminArea.toString()+","+addresses[0].countryName.toString())
//                mBinding.txtLocation.setText(addresses[0].adminArea.toString()+","+addresses[0].countryName.toString())
            } else {
                mBinding.ivCancel.visible()
                SharedPreferenceManager.putString(
                    AppConstants.CITY,
                    addresses[0].locality.toString()
                )
                SharedPreferenceManager.putString(
                    AppConstants.STATE, addresses[0].adminArea.toString()
                )

//                mBinding.edtSearch.setText(addresses[0].locality.toString()+","+addresses[0].countryName.toString())
//                mBinding.txtLocation.setText(addresses[0].locality.toString()+","+addresses[0].countryName.toString())
            }

        }

    }
}


