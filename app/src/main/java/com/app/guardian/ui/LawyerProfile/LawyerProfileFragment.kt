package com.app.guardian.ui.LawyerProfile

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReplaceFragment
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.changeDateFormat
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentLawyerProfileBinding
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.model.viewModels.UserViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.SeekLegalAdvice.SeekLegalAdviceListFragment
import com.app.guardian.ui.chatting.ChattingFragment
import com.app.guardian.utils.ApiConstant
import com.app.guardian.utils.Config
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*


class LawyerProfileFragment(selectLawyerListIdParams: Int) : BaseFragment() {

    private lateinit var mBinding: FragmentLawyerProfileBinding
    private val mViewModel: UserViewModel by viewModel()
    private val commonViewModel: CommonScreensViewModel by viewModel()
    private var selectedLawyerListId: Int? = null
    var selected_laywer_id: Int? = -1

    private var storeSelctedId: Int? = null
    private var isDialLawyerSelected: Boolean = false
    private var strLawyerName: String? = null
    private var strProfilePic: String? = null
    private var strPhoneNumber: String? = null
    private var lastSeen: String? = null
    private var strEmail: String? = null
    override fun getInflateResource(): Int {
        return R.layout.fragment_lawyer_profile
    }

    override fun initView() {
        mBinding = getBinding()


        arguments?.let {
            selectedLawyerListId = it.getInt("LawyerId")
            isDialLawyerSelected = it.getBoolean("isDialLawyer")
            storeSelctedId = selectedLawyerListId
        }

        (activity as HomeActivity).bottomTabVisibility(false)

        (activity as HomeActivity).headerTextVisible(
            requireActivity().resources.getString(R.string.lawyer_profile),
            true,
            true
        )
        callAPI()
        mBinding.btnSeeLegal.setOnClickListener {

            ReplaceFragment.replaceFragment(
                requireActivity(),
                SeekLegalAdviceListFragment(false, storeSelctedId!!), true,
                LawyerProfileFragment::class.java.name,
                null
            )
        }

        if (isDialLawyerSelected) {
            mBinding.imgRowLawyerChat.gone()
            mBinding.imgRowLawyerVideo.gone()
        } else {
            mBinding.imgRowLawyerChat.visible()
            mBinding.imgRowLawyerVideo.visible()
        }

        mBinding.imgRowLawyerCall.setOnClickListener {
            strLawyerName?.let {
                ReusedMethod.displayLawyerContactDetails(
                    requireActivity(),
                    it, strEmail, strPhoneNumber, strProfilePic
                )
            }
        }

        mBinding.imgRowLawyerChat.setOnClickListener {
            ReplaceFragment.replaceFragment(
                requireActivity(),
                ChattingFragment(
                    selectedLawyerListId!!,
                ),
                true,
                LawyerProfileFragment::class.java.name,
                LawyerProfileFragment::class.java.name
            )
        }

        mBinding.imgRowLawyerVideo.setOnClickListener {
            if (SharedPreferenceManager.getLoginUserRole() == AppConstants.APP_ROLE_USER) {
                displayVideoCallDialog(selectedLawyerListId!!)
            } else {
                callVideoCallRequestAPI(
                    selectedLawyerListId!!,
                    AppConstants.APP_ROLE_LAWYER,
                    0,
                    ReusedMethod.getCurrentDate(),
                    0
                )
            }

//            ReusedMethod.displayMessage(requireActivity(), resources.getString(R.string.come_soon))
        }
    }

    override fun postInit() {
    }

    override fun handleListener() {

    }

