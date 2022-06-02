package com.app.guardian.ui.LawyerProfile

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.app.guardian.R
import com.app.guardian.common.ReplaceFragment
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentLawyerProfileBinding
import com.app.guardian.model.viewModels.UserViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.LawyerList.LawyerListFragment
import com.app.guardian.ui.SeekLegalAdvice.SeekLegalAdviceListFragment
import com.app.guardian.ui.chatting.ChattingFragment
import com.app.guardian.utils.Config
import org.koin.android.viewmodel.ext.android.viewModel


class LawyerProfileFragment(selectLawyerListIdParams: Int) : BaseFragment() {

    private lateinit var mBinding: FragmentLawyerProfileBinding
    private val mViewModel: UserViewModel by viewModel()
    private var selectedLawyerListId: Int? = null

    private var storeSelctedId: Int? = null
    private  var isDialLawyerSelected: Boolean = false
    private var strLawyerName : String ?= null
    private var strProfilePic : String ?= null
    private var strPhoneNumber : String ?= null
    private var strEmail : String ?= null
    override fun getInflateResource(): Int {
        return R.layout.fragment_lawyer_profile
    }

    override fun initView() {
        mBinding = getBinding()

        (activity as HomeActivity).bottomTabVisibility(false)
        (activity as HomeActivity).headerTextVisible(requireActivity().resources.getString(R.string.lawyer_profile),true,true)

        arguments?.let {
            selectedLawyerListId = it.getInt("LawyerId")
            isDialLawyerSelected = it.getBoolean("isDialLawyer")
            storeSelctedId = selectedLawyerListId
        }

        callAPI()
        mBinding.btnSeeLegal.setOnClickListener {

            ReplaceFragment.replaceFragment(
                requireActivity(),
                SeekLegalAdviceListFragment(false,storeSelctedId!!), true,
                LawyerProfileFragment::class.java.name,
                null
            )
        }

        if(isDialLawyerSelected){
            mBinding.imgRowLawyerChat.gone()
            mBinding.imgRowLawyerVideo.gone()
        }
        else {
            mBinding.imgRowLawyerChat.visible()
            mBinding.imgRowLawyerVideo.visible()
        }

        mBinding.imgRowLawyerCall.setOnClickListener {
            strLawyerName?.let {
                ReusedMethod.displayLawyerContactDetails(requireActivity(),
                    it, strEmail,strPhoneNumber,"")
            }
        }

        mBinding.imgRowLawyerChat.setOnClickListener {
            ReplaceFragment.replaceFragment(
                requireActivity(),
                ChattingFragment(selectedLawyerListId!!,strLawyerName!!,""),
                true,
                LawyerProfileFragment::class.java.name,
                LawyerProfileFragment::class.java.name
            )
        }

        mBinding.imgRowLawyerVideo.setOnClickListener{
            ReusedMethod.displayMessage(requireActivity(), requireActivity().resources.getString(R.string.come_soon))
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
                        mBinding.txtSpecializationInfo.text =
                            "Criminal Lawyer\t\t\t" + it.years_of_experience + "+ year Experiance"
                        mBinding.txtContactInfo.text = it.email
                        strLawyerName=it.full_name
                        //strProfilePic= it.profile_avatar.toString()
                        strEmail=it.email
                        strPhoneNumber=it.phone
                        //description data is null from api side
                        //mBinding.txtDescriptionInfo.text = it.
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
                                ?.let { ReusedMethod.displayMessage(context as Activity, it) }
                    }
                }
            }
        })
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
                    putBoolean("isDialLawyer",isDialLawyerOpen)
                }
            }
    }
}