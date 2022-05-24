package com.app.guardian.ui.Mediator.MediatorHome

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReplaceFragment
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentLawyerHomeBinding
import com.app.guardian.databinding.FragmentMediatorHomeBinding
import com.app.guardian.model.UserModels.HomeFrag.UserHomeBannerResp
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.BannerAds.BannerAdsPager
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.KnowRight.KnowRightFragment
import com.app.guardian.ui.LawyerList.LawyerListFragment
import com.app.guardian.utils.Config
import org.koin.android.viewmodel.ext.android.viewModel


class MediatorHomeFragment : BaseFragment(), View.OnClickListener {
    private val mViewModel: CommonScreensViewModel by viewModel()
    var array = ArrayList<UserHomeBannerResp>()
    var bannerAdsPager: BannerAdsPager? = null
    lateinit var mBinding: FragmentMediatorHomeBinding
    override fun getInflateResource(): Int {
        return R.layout.fragment_mediator_home
    }

    override fun initView() {
        mBinding = getBinding()
        (activity as HomeActivity).bottomTabVisibility(true)
        (activity as HomeActivity).headerTextVisible(requireActivity().resources.getString(R.string.lawyer_profile),false,false)

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
        bannerAdsPager = BannerAdsPager(requireActivity(), array, object
            : BannerAdsPager.onItemClicklisteners {
            override fun onItemClick(position: Int) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(array[position].url))
                startActivity(browserIntent)
            }

        })
        mBinding.pager.adapter = bannerAdsPager
    }

    override fun onResume() {
        super.onResume()
        mBinding.noInternetUserHomeFrag.llNointernet.gone()
        mBinding.noDataUserHomeFrag.gone()
        mBinding.cl.visible()

        chnagelayout(0)


    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.cvDialLawyer.setOnClickListener(this)
        mBinding.cvKnowBasicRight.setOnClickListener(this)
        mBinding.rbDialLawyer.setOnClickListener(this)
        mBinding.rbKnowBasicRight.setOnClickListener(this)

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
            R.id.cvKnowBasicRight -> {
                chnagelayout(1)
                ReplaceFragment.replaceFragment(requireActivity(),
                    KnowRightFragment(),true,"",MediatorHomeFragment::class.java.name)

            }
            R.id.cvDialLawyer -> {
                chnagelayout(2)
                ReplaceFragment.replaceFragment(requireActivity(),
                    LawyerListFragment(true),true,"",MediatorHomeFragment::class.java.name)

            }

            R.id.rbKnowBasicRight -> {
                chnagelayout(1)
               mBinding.cvKnowBasicRight.performClick()
            }
            R.id.rbDialLawyer -> {
                chnagelayout(2)
                mBinding.cvDialLawyer.performClick()
            }

        }
    }
    private fun chnagelayout(i: Int) {
        when (i) {
            1 -> {
                mBinding.rlKnowBasicRight.setBackgroundColor(
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
                mBinding.rbKnowBasicRight.isChecked = true
                mBinding.rbDialLawyer.isChecked = false

                mBinding.rbKnowBasicRight.buttonTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))

                mBinding.rbDialLawyer.buttonTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimaryDark
                        )
                    )
            }
            2 -> {
                mBinding.rlKnowBasicRight.setBackgroundColor(
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
                mBinding.rbKnowBasicRight.isChecked = false
                mBinding.rbDialLawyer.isChecked = true
                mBinding.rbKnowBasicRight.buttonTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))

                mBinding.rbDialLawyer.buttonTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
            }
            0->{
                mBinding.rlKnowBasicRight.setBackgroundColor(
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
                mBinding.rbKnowBasicRight.isChecked = false
                mBinding.rbDialLawyer.isChecked = false
                mBinding.rbKnowBasicRight.buttonTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))

                mBinding.rbDialLawyer.buttonTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimaryDark
                        )
                    )
            }

        }
    }

}