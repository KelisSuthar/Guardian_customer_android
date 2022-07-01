package com.app.guardian.ui.LawyerVideoCallReq

import android.content.Intent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentLawyerVideoCallReqBinding
import com.app.guardian.model.GetVideoCallRequestResp.GetVideoCallRequestListResp
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.LawyerVideoCallReq.adapter.LawyerVideoCallReqAdapter
import com.app.guardian.ui.Login.LoginActivity
import com.app.guardian.ui.createorjoin.CreateOrJoinActivity
import com.app.guardian.utils.Config
import org.koin.android.viewmodel.ext.android.viewModel


class LawyerVideoCallReqFragment : BaseFragment(), View.OnClickListener {
    private lateinit var mBinding: FragmentLawyerVideoCallReqBinding
    private val mViewModel: CommonScreensViewModel by viewModel()
    val array = ArrayList<GetVideoCallRequestListResp>()
    var lawyerVideoCallReqAdapter: LawyerVideoCallReqAdapter? = null
    override fun getInflateResource(): Int {
        return R.layout.fragment_lawyer_video_call_req
    }

    override fun initView() {
        (activity as HomeActivity).bottomTabVisibility(false)
        (activity as HomeActivity).headerTextVisible(
            resources.getString(R.string.lawyer_video_req),
            isHeaderVisible = true,
            isBackButtonVisible = true
        )
        mBinding = getBinding()
        mBinding.searchConnectedHistory.lySearchFilter.gone()
        mBinding.searchConnectedHistory.edtLoginEmail.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                CallVieoCallReqListAPI(
                    mBinding.searchConnectedHistory.edtLoginEmail.text?.trim().toString()
                )
                return@OnEditorActionListener true
            }
            false
        })

    }

    override fun onResume() {
        super.onResume()
        mBinding.noDataLawyerVideoCallReq.gone()
        mBinding.noInternetLawyerVideoCallReq.llNointernet.gone()
        setAdapter()
        CallVieoCallReqListAPI("")
    }

    private fun setAdapter() {
        lawyerVideoCallReqAdapter = LawyerVideoCallReqAdapter(requireContext(),
            object : LawyerVideoCallReqAdapter.onItemClicklisteners {
                override fun onItemClick(position: Int?) {
//                    ReplaceFragment.replaceFragment(
//                        requireActivity(),
//                        LawyerProfileFragment.newInstance(selectedLawyerListId!!, isDialLawyerOpen),
//                        true,
//                        LawyerListFragment::class.java.name,
//                        LawyerListFragment::class.java.name
//                    )
//                    startActivity(
//                        Intent(context, CreateOrJoinActivity::class.java)
//                            .putExtra(
//                                AppConstants.EXTRA_CALLING_HISTORY_ID,
//                                array[position!!].id.toString()
//                            )
//                            .putExtra(
//                                AppConstants.EXTRA_TO_ID,
//                                array[position!!].from_id.toString()
//                            )
//                            .putExtra(
//                                AppConstants.EXTRA_TO_ROLE,
//                                array[position!!].from_role.toString()
//                            )
//                            .putExtra(
//                                AppConstants.EXTRA_NAME,
//                                array[position!!].user_detail.full_name.toString()
//                            )
//
//                    )
                }

            })
        mBinding.rvReqList.adapter = lawyerVideoCallReqAdapter
    }


    private fun CallVieoCallReqListAPI(search: String) {
        if (ReusedMethod.isNetworkConnected(requireContext())) {
            if (SharedPreferenceManager.getLoginUserRole() == AppConstants.APP_ROLE_USER) {
                mViewModel.GetVideoCallRequestUserList(
                    true,
                    requireActivity() as BaseActivity,
                    search
                )
            } else {
                mViewModel.GetVideoCallRequestMediatorList(
                    true,
                    requireActivity() as BaseActivity,
                    search
                )
            }

        } else {
            mBinding.noInternetLawyerVideoCallReq.llNointernet.visible()
            mBinding.noDataLawyerVideoCallReq.gone()
            mBinding.cl1.gone()
        }
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.noInternetLawyerVideoCallReq.btnTryAgain.setOnClickListener(this)
    }

    override fun initObserver() {
        mViewModel.getVideoCallRequestListResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        if (it.status) {
                            array.clear()
                            array.addAll(data)
                            mBinding.cl1.visible()
                            mBinding.noDataLawyerVideoCallReq.gone()
                            mBinding.noInternetLawyerVideoCallReq.llNointernet.gone()
                            if (data.isEmpty()) {
                                mBinding.cl1.gone()
                                mBinding.noDataLawyerVideoCallReq.visible()
                                mBinding.noInternetLawyerVideoCallReq.llNointernet.gone()
                            }
                            lawyerVideoCallReqAdapter?.updateAll(data)
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
            R.id.btnTryAgain -> {
                onResume()
            }
        }
    }

}