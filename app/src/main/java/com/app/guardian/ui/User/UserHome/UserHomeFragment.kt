package com.app.guardian.ui.User.UserHome


import android.view.View
import androidx.core.content.ContextCompat
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentUserHomeBinding
import com.app.guardian.model.UserModels.HomeFrag.UserHomeBannerResp
import com.app.guardian.model.viewModels.AuthenticationViewModel
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.BannerAds.BannerAdsPager
import com.app.guardian.utils.Config
import org.koin.android.viewmodel.ext.android.viewModel


class UserHomeFragment : BaseFragment(), View.OnClickListener {
    private val mViewModel: CommonScreensViewModel by viewModel()
    private lateinit var mBinding: FragmentUserHomeBinding
    var array = ArrayList<UserHomeBannerResp>()
    var bannerAdsPager: BannerAdsPager? = null

    override fun getInflateResource(): Int {
        return R.layout.fragment_user_home
    }

    override fun initView() {
        mBinding = getBinding()
        setAdapter()
        callApi()
    }

    private fun callApi() {
        if (ReusedMethod.isNetworkConnected(requireActivity())) {
            mViewModel.getuserHomeBanners(true, requireActivity() as BaseActivity)
        } else {
            mBinding.noInternetUserHomeFrag.llNointernet.visible()
            mBinding.noDataUserHomeFrag.gone()
            mBinding.cl.gone()
        }
    }

    private fun setAdapter() {
        bannerAdsPager = BannerAdsPager(requireActivity(), array)
        mBinding.pager.adapter = bannerAdsPager
    }

    override fun postInit() {

    }

    override fun onResume() {
        super.onResume()
        mBinding.noInternetUserHomeFrag.llNointernet.gone()
        mBinding.noDataUserHomeFrag.gone()
        mBinding.cl.visible()
    }

    override fun handleListener() {
        mBinding.cvRecord.setOnClickListener(this)
        mBinding.cvScheduleVirtualWitness.setOnClickListener(this)
        mBinding.cvSupportService.setOnClickListener(this)
        mBinding.rbRecord.setOnClickListener(this)
        mBinding.rbScheduleVirtualWitness.setOnClickListener(this)
        mBinding.rbSupportService.setOnClickListener(this)
        mBinding.noInternetUserHomeFrag.btnTryAgain.setOnClickListener(this)
    }

    override fun initObserver() {
        mViewModel.getuserHomeBannerResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        SharedPreferenceManager.putBoolean(AppConstants.IS_SUBSCRIBE, true)

                        if (it.status) {
                            array.clear()
                            array.addAll(data)
                            bannerAdsPager?.notifyDataSetChanged()
                        } else {
                            mBinding.cl.gone()
                            mBinding.noDataUserHomeFrag.visible()
                            mBinding.noInternetUserHomeFrag.llNointernet.gone()
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
                                ?.let { ReusedMethod.displayMessage(requireActivity(), it) }
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cvRecord -> {
                changeLayout(1)
            }
            R.id.cvScheduleVirtualWitness -> {
                changeLayout(2)
            }
            R.id.cvSupportService -> {
                changeLayout(3)
            }
            R.id.rbRecord -> {
                changeLayout(1)
            }
            R.id.rbScheduleVirtualWitness -> {
                changeLayout(2)
            }
            R.id.rbSupportService -> {
                changeLayout(3)
            }
            R.id.btnTryAgain -> {
                callApi()
            }
        }
    }

    private fun changeLayout(i: Int) {

        when (i) {
            1 -> {
                mBinding.rlRecord.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
                mBinding.rlScheduleVirtualWitness.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlSupportService.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rbRecord.isChecked = true
                mBinding.rbScheduleVirtualWitness.isChecked = false
                mBinding.rbSupportService.isChecked = false


            }
            2 -> {
                mBinding.rlRecord.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlScheduleVirtualWitness.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
                mBinding.rlSupportService.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rbRecord.isChecked = false
                mBinding.rbScheduleVirtualWitness.isChecked = true
                mBinding.rbSupportService.isChecked = false
            }
            3 -> {
                mBinding.rlRecord.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlScheduleVirtualWitness.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlSupportService.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
                mBinding.rbRecord.isChecked = false
                mBinding.rbScheduleVirtualWitness.isChecked = false
                mBinding.rbSupportService.isChecked = true
            }

        }
    }
}