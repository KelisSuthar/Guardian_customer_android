package com.app.guardian.ui.User.ScheduleVirtualWitness

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import com.app.guardian.R
import com.app.guardian.common.ReplaceFragment
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.ReusedMethod.Companion.displayMessage
import com.app.guardian.databinding.FragmentScheduleVirtualWitnessBinding
import com.app.guardian.model.HomeBanners.BannerCollection
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.BannerAds.BannerAdsPager
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.User.ContactSupport.ContactSupportFragment
import com.app.guardian.utils.ApiConstant
import com.app.guardian.utils.Config
import org.koin.android.viewmodel.ext.android.viewModel


class ScheduleVirtualWitnessFragment : BaseFragment(), View.OnClickListener {
    private val mViewModel: CommonScreensViewModel by viewModel()
    lateinit var mBinding: FragmentScheduleVirtualWitnessBinding
    var array = ArrayList<BannerCollection>()
    var bannerAdsPager: BannerAdsPager? = null
    override fun getInflateResource(): Int {
        return R.layout.fragment_schedule_virtual_witness
    }

    override fun initView() {
        mBinding = getBinding()
        (activity as HomeActivity).bottomTabVisibility(false)
        (activity as HomeActivity).headerTextVisible(
            requireActivity().resources.getString(R.string.schedule_virtual_witness),
            true,
            true
        )
        setAdapter()
        callApi()
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
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(array[position].url))
                startActivity(browserIntent)
            }

        })
        mBinding.pager.adapter = bannerAdsPager
    }

    override fun postInit() {

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
    }

    override fun initObserver() {
        mViewModel.getuserHomeBannerResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->


                        if (it.status) {
                            array.clear()
                            array.addAll(data.bannerCollection)
                            bannerAdsPager?.notifyDataSetChanged()
                            if (array.size > 1) {
                                ReusedMethod.viewPagerScroll(mBinding.pager, array.size)
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
                                        ReusedMethod.displayMessage(context as Activity, it)
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

            }
            R.id.cvLocationWhereCallWillTakePlace -> {

            }
            R.id.cvScheduleMultipleCalls -> {

            }
            R.id.cvContactSupport -> {
                ReplaceFragment.replaceFragment(
                    requireActivity(),
                    ContactSupportFragment(),
                    true,
                    ScheduleVirtualWitnessFragment::class.java.name,
                    ScheduleVirtualWitnessFragment::class.java.name
                );
            }
            R.id.rlContactSupport -> {
                mBinding.cvContactSupport.performClick()
            }
            R.id.rlScheduleMultipleCalls -> {}
            R.id.rlLocationWhereCallWillTakePlace -> {}
            R.id.rlScheduleDateTime -> {}
            R.id.rbContactSupport -> {
                mBinding.cvContactSupport.performClick()
            }
            R.id.rbScheduleDateTime -> {}
            R.id.rbLocationWhereCallWillTakePlace -> {}
            R.id.rbScheduleMultipleCalls -> {}
        }
    }


}