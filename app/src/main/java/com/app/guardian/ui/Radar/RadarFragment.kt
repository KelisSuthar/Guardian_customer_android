package com.app.guardian.ui.Radar

import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.databinding.FragmentRadarBinding
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
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

class RadarFragment : BaseFragment(), View.OnClickListener, OnMapReadyCallback {
    lateinit var mBinding: FragmentRadarBinding
    var mMarker: MarkerOptions? = null
    private var gMap: GoogleMap? = null
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

//        val mSupportMapFragment =
//            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
//        mSupportMapFragment?.setListener {
//            mBinding.ns.requestDisallowInterceptTouchEvent(true)
//        }
        setG_MAP()
    }

    override fun postInit() {

    }

    override fun handleListener() {

    }

    override fun initObserver() {

    }

    override fun onClick(v: View?) {

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
        gMap = googleMap
        val dFormat = DecimalFormat("#.####")
        AppConstants.latitude = dFormat.format(AppConstants.latitude).toDouble()
        AppConstants.longitude = dFormat.format(AppConstants.longitude).toDouble()
//        itemLatitude = dFormat.format(itemLatitude).toDouble()
//        itemLongitude = dFormat.format(itemLongitude).toDouble()
        val latLng = LatLng(
            23.033863, 72.585022
        )
//        val latLng = LatLng(23.0225, 72.5714)
        mMarker = MarkerOptions()
        mMarker!!.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
            .anchor(0.0f, 1.0f)
            .title("Ahmedabad")
            .flat(true)
            .position(latLng)

        gMap!!.addMarker(mMarker!!)
        setMarkerZoom(23.033863, 72.585022)
//        setMarkerZoom(23.0225, 72.5714)

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
            gMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng!!, 18f))

        }
    }
}