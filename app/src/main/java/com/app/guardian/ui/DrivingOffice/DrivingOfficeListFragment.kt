package com.app.guardian.ui.DrivingOffice

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentDrivingOfficeListBinding
import com.app.guardian.model.viewModels.AuthenticationViewModel
import com.app.guardian.model.viewModels.UserViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.DrivingOffice.adapter.DrivingOffenceListAdapter
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.Login.LoginActivity
import com.app.guardian.utils.ApiConstant
import com.app.guardian.utils.Config
import org.koin.android.ext.android.get
import org.koin.android.viewmodel.ext.android.viewModel


class DrivingOfficeListFragment : BaseFragment(), View.OnClickListener {
    private val mViewModel: UserViewModel by viewModel()
    lateinit var mBinding: FragmentDrivingOfficeListBinding
    var drivingOffenceListAdapter: DrivingOffenceListAdapter? = null
    override fun getInflateResource(): Int {
        return R.layout.fragment_driving_office_list
    }

    override fun initView() {
        mBinding = getBinding()
        mBinding = getBinding()
        (activity as HomeActivity).headerTextVisible(
            requireActivity().resources.getString(R.string.driving_offence_list),
            isHeaderVisible = true,
            isBackButtonVisible = true
        )
        (activity as HomeActivity).bottomTabVisibility(false)
    }

    override fun onResume() {
        super.onResume()
        callAPI()
        setAdapter()
        mBinding.cl.gone()
    }

    private fun callAPI() {
        if (ReusedMethod.isNetworkConnected(requireContext())) {
            mViewModel.drivigOffnceList(true, requireActivity() as BaseActivity)
        } else {
            mBinding.noData.gone()
            mBinding.nointernet.llNointernet.visible()
            mBinding.rvSDrivingOfficeList.gone()
        }
    }

    private fun setAdapter() {
        mBinding.rvSDrivingOfficeList.adapter = null
        drivingOffenceListAdapter = DrivingOffenceListAdapter(requireContext(),
            object : DrivingOffenceListAdapter.onItemClicklisteners {
                override fun onItemClick(position: Int?) {
                ReusedMethod.displayMessage(requireActivity(),resources.getString(R.string.come_soon))
                }

            })
        mBinding.rvSDrivingOfficeList.adapter = drivingOffenceListAdapter
    }

    override fun postInit() {

    }

    override fun handleListener() {

    }

    override fun initObserver() {
        mViewModel.getDrivingOffenceResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            mBinding.cl.visible()
                            setAdapter()
                            drivingOffenceListAdapter?.updateAll(data)
                            if(!data.isNullOrEmpty())
                            {
                                mBinding.rvSDrivingOfficeList.visible()
                                mBinding.nointernet.llNointernet.gone()
                                mBinding.noData.gone()
                            }else{
                                mBinding.rvSDrivingOfficeList.gone()
                                mBinding.nointernet.llNointernet.gone()
                                mBinding.noData.visible()
                            }
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
                                        mBinding.rvSDrivingOfficeList.gone()
                                        mBinding.noData.visible()
                                        mBinding.nointernet.llNointernet.visible()
                                        ReusedMethod.displayMessage(context as Activity, it)
                                    }
                                }
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {

    }
}