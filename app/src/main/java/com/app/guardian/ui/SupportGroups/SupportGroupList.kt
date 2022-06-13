package com.app.guardian.ui.SupportGroups

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReplaceFragment
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentNotificationBinding
import com.app.guardian.databinding.FragmentSupportGroupListBinding
import com.app.guardian.model.SupportGroup.SupportGroupResp
import com.app.guardian.model.viewModels.UserViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.LawyerList.LawyerListFragment
import com.app.guardian.ui.Login.LoginActivity
import com.app.guardian.ui.SupportGroups.adapter.SupportGroupListAdapter
import com.app.guardian.ui.chatting.ChattingFragment
import com.app.guardian.ui.virtualWitness.VirtualWitnessFragment
import com.app.guardian.utils.ApiConstant
import com.app.guardian.utils.Config
import org.koin.android.viewmodel.ext.android.viewModel


class SupportGroupList(val header: String?) : BaseFragment(), View.OnClickListener {
    lateinit var mBinding: FragmentSupportGroupListBinding
    private val mViewModel: UserViewModel by viewModel()
    var supportGroupListAdapter: SupportGroupListAdapter? = null
    var array = ArrayList<SupportGroupResp>()

    override fun getInflateResource(): Int {
        return R.layout.fragment_support_group_list

    }

    override fun initView() {
        mBinding = getBinding()
        (activity as HomeActivity).bottomTabVisibility(false)
        if (header != null) {
            (activity as HomeActivity).headerTextVisible(
                header!!,
                true,
                true
            )
        } else {
            (activity as HomeActivity).headerTextVisible(
                resources.getString(R.string.support_groups),
                true,
                true
            )
        }

    }

    override fun onResume() {
        super.onResume()
        mBinding.rvSupportGroupList.visible()
        mBinding.noDataSupportGroup.gone()
        mBinding.noInternetSupportGroup.llNointernet.gone()
        callAPI()
        setAdapter()
    }

    private fun setAdapter() {
        mBinding.rvSupportGroupList.adapter = null
        supportGroupListAdapter = SupportGroupListAdapter(
            requireActivity(),
            array,
            object : SupportGroupListAdapter.onItemClicklisteners {
                override fun onClick(position: Int) {
                    ReplaceFragment.replaceFragment(
                        requireActivity(),
                        VirtualWitnessFragment(array[position].title),
                        true,
                        SupportGroupList::class.java.name,
                        SupportGroupList::class.java.name
                    );
                }

            })
        mBinding.rvSupportGroupList.adapter = supportGroupListAdapter
    }

    private fun callAPI() {
        if (ReusedMethod.isNetworkConnected(requireContext())) {
            mViewModel.getSupportGroup(true, context as BaseActivity)
        } else {
            mBinding.rvSupportGroupList.gone()
            mBinding.noInternetSupportGroup.llNointernet.visible()
            mBinding.noDataSupportGroup.gone()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun postInit() {
        mViewModel.getSupportGroupResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            array.clear()
                            array.addAll(data)
                            supportGroupListAdapter?.notifyDataSetChanged()

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

    override fun handleListener() {
        mBinding.noInternetSupportGroup.btnTryAgain.setOnClickListener(this)
    }

    override fun initObserver() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnTryAgain -> {
                onResume()
            }
        }
    }


//    companion object {
//
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            SupportGroupList().apply {
//
//            }
//    }
}