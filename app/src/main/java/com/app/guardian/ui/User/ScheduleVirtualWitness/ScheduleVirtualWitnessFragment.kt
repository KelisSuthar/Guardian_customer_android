package com.app.guardian.ui.User.ScheduleVirtualWitness

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReplaceFragment
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.ReusedMethod.Companion.displayMessage
import com.app.guardian.common.ReusedMethod.Companion.getAddress
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.checkLoationPermission
import com.app.guardian.common.extentions.gone
import com.app.guardian.databinding.FragmentScheduleVirtualWitnessBinding
import com.app.guardian.model.HomeBanners.BannerCollection
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.BannerAds.BannerAdsPager
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.HomeBanners.HomeBannersFragment
import com.app.guardian.ui.User.ContactSupport.ContactSupportFragment
import com.app.guardian.utils.ApiConstant
import com.app.guardian.utils.Config
import com.google.android.gms.location.*
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ScheduleVirtualWitnessFragment : BaseFragment(), View.OnClickListener {
    private val mViewModel: CommonScreensViewModel by viewModel()
    lateinit var mBinding: FragmentScheduleVirtualWitnessBinding
    var array = ArrayList<BannerCollection>()
    var bannerArray = ArrayList<BannerCollection>()
    var bannerAdsPager: BannerAdsPager? = null
    var isMultiple: Boolean = false
    var isStopped: Boolean = false
    var current_add = ""
    private var locationManager: LocationManager? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    override fun getInflateResource(): Int {
        return R.layout.fragment_schedule_virtual_witness
    }

    override fun initView() {
        mBinding = getBinding()
        setFusedLoc()
        (activity as HomeActivity).bottomTabVisibility(false)
        (activity as HomeActivity).headerTextVisible(
            requireActivity().resources.getString(R.string.schedule_virtual_witness),
            true,
            true
        )
        checkPermissions(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            AppConstants.EXTRA_COURSE_PERMISSION
        )
        checkPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            AppConstants.EXTRA_FINE_PERMISSION
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

    private fun callApi() {
        if (ReusedMethod.isNetworkConnected(requireActivity())) {
            mViewModel.getuserHomeBanners(true, requireActivity() as BaseActivity)
        } else {
            displayMessage(requireActivity(), getString(R.string.text_error_network))
        }
    }

    private fun setAdapter() {
        bannerAdsPager = BannerAdsPager(requireActivity(), array, object
            : BannerAdsPager.onItemClicklisteners {
            override fun onItemClick(position: Int) {
                ReusedMethod.redirecttoUrl(requireContext(),array[position].url)
            }

        })
        mBinding.pager.adapter = bannerAdsPager
    }

    override fun postInit() {

    }

    override fun onResume() {
        super.onResume()
        changeLayout(SharedPreferenceManager.getInt(AppConstants.EXTRA_SH_SCHEDUAL_VIRTUAL_WITNESS,0))
        setAdapter()
        callApi()
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

    override fun handleListener() {
        mBinding.cvScheduleDateTime.setOnClickListener(this)
        mBinding.cvLocationWhereCallWillTakePlace.setOnClickListener(this)
        mBinding.cvScheduleMultipleCalls.setOnClickListener(this)
        mBinding.cvContactSupport.setOnClickListener(this)
        mBinding.rlContactSupport.setOnClickListener(this)
        mBinding.rlScheduleMultipleCalls.setOnClickListener(this)
        mBinding.rlLocationWhereCallWillTakePlace.setOnClickListener(this)
        mBinding.rlScheduleDateTime.setOnClickListener(this)
        mBinding.rbContactSupport.setOnClickListener(this)
        mBinding.rbScheduleDateTime.setOnClickListener(this)
        mBinding.rbLocationWhereCallWillTakePlace.setOnClickListener(this)
        mBinding.rbScheduleMultipleCalls.setOnClickListener(this)
        mBinding.txtViewMore.setOnClickListener(this)
    }

    override fun initObserver() {
        mViewModel.getuserHomeBannerResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->


                        if (it.status) {
                            array.clear()
                            array.addAll(data.top5)
                            bannerAdsPager?.notifyDataSetChanged()
                            bannerArray.clear()
                            bannerArray.addAll(data.bannerCollection)
                            if (data.bannerCollection.isNullOrEmpty()) {
                                mBinding.txtViewMore.gone()
                            }
                            if (array.size > 1) {
                                ReusedMethod.viewPagerScroll(mBinding.pager, array.size)
                            }
                        }


                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            displayMessage(
                                requireActivity(),
                                getString(R.string.text_error_network)
                            )

                        Config.CUSTOM_ERROR ->
                            errorObj.customMessage
                                ?.let {
                                    if (errorObj.code == ApiConstant.API_401) {
                                        displayMessage(requireActivity(), it)
                                        (activity as HomeActivity).unAuthorizedNavigation()
                                    } else {
                                        displayMessage(context as Activity, it)
                                    }
                                }
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cvScheduleDateTime -> {
                changeLayout(1)
                callScedualDialog()

            }
            R.id.cvLocationWhereCallWillTakePlace -> {
                changeLayout(2)
                locationCall(requireActivity())
            }
            R.id.cvScheduleMultipleCalls -> {
                changeLayout(3)
                callScedualDialog()
            }
            R.id.cvContactSupport -> {
                changeLayout(4)
                ReplaceFragment.replaceFragment(
                    requireActivity(),
                    ContactSupportFragment(),
                    true,
                    ScheduleVirtualWitnessFragment::class.java.name,
                    ScheduleVirtualWitnessFragment::class.java.name
                );
            }
            R.id.rlContactSupport -> {
                changeLayout(4)
                ReplaceFragment.replaceFragment(
                    requireActivity(),
                    ContactSupportFragment(),
                    true,
                    ScheduleVirtualWitnessFragment::class.java.name,
                    ScheduleVirtualWitnessFragment::class.java.name
                );
            }
            R.id.rlScheduleMultipleCalls -> {
                changeLayout(3)
                callScedualDialog()
                displayMessage(requireActivity(), "Coming Soon!")
            }
            R.id.rlLocationWhereCallWillTakePlace -> {
                changeLayout(2)
                locationCall(requireActivity())
            }
            R.id.rlScheduleDateTime -> {
                changeLayout(1)
                callScedualDialog()

            }
            R.id.rbContactSupport -> {
                changeLayout(4)
                ReplaceFragment.replaceFragment(
                    requireActivity(),
                    ContactSupportFragment(),
                    true,
                    ScheduleVirtualWitnessFragment::class.java.name,
                    ScheduleVirtualWitnessFragment::class.java.name
                );
            }
            R.id.rbScheduleDateTime -> {
                changeLayout(1)
                callScedualDialog()

            }
            R.id.rbLocationWhereCallWillTakePlace -> {
                changeLayout(2)
                locationCall(requireActivity())

            }
            R.id.rbScheduleMultipleCalls -> {
                changeLayout(3)

                callScedualDialog()

            }
            R.id.txtViewMore -> {
                ReplaceFragment.replaceFragment(
                    requireActivity(),
                    HomeBannersFragment(bannerArray),
                    true,
                    HomeActivity::class.java.name,
                    HomeActivity::class.java.name
                );
            }
        }
    }

    private fun changeLayout(i: Int) {
        SharedPreferenceManager.putInt(AppConstants.EXTRA_SH_SCHEDUAL_VIRTUAL_WITNESS,i)
        when (i) {
            0 -> {
                mBinding.rlScheduleDateTime.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlLocationWhereCallWillTakePlace.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlScheduleMultipleCalls.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlContactSupport.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rbScheduleDateTime.isChecked = false
                mBinding.rbLocationWhereCallWillTakePlace.isChecked = false
                mBinding.rbScheduleMultipleCalls.isChecked = false
                mBinding.rbContactSupport.isChecked = false
            }
            1 -> {
                isMultiple = false
                mBinding.rlScheduleDateTime.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
                mBinding.rlLocationWhereCallWillTakePlace.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlScheduleMultipleCalls.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlContactSupport.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rbScheduleDateTime.isChecked = true
                mBinding.rbLocationWhereCallWillTakePlace.isChecked = false
                mBinding.rbScheduleMultipleCalls.isChecked = false
                mBinding.rbContactSupport.isChecked = false
            }
            2 -> {
                mBinding.rlScheduleDateTime.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlLocationWhereCallWillTakePlace.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
                mBinding.rlScheduleMultipleCalls.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlContactSupport.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rbScheduleDateTime.isChecked = false
                mBinding.rbLocationWhereCallWillTakePlace.isChecked = true
                mBinding.rbScheduleMultipleCalls.isChecked = false
                mBinding.rbContactSupport.isChecked = false
            }
            3 -> {
                isMultiple = true
                mBinding.rlScheduleDateTime.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlLocationWhereCallWillTakePlace.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlScheduleMultipleCalls.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
                mBinding.rlContactSupport.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rbScheduleDateTime.isChecked = false
                mBinding.rbLocationWhereCallWillTakePlace.isChecked = false
                mBinding.rbScheduleMultipleCalls.isChecked = true
                mBinding.rbContactSupport.isChecked = false
            }
            4 -> {
                mBinding.rlScheduleDateTime.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlLocationWhereCallWillTakePlace.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlScheduleMultipleCalls.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlContactSupport.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
                mBinding.rbScheduleDateTime.isChecked = false
                mBinding.rbLocationWhereCallWillTakePlace.isChecked = false
                mBinding.rbScheduleMultipleCalls.isChecked = false
                mBinding.rbContactSupport.isChecked = true
            }
        }
    }

    fun callScedualDialog() {
        val dialog = Dialog(
            requireActivity(),
            com.google.android.material.R.style.Base_Theme_AppCompat_Light_Dialog_Alert
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.virtual_witness_request_dialog)
        dialog.setCancelable(true)

        val cvScheduleDate: MaterialCardView = dialog.findViewById(R.id.cvScheduleDate)
        val cvScheduleTime: MaterialCardView = dialog.findViewById(R.id.cvScheduleTime)
        val txtDate: TextView = dialog.findViewById(R.id.txtDate)
        val txtTime: TextView = dialog.findViewById(R.id.txtTime)
        val ivClose: ImageView = dialog.findViewById(R.id.ivClose)
        val btnImmediateJoin: Button = dialog.findViewById(R.id.btnImmediateJoin)
        val btnRequestSend: Button = dialog.findViewById(R.id.btnRequestSend)
        txtDate.text = SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().time)
        txtTime.text = SimpleDateFormat("hh:mm a").format(Calendar.getInstance().time)
        cvScheduleDate.setOnClickListener {
            ReusedMethod.selectDate(requireActivity(), txtDate)
        }

        ivClose.setOnClickListener {
            dialog.dismiss()
        }
        cvScheduleTime.setOnClickListener {
            ReusedMethod.selectTime(requireActivity(), txtTime)
        }
        btnImmediateJoin.setOnClickListener {
            dialog.dismiss()
            if (isMultiple) {
                displayMultipleCallConfirmationDialog(requireContext())
            } else {
                displayMessage(requireActivity(), resources.getString(R.string.come_soon))
            }

        }
        btnRequestSend.setOnClickListener {
            dialog.dismiss()
            callConformationDialog(
                requireActivity(),
                txtDate.text.toString(),
                txtTime.text.toString(),
                resources.getString(R.string.virtual_witness)

            )

        }
        dialog.show()
    }

    fun callConformationDialog(context: Context, date: String, time: String, headder: String?) {
        val dialog = Dialog(
            context,
            com.google.android.material.R.style.Base_Theme_AppCompat_Light_Dialog_Alert
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.virtual_witness_request_confirmation_dialog)
        dialog.setCancelable(true)

        val ivClose: ImageView = dialog.findViewById(R.id.ivClose)
        val txtDate: TextView = dialog.findViewById(R.id.txtDate)
        val txtTime: TextView = dialog.findViewById(R.id.txtTime)
        val txtDesc: TextView = dialog.findViewById(R.id.txtDesc)
        val txtTitle: TextView = dialog.findViewById(R.id.txtTitle)
        val sub: AppCompatButton = dialog.findViewById(R.id.btnRequestSend)

        txtDate.text = date
        txtTime.text = time
        txtTitle.text = headder
        ivClose.setOnClickListener {
            dialog.dismiss()
        }
        sub.setOnClickListener {
            dialog.dismiss()
            if (isMultiple) {
                displayMultipleCallConfirmationDialog(context)
            }
        }
        dialog.show()
    }

    private fun displayMultipleCallConfirmationDialog(context: Context) {
        val dialog = Dialog(
            context,
            com.google.android.material.R.style.Base_Theme_AppCompat_Light_Dialog_Alert
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialog_layout)
        dialog.setCancelable(false)

        val OK = dialog.findViewById<MaterialTextView>(R.id.tvPositive)
        val HEADDER = dialog.findViewById<MaterialTextView>(R.id.txtAppname)
        val TITLE = dialog.findViewById<TextView>(R.id.tvTitle)
        val MESSAGE = dialog.findViewById<TextView>(R.id.tvMessage)
        val CANCEL = dialog.findViewById<MaterialTextView>(R.id.tvNegative)
        TITLE.gone()
        MESSAGE.gone()
        HEADDER.text = resources.getString(R.string.want_to_add_call)
        CANCEL.setOnClickListener {
            dialog.dismiss()
        }
        OK.setOnClickListener {
            dialog.dismiss()
            if (isMultiple) {
                callScedualDialog()
            }

        }
        dialog.show()
    }

    private fun locationCall(context: Context) {


        val dialog = Dialog(
            context,
            com.google.android.material.R.style.Base_Theme_AppCompat_Light_Dialog_Alert
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialog_layout)
        dialog.setCancelable(false)

        val OK = dialog.findViewById<MaterialTextView>(R.id.tvPositive)
        val TITLE = dialog.findViewById<TextView>(R.id.tvTitle)
        val MESSAGE = dialog.findViewById<TextView>(R.id.tvMessage)
        val HEADDER = dialog.findViewById<TextView>(R.id.txtAppname)
        val CANCEL = dialog.findViewById<MaterialTextView>(R.id.tvNegative)
        HEADDER.gone()
        OK.text = "YES"
        CANCEL.text = "NO"
        MESSAGE.text = "Do you want to make call at this place ?"
        TITLE.text = current_add

        CANCEL.setOnClickListener {
            dialog.dismiss()
        }
        OK.setOnClickListener {
            dialog.dismiss()
            displayMessage(requireActivity(), resources.getString(R.string.come_soon))
        }
        dialog.show()
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
                        if (!isStopped) {
                            if (location != null) {
                                current_add =
                                    getAddress(
                                        requireContext(),
                                        location.latitude,
                                        location.longitude
                                    )[0].featureName + " ," + getAddress(
                                        requireContext(),
                                        location.latitude,
                                        location.longitude
                                    )[0].locality
                                Log.i("THIS_APP", current_add)


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

    override fun onDestroy() {
        super.onDestroy()
        isStopped = true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode ==
            AppConstants.EXTRA_FINE_PERMISSION || requestCode == AppConstants.EXTRA_COURSE_PERMISSION
        ) {
            getLatLong()
        } else {
            displayMessage(
                requireActivity(),
                " You have denied permission\nPlease accept permission"
            )
        }
    }

    private fun checkPermissions(permissions: String, reqcode: Int): Boolean {
        return if (ContextCompat.checkSelfPermission(
                requireContext(),
                permissions
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(permissions), reqcode)
            false
        } else {
            true
        }
    }

}
