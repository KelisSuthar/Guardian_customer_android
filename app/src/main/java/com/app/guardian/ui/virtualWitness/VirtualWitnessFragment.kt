package com.app.guardian.ui.virtualWitness

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.ReusedMethod.Companion.displayMessage
import com.app.guardian.common.ReusedMethod.Companion.selectDate
import com.app.guardian.common.ReusedMethod.Companion.selectTime
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.changeDateFormat
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.loadWebViewData
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentVirtualWitnessBinding
import com.app.guardian.model.SupportGroup.SupportGroupResp
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.utils.ApiConstant
import com.app.guardian.utils.Config
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*


class VirtualWitnessFragment(val resp: SupportGroupResp) : BaseFragment(),
    View.OnClickListener {
    private lateinit var mBinding: FragmentVirtualWitnessBinding
    private val mViewModel: CommonScreensViewModel by viewModel()
    override fun getInflateResource(): Int {
        return R.layout.fragment_virtual_witness
    }

    override fun initView() {
        mBinding = getBinding()
        (activity as HomeActivity).bottomTabVisibility(false)
        if (resp.toString().isNullOrEmpty()) {
            (activity as HomeActivity).headerTextVisible(
                resources.getString(R.string.virtual_witness),
                isHeaderVisible = true,
                isBackButtonVisible = true
            )
        } else {
            (activity as HomeActivity).headerTextVisible(
                resp.title.toString(),
                isHeaderVisible = true,
                isBackButtonVisible = true
            )
        }

    }

    override fun onResume() {
        super.onResume()
        showLoadingIndicator(true)
        Handler().postDelayed({
            showLoadingIndicator(false)
        }, 2000)

        mBinding.noDataVWitness.gone()
        mBinding.noInternetVWitness.llNointernet.gone()
        mBinding.cl1.visible()

        if (SharedPreferenceManager.getCMS() == null) {
            callCMSAPI()
        } else {
            mBinding.webView.loadWebViewData(SharedPreferenceManager.getCMS()!!.virtual_witness_content)
        }
    }

    private fun callCMSAPI() {
        if (ReusedMethod.isNetworkConnected(requireActivity())) {
            mViewModel.getCMSData(true, context as BaseActivity)
        } else {
            ReusedMethod.displayMessage(
                requireActivity(),
                resources.getString(R.string.text_error_network)
            )
        }
    }

    private fun callVirtualWitnessReqAPI(is_immediate_joining: Int? = 0, schedule_time: String) {
        if (ReusedMethod.isNetworkConnected(requireActivity())) {
            mViewModel.SendRequestVirtualWitness(
                true, context as BaseActivity, resp.id.toString(),
                is_immediate_joining!!, schedule_time
            )
            showLoadingIndicator(true)
        } else {
            showLoadingIndicator(true)
            ReusedMethod.displayMessage(
                requireActivity(),
                resources.getString(R.string.text_error_network)
            )
        }
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.btnRequest.setOnClickListener(this)
        mBinding.noInternetVWitness.btnTryAgain.setOnClickListener(this)
    }

    override fun initObserver() {
        //CMS DATA RESP
        mViewModel.getCMSResp().observe(this) { response ->
            response?.let { requestState ->
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            val gson = Gson()
                            val json = gson.toJson(data)
                            SharedPreferenceManager.putString(AppConstants.CMS_DETAIL, json)
                            mBinding.webView.loadWebViewData(SharedPreferenceManager.getCMS()!!.virtual_witness_content)

                            showLoadingIndicator(false)
                            mBinding.webView.webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView, url: String) {
                                    displayMessage(requireActivity(), "finished")
                                }
                            }


                        } else {
                            ReusedMethod.displayMessage(
                                requireActivity(),
                                it.message.toString()
                            )
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
        mViewModel.getSendRequestVirtualWitnessResp().observe(this) { response ->
            response.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            ReusedMethod.displayMessage(requireActivity(), it.message.toString())
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
            R.id.btnRequest -> {
                callScedualDialog()
            }
            R.id.btnTryAgain -> {
                onResume()
            }
        }
    }

    private fun callScedualDialog() {
        val dialog = ReusedMethod.setUpDialog(
            requireContext(),
            R.layout.virtual_witness_request_dialog,
            true
        )
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
            selectDate(requireActivity(), txtDate)
        }

        ivClose.setOnClickListener {
            dialog.dismiss()
        }
        cvScheduleTime.setOnClickListener {
            selectTime(requireActivity(), txtTime)
        }
        btnImmediateJoin.setOnClickListener {
            dialog.dismiss()
//            callVirtualWitnessReqAPI(0, getCurrentDate())
            ReusedMethod.displayMessage(requireActivity(), resources.getString(R.string.come_soon))
        }
        btnRequestSend.setOnClickListener {
            dialog.dismiss()
            if (resp.toString().isNullOrEmpty()) {
                callConformationDialog(
                    requireActivity(),
                    txtDate.text.toString(),
                    txtTime.text.toString(),
                    resources.getString(R.string.virtual_witness)
                )
            } else {
                callConformationDialog(
                    requireActivity(),
                    txtDate.text.toString(),
                    txtTime.text.toString(),
                    resp.title.toString()
                )
            }

        }

        dialog.show()
    }

    fun callConformationDialog(
        context: Context,
        date: String,
        time: String,
        headder: String?,
    ) {
        val dialog = ReusedMethod.setUpDialog(
            context,
            R.layout.virtual_witness_request_confirmation_dialog,
            true
        )
        val ivClose: ImageView = dialog.findViewById(R.id.ivClose)
        val txtDate: TextView = dialog.findViewById(R.id.txtDate)
        val txtTime: TextView = dialog.findViewById(R.id.txtTime)
        val txtTitle: TextView = dialog.findViewById(R.id.txtTitle)
        val sub: AppCompatButton = dialog.findViewById(R.id.btnRequestSend)

        txtDate.text = date
        txtTime.text = time
        txtTitle.text = headder
        ivClose.setOnClickListener {
            dialog.dismiss()
        }
        val time = changeDateFormat(
            "dd-MM-yyyy hh:mm a",
            "yyyy-MM-dd HH:mm:ss",
            (txtDate.text.toString() + " " + txtTime.text.toString())
        )
        sub.setOnClickListener {
            callVirtualWitnessReqAPI(schedule_time = time)
            dialog.dismiss()
        }
        dialog.show()
    }

}