package com.app.guardian.ui.User.LiveVirtualVitness

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.core.content.ContextCompat
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReplaceFragment
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentLiveVirtualVitnessUserBinding
import com.app.guardian.model.HomeBanners.BannerCollection
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.BannerAds.BannerAdsPager
import com.app.guardian.ui.DrivingOffice.DrivingOfficeListFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.HomeBanners.HomeBannersFragment
import com.app.guardian.ui.LawyerList.LawyerListFragment
import com.app.guardian.ui.User.MyVideos.MyVideosFragment
import com.app.guardian.utils.ApiConstant
import com.app.guardian.utils.Config
import org.koin.android.viewmodel.ext.android.viewModel


class LiveVirtualVitnessUserFragment : BaseFragment(), View.OnClickListener {
    lateinit var mBinding: FragmentLiveVirtualVitnessUserBinding
    private val mViewModel: CommonScreensViewModel by viewModel()
    var array = ArrayList<BannerCollection>()
    var bannerArray = ArrayList<BannerCollection>()
    var bannerAdsPager: BannerAdsPager? = null
    override fun getInflateResource(): Int {
        return R.layout.fragment_live_virtual_vitness_user
    }

    override fun onResume() {
        super.onResume()
        changeLayout(SharedPreferenceManager.getInt(AppConstants.EXTRA_SH_LIVE_VIRTUAL_WITNESS, 0))
    }

    override fun initView() {
        mBinding = getBinding()
        setAdapter()
        callApi()
        (activity as HomeActivity).bottomTabVisibility(false)
        (activity as HomeActivity).headerTextVisible(
            requireActivity().resources.getString(R.string.live_virtual_witness_mediator),
            true,
            true
        )
    }

    private fun callApi() {
        if (ReusedMethod.isNetworkConnected(requireActivity())) {
            mViewModel.getuserHomeBanners(true, requireActivity() as BaseActivity)
        } else {
            ReusedMethod.displayMessage(
                requireActivity(),
                resources.getString(R.string.text_error_network)
            )
        }
    }

    private fun setAdapter() {
        bannerAdsPager = BannerAdsPager(requireActivity(), array, object
            : BannerAdsPager.onItemClicklisteners {
            override fun onItemClick(position: Int) {
                ReusedMethod.redirectUrl(requireActivity(), array[position].url)
            }

        })
        mBinding.pager.adapter = bannerAdsPager
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.cvAccessYourRecording.setOnClickListener(this)
        mBinding.cvDrivingOffenceList.setOnClickListener(this)
        mBinding.cvDialLawyer.setOnClickListener(this)
        mBinding.rbDialLawyer.setOnClickListener(this)
        mBinding.rbDrivingOffenceList.setOnClickListener(this)
        mBinding.rbAccessYourRecording.setOnClickListener(this)
        mBinding.txtViewMore.setOnClickListener(this)
    }

    override fun initObserver() {
        mViewModel.getuserHomeBannerResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->


                        if (it.status) {
                            bannerArray.clear()
                            bannerArray.addAll(data.bannerCollection)
                            array.clear()
                            array.addAll(data.top5)
                            bannerAdsPager?.notifyDataSetChanged()
                            bannerArray.clear()
                            bannerArray.addAll(data.bannerCollection)
                            if (data.bannerCollection.isNullOrEmpty()) {
                                mBinding.txtViewMore.gone()
                            }
                            if (data.top5.isNullOrEmpty()) {
                                mBinding.cl1.gone()
                            } else {
                                mBinding.cl1.visible()
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
                                        ReusedMethod.displayMessage(requireActivity(), it)
                                    }
                                }
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cvAccessYourRecording -> {
                changeLayout(1)
                ReplaceFragment.replaceFragment(
                    requireActivity(),
                    MyVideosFragment(),
                    true,
                    LiveVirtualVitnessUserFragment::class.java.name,
                    LiveVirtualVitnessUserFragment::class.java.name
                )
//                ReusedMethod.displayMessage(
//                    requireActivity(),
//                    requireContext().resources.getString(R.string.come_soon)
//                )
            }
            R.id.cvDrivingOffenceList -> {
                changeLayout(2)
//                ReusedMethod.displayMessage(
//                    requireActivity(),
//                    requireContext().resources.getString(R.string.come_soon)
//                )
                ReplaceFragment.replaceFragment(
                    requireActivity(),
                    DrivingOfficeListFragment(),
                    true,
                    LiveVirtualVitnessUserFragment::class.java.name,
                    LiveVirtualVitnessUserFragment::class.java.name
                )
            }
            R.id.cvDialLawyer -> {
                mBinding.rbDialLawyer.performClick()
            }
            R.id.rbAccessYourRecording -> {
                mBinding.cvAccessYourRecording.performClick()
            }
            R.id.rbDrivingOffenceList -> {
                mBinding.cvDrivingOffenceList.performClick()

            }
            R.id.rbDialLawyer -> {
                changeLayout(3)
                ReplaceFragment.replaceFragment(
                    requireActivity(),
                    LawyerListFragment(true),
                    true,
                    "",
                    LiveVirtualVitnessUserFragment::class.java.name
                )

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
        SharedPreferenceManager.putInt(AppConstants.EXTRA_SH_LIVE_VIRTUAL_WITNESS, i)
        when (i) {
            0 -> {
                mBinding.rlAccessYourRecording.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlDrivingOffenceList.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlDialLawyer.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rbAccessYourRecording.isChecked = false
                mBinding.rbDrivingOffenceList.isChecked = false
                mBinding.rbDialLawyer.isChecked = false

            }
            1 -> {
                mBinding.rlAccessYourRecording.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
                mBinding.rlDrivingOffenceList.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlDialLawyer.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )

                mBinding.rbAccessYourRecording.isChecked = true
                mBinding.rbDrivingOffenceList.isChecked = false
                mBinding.rbDialLawyer.isChecked = false
            }
            2 -> {
                mBinding.rlAccessYourRecording.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlDrivingOffenceList.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
                mBinding.rlDialLawyer.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )

                mBinding.rbAccessYourRecording.isChecked = false
                mBinding.rbDrivingOffenceList.isChecked = true
                mBinding.rbDialLawyer.isChecked = false
            }
            3 -> {
                mBinding.rlAccessYourRecording.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlDrivingOffenceList.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlDialLawyer.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )

                mBinding.rbAccessYourRecording.isChecked = false
                mBinding.rbDrivingOffenceList.isChecked = false
                mBinding.rbDialLawyer.isChecked = true
            }

        }
    }


}