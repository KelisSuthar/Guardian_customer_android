package com.app.guardian.ui.User.RecordPolice

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.app.guardian.R
import com.app.guardian.common.ReplaceFragment
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentRecordPoliceInteractionBinding
import com.app.guardian.model.UserModels.HomeFrag.UserHomeBannerResp
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.BannerAds.BannerAdsPager
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.User.RecordPoliceInteraction_2.RecordPoliceInteraction_2Fragment
import com.app.guardian.utils.ApiConstant
import com.app.guardian.utils.Config
import org.koin.android.viewmodel.ext.android.viewModel


class RecordPoliceInteractionFragment : BaseFragment(), View.OnClickListener {
    lateinit var mBinding: FragmentRecordPoliceInteractionBinding
    private val mViewModel: CommonScreensViewModel by viewModel()
    var array = ArrayList<UserHomeBannerResp>()
    var bannerAdsPager: BannerAdsPager? = null
    override fun getInflateResource(): Int {
        return R.layout.fragment_record_police_interaction
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

    override fun onResume() {
        super.onResume()

        changeLayout(0)
    }

    private fun changeLayout(i: Int) {
        when (i) {
            0 -> {
                mBinding.rlVideoAndAudio.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlJustAudio.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rbVideoAndAudio.isChecked = false
                mBinding.rbJustAudio.isChecked = false

            }
            1 -> {
                mBinding.rlVideoAndAudio.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
                mBinding.rlJustAudio.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rbVideoAndAudio.isChecked = true
                mBinding.rbJustAudio.isChecked = false


            }
            2 -> {
                mBinding.rlVideoAndAudio.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlJustAudio.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
                mBinding.rbVideoAndAudio.isChecked = false
                mBinding.rbJustAudio.isChecked = true
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


    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.cvVideoAndAudio.setOnClickListener(this)
        mBinding.rlVideoAndAudio.setOnClickListener(this)
        mBinding.rbVideoAndAudio.setOnClickListener(this)
        mBinding.cvJustAudio.setOnClickListener(this)
        mBinding.rlJustAudio.setOnClickListener(this)
        mBinding.rbJustAudio.setOnClickListener(this)
    }

    override fun initObserver() {
        mViewModel.getuserHomeBannerResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->


                        if (it.status) {
                            array.clear()
                            array.addAll(data)
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
            R.id.cvVideoAndAudio -> {
                changeLayout(1)
                ReplaceFragment.replaceFragment(
                    requireActivity(),
                    RecordPoliceInteraction_2Fragment(),
                    true,
                    RecordPoliceInteractionFragment::class.java.name,
                    RecordPoliceInteractionFragment::class.java.name
                );
            }
            R.id.rlVideoAndAudio -> {
                mBinding.cvVideoAndAudio.performClick()
            }
            R.id.cvJustAudio -> {
                changeLayout(2)
                mBinding.cvVideoAndAudio.performClick()
            }
            R.id.rlJustAudio -> {
                changeLayout(2)
                mBinding.cvJustAudio.performClick()
            }
            R.id.rbJustAudio -> {
                changeLayout(1)
                mBinding.cvJustAudio.performClick()
            }
            R.id.rbVideoAndAudio -> {
                changeLayout(2)
                mBinding.cvVideoAndAudio.performClick()
            }
        }


    }

}