    override fun initObserver() {
        mViewModel.lawyerProfileDetails().observe(this, Observer { response ->
            response.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse.let {
                    it?.data?.let {
                        mBinding.tvLawyerName.text = it.full_name
                        mBinding.tvSpecialization.text = it.specialization
                        if (it.years_of_experience!!.toInt() > 1) {
                            mBinding.txtSpecializationInfo.text =
                                it.specialization + "\t\t\t" + it.years_of_experience + "+ years Experience"
                        } else {
                            mBinding.txtSpecializationInfo.text =
                                it.specialization + "\t\t\t" + it.years_of_experience + "+ year Experience"
                        }
                        mBinding.txtContactInfo.text = "+" + it.phone
                        strLawyerName = it.full_name
                        strProfilePic = it.profile_avatar
                        lastSeen = it.last_seen
                        Glide.with(requireActivity())
                            .load(it.profile_avatar)
                            .placeholder(R.drawable.profile)
                            .into(mBinding.imgRoimgRowLawyerPicturewLawyerPicture)
                        strPhoneNumber = it.phone

                        if (!it.description.isNullOrEmpty()) {
                            mBinding.txtDescriptionInfo.text = it.description
                        }
                        if (!it.availability_time.isNullOrEmpty()) {
                            mBinding.txtAvaibilityInfo.text = it.availability_time
                        }
                    }
                }

                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            ReusedMethod.displayMessage(
                                context as Activity,
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
        })
        //SEND MEDIATOR CALLING REEQUEST RESP
        mViewModel.getCallMediatorReqResp().observe(this) { response ->
            response.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        ReusedMethod.displayMessage(requireActivity(), it.message.toString())
                        if (SharedPreferenceManager.getString(
                                AppConstants.USER_ROLE,
                                ""
                            ) == AppConstants.APP_ROLE_USER
                        ) {
                            selected_laywer_id?.let { it1 ->
//                                callVideoCallRequestAPI(
//                                    it1,
//                                    AppConstants.APP_ROLE_LAWYER,
//                                    data.is_immediate_joining,
//                                    data.request_datetime
//                                )
                            }
                        }

                    }
                }

                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            ReusedMethod.displayMessage(
                                context as Activity,
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

        //SEND Video CallReq
        commonViewModel.getSendVideoCallRequestResp().observe(this) { response ->
            response.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        ReusedMethod.displayMessage(requireActivity(), it.message.toString())
                    }
                }

                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            ReusedMethod.displayMessage(
                                context as Activity,
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

    private fun callAPI() {

        if (ReusedMethod.isNetworkConnected(requireContext())) {
            mViewModel.getLawyerProfileDetails(
                true,
                context as BaseActivity,
                selectedLawyerListId!!
            )
        } else {
            mBinding.conLawyer.gone()
            mBinding.noLawyerProfile.gone()
            mBinding.noInternetLawyerProfile.llNointernet.visible()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(selectLawyerListIdParams: Int, isDialLawyerOpen: Boolean) =
            LawyerProfileFragment(selectLawyerListIdParams).apply {
                arguments = Bundle().apply {
                    putInt("LawyerId", selectLawyerListIdParams)
                    putBoolean("isDialLawyer", isDialLawyerOpen)
                }
            }
    }

    fun displayVideoCallDialog(id: Int?) {
        selected_laywer_id = id!!
        val dialog = Dialog(
            requireContext(),
            com.google.android.material.R.style.Base_Theme_AppCompat_Light_Dialog_Alert
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(R.layout.dialog_do_you_need_mediator)
        dialog.setCancelable(true)

        val YES = dialog.findViewById<MaterialTextView>(R.id.txtDialogYes)
        val NO = dialog.findViewById<MaterialTextView>(R.id.txtDialogNo)

        YES.setOnClickListener {
            dialog.dismiss()
//            callRequestrMediatorApi()
            callScedualDialog()

//            startActivity(Intent(context,CreateOrJoinActivity::class.java))
        }

        NO.setOnClickListener {
            if (SharedPreferenceManager.getString(
                    AppConstants.USER_ROLE,
                    ""
                ) == AppConstants.APP_ROLE_USER
            ) {
                callVideoCallRequestAPI(
                    selected_laywer_id!!,
                    AppConstants.APP_ROLE_LAWYER,
                    0,
                    ReusedMethod.getCurrentDate(),
                    0
                )
            }
            dialog.dismiss()
        }
        dialog.show()
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
        val linerlayout_or: LinearLayout = dialog.findViewById(R.id.llor)
        val current_date = SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().time)

        txtDate.text = SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().time)
        txtTime.text = SimpleDateFormat("hh:mm a").format(Calendar.getInstance().time)
        btnImmediateJoin.gone()
        linerlayout_or.gone()
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
//            callRequestrMediatorApi(1, txtDate.text.toString() + " " + txtTime.text.toString())
            ReusedMethod.displayMessage(requireActivity(), resources.getString(R.string.come_soon))
        }
        btnRequestSend.setOnClickListener {

            if (current_date != txtDate.text.toString()) {
                dialog.dismiss()
//                callRequestrMediatorApi(
//                    0,
//                    txtDate.text.toString() + " " + txtTime.text.toString()
//                )
                callVideoCallRequestAPI(
                    selected_laywer_id!!,
                    AppConstants.APP_ROLE_LAWYER,
                    0,
                    txtDate.text.toString() + " " + txtTime.text.toString(),
                    1
                )
            } else {
                if (isValidTime(txtTime.text.toString())) {
                    dialog.dismiss()
//                    callRequestrMediatorApi(
//                        0,
//                        txtDate.text.toString() + " " + txtTime.text.toString()
//                    )
                    callVideoCallRequestAPI(
                        selected_laywer_id!!,
                        AppConstants.APP_ROLE_LAWYER,
                        0,
                        txtDate.text.toString() + " " + txtTime.text.toString(),
                        1
                    )
                } else {
                    ReusedMethod.displayMessage(
                        requireActivity(),
                        "Please select proper date and time"
                    )
                }
            }
        }

        dialog.show()
    }

    private fun isValidTime(time: String): Boolean {
        var days = 0
        var hrs = 0
        var min = 0
        val currentDate = ReusedMethod.getCurrentDate()
        val currentDateTime =
            changeDateFormat("yyyy-MM-dd HH:mm:ss", "hh:mm a", currentDate)

        val date2: Date =
            SimpleDateFormat("hh:mm a").parse("$time")
        val date1: Date =
            SimpleDateFormat("hh:mm a").parse(currentDateTime)
        val difference: Long = date2.time - date1.time

        days = ((difference / (1000 * 60 * 60 * 24)).toInt())
        hrs = (((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60)).toInt())
        min =
            ((difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hrs)) / (1000 * 60)).toInt();

        return hrs > 0 || min > 30
//        return hrs > 0 && min > 30
    }


    private fun callRequestrMediatorApi(is_imediate_join: Int, schedual_datetime: String) {
        if (ReusedMethod.isNetworkConnected(requireContext())) {
            mViewModel.sendCallingReqtoMediator(
                true, requireActivity() as BaseActivity,
                is_imediate_join, schedual_datetime
            )
        } else {
            ReusedMethod.displayMessage(
                requireActivity(),
                resources.getString(R.string.text_error_network)
            )
        }
    }

    private fun callVideoCallRequestAPI(
        selected_laywer_id: Int,
        role: String,
        isImmediateJoining: Int,
        requestDatetime: String,
        is_med_req: Int,
    ) {
        if (ReusedMethod.isNetworkConnected(requireContext())) {
            commonViewModel.sendVideoCallReq(
                true,
                requireActivity() as BaseActivity,
                selected_laywer_id,
                role,
                isImmediateJoining,
                requestDatetime,
                is_med_req
            )
        }
    }
}