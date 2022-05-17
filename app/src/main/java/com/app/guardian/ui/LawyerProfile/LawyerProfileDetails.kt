package com.app.guardian.ui.LawyerProfile

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentLawyerProfileBinding
import com.app.guardian.model.viewModels.UserViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.utils.Config
import org.koin.android.viewmodel.ext.android.viewModel


class LawyerProfile(selectedLawyerListId: Int?) : BaseFragment() {

    private  lateinit var  mBinding: FragmentLawyerProfileBinding
    private val mViewModel: UserViewModel by viewModel()
    private var selectedLawyerListId: Int? = null


    private var rootView : View ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedLawyerListId = it.getInt("LawyerId")
        }
    }


    override fun getInflateResource(): Int {
        return R.layout.fragment_lawyer_profile
    }

    override fun initView() {
        mBinding = getBinding()
        callAPI()
    }

    override fun postInit() {
    }

    override fun handleListener() {
    }

    override fun initObserver() {
        mViewModel.lawyerProfileDetails().observe(this, Observer { response ->
            response.let {
                requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse.let {
                    it?.data?.let {
                        mBinding.tvLawyerName.text = it.full_name
                        mBinding.tvSpecialization.text = it.specialization
                        mBinding.txtSpecializationInfo.text = "Criminal Lawyer\t\t\t" +it.years_of_experience+"+ year Experiance"
                        mBinding.txtContactInfo.text = it.email

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
        Log.e("lawyerId","Selected lawyer list id : "+selectedLawyerListId.toString())

        if (ReusedMethod.isNetworkConnected(requireContext())) {
            mViewModel.getLawyerProfileDetails(true, context as BaseActivity,selectedLawyerListId!!)
        } else {
            mBinding.conLawyer.gone()
            mBinding.noLawyerProfile.gone()
            mBinding.noInternetLawyerProfile.llNointernet.visible()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(selectLawyerListIdParams: Int) =
            LawyerProfile(selectLawyerListIdParams).apply {
                arguments = Bundle().apply {
                    putInt("LawyerId", selectLawyerListIdParams)
                }
            }


    }
}