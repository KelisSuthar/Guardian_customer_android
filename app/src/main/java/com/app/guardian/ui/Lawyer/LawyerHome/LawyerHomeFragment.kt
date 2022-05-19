package com.app.guardian.ui.Lawyer.LawyerHome

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import com.app.guardian.R
import com.app.guardian.common.AppConstants
import com.app.guardian.common.ReplaceFragment
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentLawyerHomeBinding
import com.app.guardian.model.UserModels.HomeFrag.UserHomeBannerResp
import com.app.guardian.model.viewModels.CommonScreensViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.BannerAds.BannerAdsPager
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.LawyerList.LawyerListFragment
import com.app.guardian.ui.SeekLegalAdvice.SeekLegalAdviceListFragment
import com.app.guardian.utils.Config
import org.koin.android.viewmodel.ext.android.viewModel


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LawyerHomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LawyerHomeFragment : BaseFragment(), View.OnClickListener {
    private val mViewModel: CommonScreensViewModel by viewModel()
    var array = ArrayList<UserHomeBannerResp>()
    var bannerAdsPager: BannerAdsPager? = null
    lateinit var mBinding: FragmentLawyerHomeBinding
    override fun getInflateResource(): Int {
        return R.layout.fragment_lawyer_home
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
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.cvKnowRights.setOnClickListener(this)
        mBinding.cvAskQuestion.setOnClickListener(this)
        mBinding.cvSeekAdv.setOnClickListener(this)
        mBinding.btnKnowRights.setOnClickListener(this)
        mBinding.btnAskQuestions.setOnClickListener(this)
        mBinding.btnSeekAdv.setOnClickListener(this)
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
            R.id.cvKnowRights -> {
                chnagelayout(1)
            }
            R.id.cvAskQuestion -> {
                chnagelayout(2)
            }
            R.id.cvSeekAdv -> {
                chnagelayout(3)
                ReplaceFragment.replaceFragment(
                    requireActivity(),
                    SeekLegalAdviceListFragment(true,SharedPreferenceManager.getUser()!!.user.id!!),
                    false,
                    "",
                    HomeActivity::class.java.name
                )
            }
            R.id.btnKnowRights -> {
                chnagelayout(1)
            }
            R.id.btnAskQuestions -> {
                chnagelayout(2)
            }
            R.id.btnSeekAdv -> {
                ReplaceFragment.replaceFragment(
                    requireActivity(),
                    SeekLegalAdviceListFragment(true,SharedPreferenceManager.getUser()!!.user.id!!),
                    false,
                    "",
                    HomeActivity::class.java.name
                )
                chnagelayout(3)
            }
        }
    }

    private fun chnagelayout(i: Int) {
        when (i) {
            1 -> {
                mBinding.rlKnowRights.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
                mBinding.rlAskQuestions.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlSeekAdv.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.btnKnowRights.isChecked = true
                mBinding.btnAskQuestions.isChecked = false
                mBinding.btnSeekAdv.isChecked = false
                mBinding.btnKnowRights.buttonTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                mBinding.btnAskQuestions.buttonTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimaryDark
                        )
                    )
                mBinding.btnSeekAdv.buttonTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimaryDark
                        )
                    )
            }
            2 -> {
                mBinding.rlKnowRights.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlAskQuestions.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
                mBinding.rlSeekAdv.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.btnKnowRights.isChecked = false
                mBinding.btnAskQuestions.isChecked = true
                mBinding.btnSeekAdv.isChecked = false
                mBinding.btnKnowRights.buttonTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimaryDark
                        )
                    )
                mBinding.btnAskQuestions.buttonTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                mBinding.btnSeekAdv.buttonTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimaryDark
                        )
                    )
            }
            3 -> {
                mBinding.rlKnowRights.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlAskQuestions.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.lightBlue_2
                    )
                )
                mBinding.rlSeekAdv.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
                mBinding.btnKnowRights.isChecked = false
                mBinding.btnAskQuestions.isChecked = false
                mBinding.btnSeekAdv.isChecked = true
                mBinding.btnKnowRights.buttonTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimaryDark
                        )
                    )
                mBinding.btnAskQuestions.buttonTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimaryDark
                        )
                    )
                mBinding.btnSeekAdv.buttonTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
            }
        }
    }


}