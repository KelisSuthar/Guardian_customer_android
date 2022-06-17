package com.app.guardian.ui.Radar

import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.extentions.checkLoationPermission
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentRadarBinding
import com.app.guardian.model.Radar.RadarListResp
import com.app.guardian.model.viewModels.UserViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.utils.Config
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.textview.MaterialTextView
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.DecimalFormat
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RadarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class RadarFragment : BaseFragment(), View.OnClickListener, OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {
    private val mViewModel: UserViewModel by viewModel()
    lateinit var mBinding: FragmentRadarBinding
    var mMarker: MarkerOptions? = null
    private var gMap: GoogleMap? = null
    var array = ArrayList<RadarListResp>()

    var CURRENT_LAT = 0.0
    var CURRENT_LONG = 0.0

    private var locationManager: LocationManager? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    override fun getInflateResource(): Int {
        return R.layout.fragment_radar
    }

    override fun initView() {
        mBinding = getBinding()
        (activity as HomeActivity).bottomTabVisibility(true)
        (activity as HomeActivity).headerTextVisible(
            "",
            isHeaderVisible = false,
            isBackButtonVisible = false
        )
        Log.i("DISTANCE", distance(23.033863, 72.585022, 22.7788, 73.6143).toString())

        locationManager = requireContext().getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationRequest = LocationRequest.create()
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest?.interval = 20 * 1000
    }

    override fun onResume() {
        super.onResume()

        mBinding.cl1.visible()
        mBinding.noInternetRadar.llNointernet.gone()
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

    private fun deletePointApi(lat: String, long: String, type: String) {
        if (ReusedMethod.isNetworkConnected(requireActivity())) {
            mViewModel.deleteRadarPoint(true, context as BaseActivity, lat, long, type)
        } else {
            mBinding.cl1.gone()
            mBinding.noInternetRadar.llNointernet.visible()
        }
    }

    private fun addPointAPI(type: String) {
        if (ReusedMethod.isNetworkConnected(requireActivity())) {
            mViewModel.addRadarPoint(
                true, context as BaseActivity, CURRENT_LAT.toString(),
                CURRENT_LONG.toString(), type
            )
        } else {
            mBinding.cl1.gone()
            mBinding.noInternetRadar.llNointernet.visible()
        }
    }

    private fun CallMapListAPI() {
        if (ReusedMethod.isNetworkConnected(requireActivity())) {
            mViewModel.getRadarList(
                true, context as BaseActivity,
                CURRENT_LAT.toString(), CURRENT_LONG.toString()
            )
        } else {
            mBinding.cl1.gone()
            mBinding.noInternetRadar.llNointernet.visible()
        }
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.noInternetRadar.btnTryAgain.setOnClickListener(this)
        mBinding.cvPhotoRadar.setOnClickListener(this)
        mBinding.cvAccident.setOnClickListener(this)
    }

    override fun initObserver() {
        //GET RADAR LIST RESP
        mViewModel.getRadarListResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            if (data != null) {
                                array.addAll(data)
                                setG_MAP()
                            } else {
                                setG_MAP()
                            }
                        } else {
                            ReusedMethod.displayMessage(requireActivity(), it.message.toString())
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
                                    ?.let { ReusedMethod.displayMessage(requireActivity(), it) }
                        }
                    }
                }
            }
        }
        //DELETE POINT RESP
        mViewModel.deleteRadarPointResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            ReusedMethod.displayMessage(requireActivity(), it.message.toString())
                        } else {
                            ReusedMethod.displayMessage(requireActivity(), it.message.toString())
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
        //ADD POINT RESP
        mViewModel.addRadarPointResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            ReusedMethod.displayMessage(requireActivity(), it.message.toString())
                        } else {
                            ReusedMethod.displayMessage(requireActivity(), it.message.toString())
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
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnTryAgain -> {
                onResume()
            }
            R.id.cvPhotoRadar -> {
                addPointAPI(AppConstants.EXTRA_PHOTO_RADAR)
            }
            R.id.cvAccident -> {
                addPointAPI(AppConstants.EXTRA_ROAD_BLOCK)
            }
        }
    }

    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = (sin(deg2rad(lat1))
                * sin(deg2rad(lat2))
                + (cos(deg2rad(lat1))
                * cos(deg2rad(lat2))
                * cos(deg2rad(theta))))
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 96.5606 * 1.1515
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI

    }


    private fun setG_MAP() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        MapsInitializer.initialize(requireContext())
    }

    override fun onMapReady(googleMap: GoogleMap) {
        var mapIcon: BitmapDescriptor
        gMap = googleMap
        val dFormat = DecimalFormat("#.####")
        AppConstants.latitude = dFormat.format(AppConstants.latitude).toDouble()
        AppConstants.longitude = dFormat.format(AppConstants.longitude).toDouble()
//        itemLatitude = dFormat.format(itemLatitude).toDouble()
//        itemLongitude = dFormat.format(itemLongitude).toDouble()


        if (!array.isNullOrEmpty()) {

            for (i in array.indices) {
                mapIcon = if(array[i].type == AppConstants.EXTRA_PHOTO_RADAR) {
                    BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_photoradar)
                }else{
                    BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_roadblock)
                }
                mMarker = MarkerOptions()
                mMarker!!.icon(mapIcon)
                    .anchor(0.0f, 1.0f)
                    .title(array[i].place)
                    .flat(true)
                    .position(
                        LatLng(
                            array[i].lat?.toDouble() ?: 0.0,
                            array[i].lng?.toDouble() ?: 0.0
                        )
                    )

                gMap!!.addMarker(mMarker!!)
            }
        }
