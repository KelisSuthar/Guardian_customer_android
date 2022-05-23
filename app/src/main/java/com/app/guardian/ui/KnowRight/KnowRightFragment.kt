package com.app.guardian.ui.KnowRight

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentKnowRightBinding
import com.app.guardian.model.KnowYourRights.KnowYourRightsResp
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.AutoCompleteAdapter
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.KnowRight.Adapter.KnowYourRightsAdapter
import com.app.guardian.utils.Config
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*


class KnowRightFragment : BaseFragment(), View.OnClickListener {
    private val mViewModel: CommonScreensViewModel by viewModel()
    lateinit var mBinding: FragmentKnowRightBinding
    var knowYourRightsAdapter: KnowYourRightsAdapter? = null
    var array = ArrayList<KnowYourRightsResp>()

    var CITY = ""
    var STATE = ""
    var COUNTRY = ""
    var LAT = SharedPreferenceManager.getString(AppConstants.EXTRA_LAT, "")
    var LONG = SharedPreferenceManager.getString(AppConstants.EXTRA_LONG, "")


    private val AUTOCOMPLETE_REQUEST_CODE = 1
    override fun getInflateResource(): Int {
        return R.layout.fragment_know_right
    }

    override fun initView() {
        mBinding = getBinding()
        setAdapter()
        mBinding.headerKnowYourRight.tvHeaderText.text =
            requireActivity().getString(R.string.know_your_basic_rights)
        ReusedMethod.initializeAutocompleteTextView(
            requireActivity(),
            mBinding.searchKnowRight.edtLoginEmail
        )
//        if (!Places.isInitialized()) {
//            Places.initialize(requireContext(), getString(R.string.map_api_key))
//        }
        (activity as HomeActivity).bottomTabVisibility(true)
        (activity as HomeActivity).headerTextVisible(
            requireActivity().resources.getString(R.string.know_your_basic_rights),
            true,
            true
        )
    }

    override fun onResume() {
        super.onResume()
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
        callApi()
        setAdapter()
        mBinding.NoInternetKnowYourRight.llNointernet.gone()
        mBinding.noDataKnowYourright.gone()
        mBinding.cl1.visible()
        mBinding.searchKnowRight.edtLoginEmail.setText("")

    }

    private fun callApi() {
        if (ReusedMethod.isNetworkConnected(requireActivity())) {
//            mViewModel.getKnowRights(true, context as BaseActivity, CITY, COUNTRY)
            mViewModel.getKnowRights(true, context as BaseActivity, "Saskatoon", "Canada")
        } else {
            mBinding.NoInternetKnowYourRight.llNointernet.visible()
            mBinding.noDataKnowYourright.gone()
            mBinding.cl1.gone()
            mBinding.searchKnowRight.edtLoginEmail.setText("")
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
                }
            })
        mBinding.rvLawyerList.adapter = knowYourRightsAdapter
    }


    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.searchKnowRight.edtLoginEmail.setOnClickListener(this)
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
                            if(array.isNullOrEmpty()){
                                mBinding.noDataKnowYourright.visible()
                                mBinding.NoInternetKnowYourRight.llNointernet.gone()
                                mBinding.cl1.gone()
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
                                ?.let {}
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.edtLoginEmail -> {
//setGooglePlase()
//                val fields =
//                    listOf(
//                        Place.Field.ID,
//                        Place.Field.NAME,
//                        Place.Field.ADDRESS,
//                        Place.Field.LAT_LNG
//                    )
//                val intent =
//                    Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
//                        .build(requireActivity())
//                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
            }
        }
    }

//    private fun setGooglePlase() {
//
//
//        // Initialize Autocomplete Fragments
//        // from the main activity layout file
//        val autocompleteSupportFragment1 = childFragmentManager.findFragmentById(R.id.autocomplete_fragment1) as AutocompleteSupportFragment?
//
//        // Information that we wish to fetch after typing
//        // the location and clicking on one of the options
//        autocompleteSupportFragment1!!.setPlaceFields(
//            listOf(
//
//                Place.Field.NAME,
//                Place.Field.ADDRESS,
//                Place.Field.PHONE_NUMBER,
//                Place.Field.LAT_LNG,
//                Place.Field.OPENING_HOURS,
//                Place.Field.RATING,
//                Place.Field.USER_RATINGS_TOTAL
//
//            )
//        )
//
//        // Display the fetched information after clicking on one of the options
//        autocompleteSupportFragment1.setOnPlaceSelectedListener(object : PlaceSelectionListener {
//            override fun onPlaceSelected(place: Place) {
//
//                // Text view where we will
//                // append the information that we fetch
//
//
//                // Information about the place
//                val name = place.name
//                val address = place.address
//                val phone = place.phoneNumber.toString()
//                val latlng = place.latLng
//                val latitude = latlng?.latitude
//                val longitude = latlng?.longitude
//
//                val isOpenStatus : String = if(place.isOpen == true){
//                    "Open"
//                } else {
//                    "Closed"
//                }
//
//                val rating = place.rating
//                val userRatings = place.userRatingsTotal
//
//                Log.i( "THIS_APP", name.toString())
//                Log.i( "THIS_APP", address.toString())
//                Log.i( "THIS_APP",phone)
//                Log.i( "THIS_APP", latlng.toString())
//                Log.i( "THIS_APP", latitude.toString())
//                Log.i( "THIS_APP", longitude.toString())
//            }
//
//            override fun onError(p0: Status) {
//                ReusedMethod.displayMessage(requireActivity(),"Some error occurred")
//            }
//
//
//        })
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {

            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val places = Autocomplete.getPlaceFromIntent(data)
                        val lat_long = places.latLng
                        val city_state = ReusedMethod.getAddress(
                            requireActivity(),
                            lat_long!!.latitude, lat_long.longitude
                        )[0].locality + "," + ReusedMethod.getAddress(
                            requireActivity(),
                            lat_long.latitude,
                            lat_long.longitude
                        )[0].adminArea
                        mBinding.searchKnowRight.edtLoginEmail.setText(city_state)
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
}


