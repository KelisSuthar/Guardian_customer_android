package com.app.guardian.ui.User.RecordPoliceInteraction_2

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
import com.app.guardian.databinding.FragmentRecordPoliceInteraction2Binding
import com.app.guardian.model.HomeBanners.BannerCollection
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.BannerAds.BannerAdsPager
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.KnowRight.KnowRightFragment
import com.app.guardian.ui.User.LiveVirtualVitness.LiveVirtualVitnessUserFragment
import com.app.guardian.utils.ApiConstant
import com.app.guardian.utils.Config
import org.koin.android.viewmodel.ext.android.viewModel


class RecordPoliceInteraction_2Fragment : BaseFragment(), View.OnClickListener {
    lateinit var mBinding: FragmentRecordPoliceInteraction2Binding
    private val mViewModel: CommonScreensViewModel by viewModel()
    var array = ArrayList<BannerCollection>()
    var bannerAdsPager: BannerAdsPager? = null
    override fun getInflateResource(): Int {
        return R.layout.fragment_record_police_interaction_2
    }


    override fun initView() {
        mBinding = getBinding()
        setAdapter()
        callApi()
        (activity as HomeActivity).bottomTabVisibility(false)
        (activity as HomeActivity).headerTextVisible(
            requireActivity().resources.getString(R.string.record),
            true,
            true
        )
    }

    override fun postInit() {

    }

    override fun onResume() {
        super.onResume()

        changeLayout(SharedPreferenceManager.getInt(AppConstants.EXTRA_SH_RECORD_POLICE_INTERACTION_2,0))
    }

    private fun changeLayout(i: Int) {
        SharedPreferenceManager.putInt(AppConstants.EXTRA_SH_RECORD_POLICE_INTERACTION_2,i)
        when (i) {
            0 -> {
                mBinding.rlKnowBasicRight.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlLiveVirtualWitness.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rbKnowBasicRight.isChecked = false
                mBinding.rbLiveVirtualWitness.isChecked = false

            }
            1 -> {
                mBinding.rlKnowBasicRight.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
                mBinding.rlLiveVirtualWitness.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rbKnowBasicRight.isChecked = true
                mBinding.rbLiveVirtualWitness.isChecked = false


            }
            2 -> {
                mBinding.rlKnowBasicRight.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlLiveVirtualWitness.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
                mBinding.rbKnowBasicRight.isChecked = false
                mBinding.rbLiveVirtualWitness.isChecked = true
            }

        }
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
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(array[position].url))
                startActivity(browserIntent)
            }

        })
        mBinding.pager.adapter = bannerAdsPager
    }

    override fun handleListener() {
        mBinding.cvKnowBasicRight.setOnClickListener(this)
        mBinding.rlKnowBasicRight.setOnClickListener(this)
        mBinding.rbKnowBasicRight.setOnClickListener(this)
        mBinding.cvLiveVirtualWitness.setOnClickListener(this)
        mBinding.rlLiveVirtualWitness.setOnClickListener(this)
        mBinding.rbLiveVirtualWitness.setOnClickListener(this)

    }

    override fun initObserver() {
        mViewModel.getuserHomeBannerResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->


                        if (it.status) {
                            array.clear()
                            array.addAll(data.top5)
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
                                ?.let {    if (errorObj.code == ApiConstant.API_401) {
                                    ReusedMethod.displayMessage(requireActivity(), it)
                                    (activity as HomeActivity).unAuthorizedNavigation()
                                } else {
                                    ReusedMethod.displayMessage(context as Activity, it)
                                } }
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cvKnowBasicRight -> {
                changeLayout(1)
                ReplaceFragment.replaceFragment(
                    requireActivity(),
                    KnowRightFragment(),
                    true,
                    RecordPoliceInteraction_2Fragment::class.java.name,
                    RecordPoliceInteraction_2Fragment::class.java.name
                );
            }
            R.id.rlKnowBasicRight -> {
                mBinding.cvKnowBasicRight.performClick()
            }
            R.id.cvLiveVirtualWitness -> {
                changeLayout(2)
                ReplaceFragment.replaceFragment(
                    requireActivity(),
                    LiveVirtualVitnessUserFragment(),
                    true,
                    RecordPoliceInteraction_2Fragment::class.java.name,
                    RecordPoliceInteraction_2Fragment::class.java.name
                );
            }
            R.id.rlLiveVirtualWitness -> {
                mBinding.cvLiveVirtualWitness.performClick()
            }
            R.id.rbKnowBasicRight -> {
                mBinding.cvKnowBasicRight.performClick()
            }
            R.id.rbLiveVirtualWitness -> {
                mBinding.cvLiveVirtualWitness.performClick()
            }
        }
    }
}