//        gMap!!.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
//            override fun getInfoContents(p0: Marker): View? {
//                val view =
//                    LayoutInflater.from(requireContext()).inflate(R.layout.custom_g_marker, null)
//
//                val close = view.findViewById<ImageView>(R.id.ivClose)
//                val TITLE = view.findViewById<TextView>(R.id.txtLoc)
//
//                TITLE.text = p0.title
//                close.setOnClickListener {
//                    ReusedMethod.displayMessage(requireActivity(), p0.title.toString())
//                }
//                return view
//            }
//
//            override fun getInfoWindow(p0: Marker): View? {
//                return null
//            }
//
//        })

        mMarker = MarkerOptions()
        mMarker!!.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
            .anchor(0.0f, 1.0f)
            .title(resources.getString(R.string.current_loc))
            .flat(true)
            .position(LatLng(CURRENT_LAT, CURRENT_LONG))

        gMap!!.addMarker(mMarker!!)
        setMarkerZoom(CURRENT_LAT, CURRENT_LONG)

        gMap!!.setOnInfoWindowClickListener { marker ->
//            ReusedMethod.displayMessage(requireActivity(), marker.title.toString())
        }

        gMap!!.setOnMarkerClickListener(this)

    }

    private fun setMarkerZoom(latitude: Double, longitude: Double) {
        val builder: LatLngBounds.Builder? = LatLngBounds.Builder()
        if (latitude != 0.0 && longitude != 0.0) {
            val latLng: LatLng? = LatLng(latitude, longitude)
            builder?.include(latLng!!)
            val bounds: LatLngBounds? = builder?.build()
            val width: Int = resources.displayMetrics.widthPixels
            val height: Int = resources.displayMetrics.heightPixels
            val padding: Int = (width * 0.15).toInt()
            // offset from edges of the map 12% of screen

            CameraUpdateFactory.newLatLngBounds(
                bounds!!,
                width,
                height,
                padding
            )
            gMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng!!, 14f))

        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        var type: String = ""
        for (i in array.indices) {
            if (marker.position.latitude.toString() == array[i].lat && marker.position.longitude.toString() == array[i].lng) {
                type = array[i].type.toString()
                break
            }
        }
        showInfoDialog(
            marker.title.toString(),
            marker.position.latitude.toString(),
            marker.position.longitude.toString(),
            type
        )
        return false
    }

    private fun showInfoDialog(title: String, lat: String, long: String, type: String) {
        val dialog = Dialog(
            requireActivity(),
            com.google.android.material.R.style.Base_Theme_AppCompat_Light_Dialog_Alert
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialog_layout)
        dialog.setCancelable(false)

        val OK = dialog.findViewById<MaterialTextView>(R.id.tvPositive)
        val TITLE = dialog.findViewById<TextView>(R.id.tvTitle)
        val MESSAGE = dialog.findViewById<TextView>(R.id.tvMessage)
        val APPNAME = dialog.findViewById<TextView>(R.id.txtAppname)
        val CANCEL = dialog.findViewById<MaterialTextView>(R.id.tvNegative)
        MESSAGE.gone()
        OK.text = "Yes"
        CANCEL.text = "No"

        APPNAME.text = title
        TITLE.text = "Are you sure want to delete this pin?"
        OK.setOnClickListener {
            deletePointApi(lat, long, type)
            dialog.dismiss()
        }
        CANCEL.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun getLatLong() {
        showLoadingIndicator(true)
        if (checkLoationPermission(requireActivity())) {

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
                            CURRENT_LAT = location.latitude
                            CURRENT_LONG = location.longitude
                            setG_MAP()
                            CallMapListAPI()
                            if (mFusedLocationClient != null) {
                                mFusedLocationClient?.removeLocationUpdates(locationCallback!!)
                            }
                        }
                    }
                }
            }
        } else {
            showLoadingIndicator(false)
        }
    }
}